package dev.faststats;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.Set;
import java.util.WeakHashMap;

@ApiStatus.Internal
public non-sealed abstract class SimpleContext implements FastStatsContext {
    private final Set<ErrorTracker> errorTrackers = Collections.newSetFromMap(new WeakHashMap<>()); // todo: a set of weak references to error trackers; must also be thread safe
    private final Config config;
    private final @Token String token;
    private final SdkInfo sdkInfo;

    /**
     * Creates a new context that stores the shared configuration and token for all FastStats services.
     *
     * @param config the shared configuration
     * @param name   the name of the SDK
     * @param token  the FastStats project token
     * @throws IllegalArgumentException if the token is invalid
     * @throws IllegalArgumentException if the SDK information is invalid
     * @throws IllegalStateException    if the SDK information is incomplete or missing
     * @throws UncheckedIOException     if an IO error occurs
     * @since 0.24.0
     */
    protected SimpleContext(final Config config, final String name, @Token final String token) throws IllegalArgumentException {
        this.sdkInfo = constructSdkInfo(name);
        if (!token.matches(Token.PATTERN)) {
            throw new IllegalArgumentException("Invalid token '" + token + "', must match '" + Token.PATTERN + "'");
        }
        this.config = config;
        this.token = token;
    }

    private SdkInfo constructSdkInfo(final String name) throws UncheckedIOException, IllegalStateException, IllegalArgumentException {
        try (final var stream = getClass().getResourceAsStream("/META-INF/faststats.properties")) {
            if (stream == null) throw new IllegalStateException("Resource '/META-INF/faststats.properties' not found");

            final var properties = new Properties();
            properties.load(stream);

            final var version = properties.getProperty("version", null);
            if (version == null) throw new IllegalStateException("Missing 'version' in faststats.properties");

            final var buildId = properties.getProperty("build-id", null);

            return new SimpleSdkInfo(name, version, buildId);
        } catch (final IOException e) {
            throw new UncheckedIOException("Failed to read faststats.properties from META-INF", e);
        }
    }

    @Override
    @Contract(pure = true)
    public final Config getConfig() {
        return config;
    }

    @Override
    @Contract(pure = true)
    public final @Token String getToken() {
        return token;
    }

    @Override
    @Contract(value = " -> new", pure = true)
    public final FeatureFlagService featureFlags() {
        return new SimpleFeatureFlagService(config, token, null, Duration.ofMinutes(5));
    }

    @Override
    @Contract(value = "_ -> new", pure = true)
    public final FeatureFlagService featureFlags(final Attributes attributes) {
        return new SimpleFeatureFlagService(config, token, attributes, Duration.ofMinutes(5));
    }

    @Override
    @Contract(value = "_ -> new", pure = true)
    public final FeatureFlagService featureFlags(final Duration ttl) {
        return new SimpleFeatureFlagService(config, token, null, ttl);
    }

    @Override
    @Contract(value = "_, _ -> new", pure = true)
    public final FeatureFlagService featureFlags(@Nullable final Attributes attributes, final Duration ttl) {
        return new SimpleFeatureFlagService(config, token, attributes, ttl);
    }

    @Override
    @Contract(value = " -> new")
    public final ErrorTracker awareErrorTracker() {
        final var tracker = new SimpleErrorTracker(this);
        tracker.attachErrorContext(ErrorTracker.class.getClassLoader());
        errorTrackers.add(tracker);
        return tracker;
    }

    @Override
    @Contract(value = " -> new")
    public final ErrorTracker unawareErrorTracker() {
        final var tracker = new SimpleErrorTracker(this);
        errorTrackers.add(tracker);
        return tracker;
    }

    @Override
    @Contract(pure = true)
    public SdkInfo getSdkInfo() {
        return sdkInfo;
    }
    
    public Set<ErrorTracker> errorTrackers() {
        return errorTrackers;
    }
}
