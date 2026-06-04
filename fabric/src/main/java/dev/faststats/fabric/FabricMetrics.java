package dev.faststats.fabric;

import com.google.gson.JsonObject;
import dev.faststats.SimpleMetrics;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.SharedConstants;

abstract class FabricMetrics extends SimpleMetrics {
    protected final ModContainer mod;

    protected FabricMetrics(final Factory factory, final ModContainer mod) throws IllegalStateException {
        super(factory);
        this.mod = mod;
    }

    protected void appendFabricData(final JsonObject metrics, final String serverType) {
        metrics.addProperty("platform_version", SharedConstants.getCurrentVersion().id()); // todo: doublecheck
        metrics.addProperty("plugin_version", mod.getMetadata().getVersion().getFriendlyString());
        metrics.addProperty("server_type", serverType);
    }
}
