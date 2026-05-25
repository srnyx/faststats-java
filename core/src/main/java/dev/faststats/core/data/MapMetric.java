package dev.faststats.core.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;

final class MapMetric<T> extends SimpleMetric<Map<String, ? extends T>> {
    public MapMetric(@SourceId final String id, final Callable<? extends @Nullable Map<String, ? extends T>> callable) throws IllegalArgumentException {
        super(id, callable);
    }

    @Override
    public Optional<JsonElement> getData() throws Exception {
        return compute().map(data -> {
            final var object = new JsonObject();
            data.forEach((key, value) -> {
                final var entry = new JsonObject();
                if (value instanceof final Boolean bool) entry.addProperty(key, bool);
                else if (value instanceof final Number number) entry.addProperty(key, number);
                else entry.addProperty(key, value.toString());
                object.add(key, entry);
            });
            return object;
        });
    }
}
