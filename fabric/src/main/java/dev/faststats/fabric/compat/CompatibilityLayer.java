package dev.faststats.fabric.compat;

/**
 * Version-specific Fabric integration hooks.
 */
public interface CompatibilityLayer {
    void initServer();

    boolean clientOnlineMode();

    int clientPlayerCount();

    boolean serverOnlineMode();

    int serverPlayerCount();
}
