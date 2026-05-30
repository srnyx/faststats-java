package dev.faststats.fabric;

import dev.faststats.FastStatsContextFactory;
import dev.faststats.Metrics;
import dev.faststats.SimpleContext;
import dev.faststats.SimpleMetrics;
import dev.faststats.Token;
import dev.faststats.config.SimpleConfig;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import org.jetbrains.annotations.Contract;

/**
 * Fabric FastStats context.
 *
 * @since 0.24.0
 */
public final class FabricContext extends SimpleContext {
    final ModContainer mod;

    private FabricContext(final String modId, @Token final String token) {
        super(SimpleConfig.read(FabricLoader.getInstance().getConfigDir().resolve("faststats").resolve("config.properties")), "fabric", token);
        this.mod = FabricLoader.getInstance().getModContainer(modId).orElseThrow(() -> {
            return new IllegalArgumentException("Mod not found: " + modId);
        });
    }

    @Override
    @Contract(value = " -> new", pure = true)
    protected Metrics.Factory metricsFactory() {
        return new SimpleMetrics.Factory(this) {
            @Override
            public Metrics create() throws IllegalStateException {
                final var mod = ((FabricContext) context).mod;
                return switch (FabricLoader.getInstance().getEnvironmentType()) {
                    case CLIENT -> new FabricMetricsClientImpl(this, mod);
                    case SERVER -> new FabricMetricsServerImpl(this, mod);
                };
            }
        };
    }

    @Override
    public String getProjectName() {
        return mod.getMetadata().getId();
    }

    public static final class Factory extends FastStatsContextFactory<FabricContext, Factory> {
        private final String modId;
        private final @Token String token;

        public Factory(final String modId, @Token final String token) {
            this.modId = modId;
            this.token = token;
        }

        @Override
        protected FabricContext createContext() {
            return new FabricContext(modId, token);
        }
    }
}
