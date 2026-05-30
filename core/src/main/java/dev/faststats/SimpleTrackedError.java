package dev.faststats;

import org.jspecify.annotations.Nullable;

import java.util.Objects;

final class SimpleTrackedError implements TrackedError {
    private Attributes attributes = Attributes.create();
    private boolean handled = true;
    private final Throwable error;

    SimpleTrackedError(final Throwable error) {
        this.error = error;
    }

    @Override
    public Throwable error() {
        return error;
    }

    @Override
    public boolean handled() {
        return handled;
    }

    @Override
    public TrackedError handled(final boolean handled) {
        this.handled = handled;
        return this;
    }

    @Override
    public Attributes attributes() {
        return attributes;
    }

    @Override
    public TrackedError attributes(final Attributes attributes) {
        this.attributes = Attributes.copyOf(attributes);
        return this;
    }

    @Override
    public boolean equals(@Nullable final Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        final SimpleTrackedError that = (SimpleTrackedError) o;
        return handled == that.handled
                && Objects.equals(attributes, that.attributes)
                && Objects.equals(error, that.error);
    }

    @Override
    public int hashCode() {
        return Objects.hash(attributes, handled, error);
    }
}
