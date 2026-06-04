package dev.faststats.bungee;

import com.google.gson.JsonObject;
import dev.faststats.SimpleMetrics;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

final class BungeeMetricsImpl extends SimpleMetrics {
    private final ProxyServer server;
    private final Plugin plugin;

    BungeeMetricsImpl(final Factory factory, final Plugin plugin) throws IllegalStateException {
        super(factory);

        this.server = plugin.getProxy();
        this.plugin = plugin;
    }

    @Override
    protected void appendDefaultData(final JsonObject metrics) {
        metrics.addProperty("online_mode", server.getConfig().isOnlineMode());
        metrics.addProperty("platform_version", server.getVersion());
        metrics.addProperty("player_count", server.getOnlineCount());
        metrics.addProperty("plugin_version", plugin.getDescription().getVersion());
        metrics.addProperty("server_type", server.getName());
    }
}
