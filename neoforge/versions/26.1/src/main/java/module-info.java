import org.jspecify.annotations.NullMarked;

@NullMarked
module dev.faststats.neoforge.compat.v26_1 {
    requires dev.faststats.neoforge;

    requires static org.jspecify;

    provides dev.faststats.neoforge.compat.CompatibilityLayer with dev.faststats.neoforge.compat.v26_1.CompatibilityLayer_v26_1;
}
