package dev.faststats.fabric;

import com.google.gson.JsonObject;
import dev.faststats.SimpleMetrics;
import dev.faststats.config.SimpleConfig;
import net.fabricmc.loader.api.ModContainer;

abstract class FabricMetrics extends SimpleMetrics {
    protected final ModContainer mod;

    protected FabricMetrics(final Factory factory, final ModContainer mod) throws IllegalStateException {
        super(factory);
        this.mod = mod;
    }

    @Override
    protected boolean preSubmissionStart() {
        return ((SimpleConfig) context.getConfig()).preSubmissionStart();
    }

    protected void appendFabricData(final JsonObject metrics, final String serverType) {
        metrics.addProperty("plugin_version", mod.getMetadata().getVersion().getFriendlyString());
        metrics.addProperty("server_type", serverType);
    }
}
