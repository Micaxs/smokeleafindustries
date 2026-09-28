package net.micaxs.smokeleaf.component;

import org.jetbrains.annotations.Nullable;

/**
 * Implemented by items that carry default values for a {@link DataKey} (the 1.20.1 replacement for
 * {@code Item.Properties.component(...)} prototype components).
 */
public interface DefaultDataProvider {
    @Nullable
    Object getDefaultData(DataKey<?> key);
}
