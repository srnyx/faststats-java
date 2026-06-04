package dev.faststats.hytale;

import com.google.gson.JsonObject;
import com.hypixel.hytale.common.util.java.ManifestUtil;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.auth.ServerAuthManager;
import com.hypixel.hytale.server.core.universe.Universe;
import dev.faststats.SimpleMetrics;

final class HytaleMetricsImpl extends SimpleMetrics {
    HytaleMetricsImpl(final Factory factory) throws IllegalStateException {
        super(factory);
    }

    @Override
    protected void appendDefaultData(final JsonObject metrics) {
        metrics.addProperty("online_mode", ServerAuthManager.getInstance().getAuthMode() != ServerAuthManager.AuthMode.NONE);
        metrics.addProperty("platform_version", ManifestUtil.getVersion());
        metrics.addProperty("player_count", Universe.get().getPlayerCount());
        metrics.addProperty("server_type", HytaleServer.get().getServerName());
    }
}
