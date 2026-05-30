package dev.faststats.sponge;

import com.google.inject.Inject;
import dev.faststats.FastStatsContextFactory;
import dev.faststats.Metrics;
import dev.faststats.SimpleContext;
import dev.faststats.SimpleMetrics;
import dev.faststats.Token;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.Nullable;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.plugin.PluginContainer;

import java.nio.file.Path;

/**
 * Sponge FastStats context.
 *
 * @since 0.24.0
 */
public final class SpongeContext extends SimpleContext {
    final PluginContainer plugin;

    private SpongeContext(
            final PluginContainer plugin,
            @ConfigDir(sharedRoot = true) final Path dataDirectory,
            @Token final String token
    ) {
        super(SpongeConfig.read(plugin, dataDirectory.resolve("faststats").resolve("config.properties")), "sponge", token);
        this.plugin = plugin;
    }

    @Override
    @Contract(value = " -> new", pure = true)
    protected Metrics.Factory metricsFactory() {
        return new SimpleMetrics.Factory(this) {
            @Override
            public Metrics create() throws IllegalStateException {
                return new SpongeMetricsImpl(this);
            }
        };
    }

    @Override
    public String getProjectName() {
        return plugin.metadata().id();
    }

    /**
     * Injectable Sponge context builder.
     *
     * @since 0.24.0
     */
    public static class Factory extends FastStatsContextFactory<SpongeContext, Factory> {
        private final PluginContainer plugin;
        private final Path dataDirectory;
        private @Token
        @Nullable String token;

        /**
         * Creates a new Sponge context builder.
         *
         * @param plugin        the plugin container
         * @param dataDirectory the shared Sponge config directory
         * @apiNote This instance can be injected into your plugin.
         * @since 0.24.0
         */
        @Inject
        public Factory(
                final PluginContainer plugin,
                @ConfigDir(sharedRoot = true) final Path dataDirectory
        ) {
            this.plugin = plugin;
            this.dataDirectory = dataDirectory;
        }

        // todo: document
        public SpongeContext.Factory token(@Token final String token) throws IllegalArgumentException {
            this.token = token;
            return this;
        }

        @Override
        protected SpongeContext createContext() {
            if (token == null) throw new IllegalStateException("Token not configured");
            return new SpongeContext(plugin, dataDirectory, token);
        }
    }

    /**
     * Injectable Sponge context builder.
     *
     * @since 0.24.0
     */
    public static final class Builder extends Factory {
        @Inject
        public Builder(
                final PluginContainer plugin,
                @ConfigDir(sharedRoot = true) final Path dataDirectory
        ) {
            super(plugin, dataDirectory);
        }
    }
}
