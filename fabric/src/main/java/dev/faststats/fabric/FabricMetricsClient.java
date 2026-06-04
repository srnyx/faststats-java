package dev.faststats.fabric;

import com.google.gson.JsonObject;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.client.Minecraft;

final class FabricMetricsClient extends FabricMetrics {
    private final Minecraft client = Minecraft.getInstance();

    public FabricMetricsClient(final Factory factory, final ModContainer mod) throws IllegalStateException {
        super(factory, mod);
    }

    @Override
    protected void appendDefaultData(final JsonObject metrics) {
        metrics.addProperty("online_mode", client.getUser().getXuid().isPresent() && !client.isOfflineDeveloperMode()); // todo: doublecheck
        metrics.addProperty("player_count", getPlayerCount());
        appendFabricData(metrics, "Fabric Client");
    }

    private int getPlayerCount() {
        final var connection = client.getConnection();
        if (connection != null) return connection.getOnlinePlayers().size();

        final var server = client.getSingleplayerServer();
        if (server != null) return server.getPlayerCount();

        return client.player == null ? 0 : 1;
    }
}
