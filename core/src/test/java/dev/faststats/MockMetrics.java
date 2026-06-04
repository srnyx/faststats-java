package dev.faststats;

import com.google.gson.JsonObject;

final class MockMetrics extends SimpleMetrics {
    MockMetrics(final Factory factory) {
        super(factory);
    }

    @Override
    protected void appendDefaultData(final JsonObject metrics) {
    }
}
