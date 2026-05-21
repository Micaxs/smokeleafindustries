package net.micaxs.smokeleaf.compat.jei;

import net.micaxs.smokeleaf.component.ModDataComponentTypes;
import net.micaxs.smokeleaf.strain.StrainRegistry;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility for building colored ItemStack lists for JEI recipe display.
 * Each stack carries the STRAIN_DATA component for one of the preset strains,
 * so JEI slots cycle through nicely colored variants instead of the grayscale default.
 */
public final class JeiStrainHelper {

    private JeiStrainHelper() {}

    /** Returns one colored ItemStack per preset strain for the given item type. */
    public static List<ItemStack> coloredStacks(Item item) {
        return StrainRegistry.ids().stream()
                .sorted()
                .map(id -> StrainRegistry.get(id)
                        .map(data -> {
                            ItemStack stack = new ItemStack(item);
                            stack.set(ModDataComponentTypes.STRAIN_DATA.get(), data);
                            return stack;
                        })
                        .orElse(null))
                .filter(s -> s != null)
                .collect(Collectors.toList());
    }

    /**
     * Same as {@link #coloredStacks} but also sets the {@code DRY} component to {@code true}.
     * Used for dried-bud output slots in the dryer/drying-rack JEI display.
     */
    public static List<ItemStack> coloredDriedBudStacks(Item item) {
        return coloredStacks(item).stream()
                .map(s -> {
                    ItemStack dried = s.copy();
                    dried.set(ModDataComponentTypes.DRY.get(), Boolean.TRUE);
                    return dried;
                })
                .collect(Collectors.toList());
    }
}
