package dev.faststats;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class UserAgentProviderTest {
    @Test
    public void customUserAgentIsRetrievedFromSdkInfo() {
        final var context = new MockContext.Factory()
                .metrics(Metrics.Factory::create)
                .create();

        assertEquals(CustomUserAgentProvider.USER_AGENT, context.getSdkInfo().getUserAgent());
    }

    public static final class CustomUserAgentProvider implements SdkInfo.UserAgentProvider {
        static final String USER_AGENT = "Example Vendor Example SDK/1.2.3 https://example.com/support";
    
        @Override
        public String getUserAgent(final SdkInfo sdkInfo) {
            return USER_AGENT;
        }
    }
}
