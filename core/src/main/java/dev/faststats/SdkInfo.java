package dev.faststats;

import java.util.Optional;

public sealed interface SdkInfo permits SimpleSdkInfo {
    Optional<String> getBuildId();

    String getName();

    String getVersion();
}
