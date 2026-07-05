package dev.faststats.neoforge;

import com.google.gson.JsonObject;
import dev.faststats.neoforge.compat.CompatibilityLayer;
import net.neoforged.neoforgespi.language.IModInfo;

final class NeoForgeMetricsServer extends NeoForgeMetrics {
    private final CompatibilityLayer compatibilityLayer;

    NeoForgeMetricsServer(final Factory factory, final IModInfo mod, final CompatibilityLayer compatibilityLayer) throws IllegalStateException {
        super(factory, mod);
        this.compatibilityLayer = compatibilityLayer;
        compatibilityLayer.initServer();
    }

    @Override
    protected void appendDefaultData(final JsonObject metrics) {
        metrics.addProperty("online_mode", compatibilityLayer.serverOnlineMode());
        metrics.addProperty("player_count", compatibilityLayer.serverPlayerCount());
        appendNeoForgeData(metrics, "NeoForge");
    }
}
