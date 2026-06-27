package dev.faststats.neoforge.compat;

/**
 * Version-specific NeoForge integration hooks.
 */
public interface CompatibilityLayer {
    void initServer();

    boolean clientOnlineMode();

    int clientPlayerCount();

    boolean serverOnlineMode();

    int serverPlayerCount();
}
