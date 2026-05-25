package dev.faststats;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.jspecify.annotations.Nullable;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class SimpleErrorTracker implements ErrorTracker {
    private final Map<String, Report> reports = new ConcurrentHashMap<>();

    private final Map<Class<? extends Throwable>, Set<Pattern>> ignoredTypedPatterns = new ConcurrentHashMap<>();
    private final Set<Class<? extends Throwable>> ignoredTypes = new CopyOnWriteArraySet<>();
    private final Set<Pattern> ignoredPatterns = new CopyOnWriteArraySet<>();
    private final List<Map.Entry<Pattern, String>> anonymizationEntries = new CopyOnWriteArrayList<>(List.of(
            Map.entry(ErrorHelper.ipv4Pattern(), "[IP hidden]"),
            Map.entry(ErrorHelper.ipv6Pattern(), "[IP hidden]"),
            Map.entry(ErrorHelper.userHomePathPattern(), "$1$2$3[username hidden]"),
            Map.entry(ErrorHelper.discordWebhookPattern(), "$1[token hidden]"),
            Map.entry(ErrorHelper.jdbcUrlPattern(), "$1[password hidden]$2")
    ));

    private volatile @Nullable BiConsumer<@Nullable ClassLoader, Throwable> errorEvent = null;
    private volatile @Nullable UncaughtExceptionHandler originalHandler = null;

    private final FastStatsContext context;

    public SimpleErrorTracker(final FastStatsContext context) {
        this.context = context;
        ErrorHelper.usernamePattern().ifPresent(pattern -> anonymizationEntries.add(Map.entry(pattern, "[username hidden]")));
    }

    @Override
    public void trackError(final String message) {
        trackError(message, true);
    }

    @Override
    public void trackError(final Throwable error) {
        trackError(error, true);
    }

    @Override
    public void trackError(final String message, final boolean handled) {
        trackError(new RuntimeException(message), handled);
    }

    @Override
    public void trackError(final Throwable error, final boolean handled) {
        try {
            if (isIgnored(error, Collections.newSetFromMap(new IdentityHashMap<>()))) return;
            final var compiled = ErrorHelper.compile(error, null, handled, anonymizationEntries);
            final var identity = error.getClass().getName() + "@" + error.hashCode() + ":" + (handled ? 1 : 0);
            reports.compute(identity, (s, report) -> {
                if (report == null) return new Report(compiled);
                report.counter.incrementAndGet();
                return report;
            });
        } catch (final NoClassDefFoundError ignored) {
        }
    }

    private boolean isIgnored(@Nullable final Throwable error, final Set<Throwable> visited) {
        if (error == null || !visited.add(error)) return false;

        if (ignoredTypes.contains(error.getClass())) return true;

        final var message = error.getMessage() != null ? error.getMessage() : "";
        if (ignoredPatterns.stream().map(pattern -> pattern.matcher(message)).anyMatch(Matcher::find)) return true;

        final var patterns = ignoredTypedPatterns.get(error.getClass());
        if (patterns != null && patterns.stream().map(pattern -> pattern.matcher(message)).anyMatch(Matcher::find))
            return true;

        return isIgnored(error.getCause(), visited);
    }

    @Override
    public ErrorTracker ignoreError(final Class<? extends Throwable> type) {
        ignoredTypes.add(type);
        return this;
    }

    @Override
    public ErrorTracker ignoreError(final Pattern pattern) {
        ignoredPatterns.add(pattern);
        return this;
    }

    @Override
    public ErrorTracker ignoreError(final Class<? extends Throwable> type, final Pattern pattern) {
        ignoredTypedPatterns.computeIfAbsent(type, k -> new CopyOnWriteArraySet<>()).add(pattern);
        return this;
    }

    @Override
    public ErrorTracker anonymize(final Pattern pattern, final String replacement) {
        anonymizationEntries.add(Map.entry(pattern, replacement));
        return this;
    }

    public JsonArray getData() {
        final var report = new JsonArray(reports.size());

        reports.forEach((hash, object) -> {
            report.add(fillEntry(object.json, object.counter.get()));
        });

        return report;
    }

    private JsonObject fillEntry(final JsonObject entry, final int count) {
        context.getSdkInfo().getBuildId().ifPresent(id -> entry.addProperty("buildId", id));
        if (count > 1) entry.addProperty("count", count);
        return entry;
    }

    public void clear() {
        reports.clear();
    }

    @Override
    public synchronized void attachErrorContext(@Nullable final ClassLoader loader) throws IllegalStateException {
        if (originalHandler != null) throw new IllegalStateException("Error context already attached");
        originalHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler((thread, error) -> {
            final var handler = originalHandler;
            if (handler != null) handler.uncaughtException(thread, error);
            try {
                if (loader != null && !ErrorTracker.isSameLoader(loader, error)) return;
                final var event = errorEvent;
                if (event != null) event.accept(loader, error);
                trackError(error, false);
            } catch (final Throwable t) {
                trackError(t, false);
            }
        });
    }

    @Override
    public synchronized void detachErrorContext() {
        if (originalHandler == null) return;
        Thread.setDefaultUncaughtExceptionHandler(originalHandler);
        originalHandler = null;
    }

    @Override
    public synchronized boolean isContextAttached() {
        return originalHandler != null;
    }

    @Override
    public synchronized void setContextErrorHandler(@Nullable final BiConsumer<@Nullable ClassLoader, Throwable> errorEvent) {
        this.errorEvent = errorEvent;
    }

    @Override
    public synchronized Optional<BiConsumer<@Nullable ClassLoader, Throwable>> getContextErrorHandler() {
        return Optional.ofNullable(errorEvent);
    }

    private static class Report {
        private final JsonObject json;
        private final AtomicInteger counter = new AtomicInteger(1);

        private Report(final JsonObject json) {
            this.json = json;
        }
    }
}
