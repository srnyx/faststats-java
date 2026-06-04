package dev.faststats.fabric;

import com.google.gson.JsonObject;
import dev.faststats.SimpleMetrics;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

abstract class FabricMetrics extends SimpleMetrics {
    protected final ModContainer mod;

    protected FabricMetrics(final Factory factory, final ModContainer mod) throws IllegalStateException {
        super(factory);
        this.mod = mod;
    }

    protected void appendFabricData(final JsonObject metrics, final String serverType) {
        metrics.addProperty("minecraft_version", minecraftVersion());
        metrics.addProperty("plugin_version", mod.getMetadata().getVersion().getFriendlyString());
        metrics.addProperty("server_type", serverType);
    }

    protected static String minecraftVersion() {
        return FabricLoader.getInstance()
                .getModContainer("minecraft")
                .map(container -> container.getMetadata().getVersion().getFriendlyString())
                .orElse("unknown");
    }
}
