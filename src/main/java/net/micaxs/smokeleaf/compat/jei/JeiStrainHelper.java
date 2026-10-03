package net.micaxs.smokeleaf.compat.jei;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.recipe.IFocusGroup;
import net.micaxs.smokeleaf.component.ModDataComponentTypes;
import net.micaxs.smokeleaf.fluid.ModFluids;
import net.micaxs.smokeleaf.item.ModItems;
import net.micaxs.smokeleaf.strain.StrainData;
import net.micaxs.smokeleaf.strain.StrainRegistry;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
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
                            stack.set(ModDataComponentTypes.STRAIN_ID.get(), id);
                            return stack;
                        })
                        .orElse(null))
                .filter(s -> s != null)
                .collect(Collectors.toList());
    }

    /** A single strain-tinted FluidStack of {@code baseFluid} (Hash Oil or Unidentified Mixture) carrying that strain's StrainData/STRAIN_ID, the same way {@link #coloredStacks(Item)} tints items — both fluid types read this component dynamically for their tint color and display name. */
    public static FluidStack coloredFluidStack(Fluid baseFluid, String strainId, int amount) {
        FluidStack stack = new FluidStack(baseFluid, amount);
        StrainRegistry.get(strainId).ifPresent(data -> {
            stack.set(ModDataComponentTypes.STRAIN_DATA.get(), data);
            stack.set(ModDataComponentTypes.STRAIN_ID.get(), strainId);
        });
        return stack;
    }

    /** One strain-tinted FluidStack of {@code baseFluid} per preset strain, {@code amount} each. */
    public static List<FluidStack> coloredFluidStacks(Fluid baseFluid, int amount) {
        return StrainRegistry.ids().stream()
                .sorted()
                .map(id -> coloredFluidStack(baseFluid, id, amount))
                .collect(Collectors.toList());
    }

    /** Like {@link #coloredFluidStacks(Fluid, int)} but filtered/paired to the focused strain when relevant (e.g. so a recipe's oil-input slot matches whichever strain its seed-output slot is showing). */
    public static List<FluidStack> coloredFluidStacks(Fluid baseFluid, int amount, IFocusGroup focuses) {
        String id = focusedStrainId(focuses);
        if (id == null) return coloredFluidStacks(baseFluid, amount);
        return List.of(coloredFluidStack(baseFluid, id, amount));
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

    /** Returns true if the item is one of the generic strain items. */
    public static boolean isStrainItem(Item item) {
        return item == ModItems.GENERIC_BUD.get()
                || item == ModItems.GENERIC_WEED.get()
                || item == ModItems.GENERIC_EXTRACT.get()
                || item == ModItems.GENERIC_SEEDS.get()
                || item == ModItems.GENERIC_BAG.get()
                || item == ModItems.GENERIC_GUMMY.get()
                || item == ModItems.GENERIC_GUMMY_WORM.get();
    }

    /** Checks if an ingredient matches any generic strain item. */
    public static boolean isStrainIngredient(Ingredient ingredient) {
        for (ItemStack stack : ingredient.getItems()) {
            if (isStrainItem(stack.getItem())) return true;
        }
        return false;
    }

    /** Returns colored stacks for the strain item type used in this ingredient, or empty list if not a strain ingredient. */
    public static List<ItemStack> coloredStacksForIngredient(Ingredient ingredient) {
        for (ItemStack stack : ingredient.getItems()) {
            Item item = stack.getItem();
            if (isStrainItem(item)) {
                return coloredStacks(item);
            }
        }
        return List.of();
    }

    /**
     * Builds one output stack per preset strain for a recipe (Joint, Blunt) whose result blends
     * several weed inputs together via a {@code storeWeeds(ItemStack, List<ItemStack>)}-shaped
     * setter — using the same colored weed for every slot so each cycled output is a clean single-
     * strain example rather than a random cross-strain blend. Without this, JEI's output slot shows
     * a bare {@code new ItemStack(resultItem)} with no stored weeds, which renders exactly as grey
     * as the unidentified input items this whole helper exists to avoid.
     */
    public static List<ItemStack> coloredBlendOutputs(Item resultItem, int weedSlots,
                                                        BiConsumer<ItemStack, List<ItemStack>> storeWeeds,
                                                        IFocusGroup focuses) {
        List<ItemStack> outputs = new ArrayList<>();
        for (ItemStack weed : coloredStacks(ModItems.GENERIC_WEED.get(), focuses)) {
            ItemStack out = new ItemStack(resultItem);
            List<ItemStack> blend = new ArrayList<>();
            for (int i = 0; i < weedSlots; i++) blend.add(weed);
            storeWeeds.accept(out, blend);
            outputs.add(out);
        }
        return outputs;
    }

    /** Returns colored stacks for every generic strain item type (25 strains x 5 items). */
    public static List<ItemStack> allColoredStacks() {
        List<ItemStack> all = new ArrayList<>();
        all.addAll(coloredStacks(ModItems.GENERIC_SEEDS.get()));
        all.addAll(coloredStacks(ModItems.GENERIC_BUD.get()));
        all.addAll(coloredStacks(ModItems.GENERIC_WEED.get()));
        all.addAll(coloredStacks(ModItems.GENERIC_EXTRACT.get()));
        all.addAll(coloredStacks(ModItems.GENERIC_BAG.get()));
        all.addAll(coloredStacks(ModFluids.UNIDENTIFIED_MIXTURE_BUCKET.get()));
        return all;
    }

    /** Flagship preset used to color the single default JEI ingredient-list entry for each generic strain item. */
    public static final String DEFAULT_STRAIN = "og_kush";

    /** Returns the bare, un-strained generic item stacks JEI shows by default (grey, since their ItemColor handlers have nothing to tint). */
    public static List<ItemStack> unstrainedStacks() {
        return List.of(
                new ItemStack(ModItems.GENERIC_SEEDS.get()),
                new ItemStack(ModItems.GENERIC_BUD.get()),
                new ItemStack(ModItems.GENERIC_WEED.get()),
                new ItemStack(ModItems.GENERIC_EXTRACT.get()),
                new ItemStack(ModItems.GENERIC_BAG.get()),
                new ItemStack(ModItems.GENERIC_GUMMY.get()),
                new ItemStack(ModItems.GENERIC_GUMMY_WORM.get()),
                new ItemStack(ModFluids.UNIDENTIFIED_MIXTURE_BUCKET.get())
        );
    }

    /** {@link #unstrainedStacks()}, each tinted with {@link #DEFAULT_STRAIN} — meant to replace the bare stacks in JEI's ingredient list at runtime so the single representative entry per generic item reads as one clean color instead of flat grey. */
    public static List<ItemStack> defaultColoredStacks() {
        return unstrainedStacks().stream()
                .map(s -> {
                    ItemStack colored = s.copy();
                    StrainRegistry.get(DEFAULT_STRAIN).ifPresent(data -> {
                        colored.set(ModDataComponentTypes.STRAIN_DATA.get(), data);
                        colored.set(ModDataComponentTypes.STRAIN_ID.get(), DEFAULT_STRAIN);
                    });
                    return colored;
                })
                .collect(Collectors.toList());
    }

    // ── Focus helpers ────────────────────────────────────────────────────

    /** Returns the first focused ItemStack, or {@link ItemStack#EMPTY}. */
    public static ItemStack focusedStack(IFocusGroup focuses) {
        if (focuses == null) return ItemStack.EMPTY;
        return focuses.getFocuses(VanillaTypes.ITEM_STACK)
                .map(f -> {
                    try {
                        return f.getTypedValue().getIngredient();
                    } catch (Exception e) {
                        return ItemStack.EMPTY;
                    }
                })
                .filter(s -> !s.isEmpty())
                .findFirst()
                .orElse(ItemStack.EMPTY);
    }

    /**
     * Returns the STRAIN_ID from the first focused ItemStack, or null.
     * Falls back to searching {@link StrainRegistry} by data value.
     */
    public static String focusedStrainId(IFocusGroup focuses) {
        ItemStack focus = focusedStack(focuses);
        if (focus.isEmpty()) return null;

        // Primary path: direct STRAIN_ID component
        String id = focus.get(ModDataComponentTypes.STRAIN_ID.get());
        if (id != null) return id;

        // Fallback: try to find a preset whose StrainData matches
        StrainData data = focus.get(ModDataComponentTypes.STRAIN_DATA.get());
        if (data != null) {
            return StrainRegistry.ids().stream()
                    .filter(k -> StrainRegistry.get(k)
                            .map(d -> d.equals(data))
                            .orElse(false))
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }

    /** Like {@link #coloredStacks(Item)} but filtered to the focused strain when relevant. */
    public static List<ItemStack> coloredStacks(Item item, IFocusGroup focuses) {
        String id = focusedStrainId(focuses);
        if (id == null) return coloredStacks(item);

        List<ItemStack> filtered = coloredStacks(item).stream()
                .filter(s -> id.equals(s.get(ModDataComponentTypes.STRAIN_ID.get())))
                .collect(Collectors.toList());

        if (!filtered.isEmpty()) return filtered;

        // Focused strain is not a preset — try making a stack from the focus itself.
        ItemStack focus = focusedStack(focuses);
        if (!focus.isEmpty() && isStrainItem(focus.getItem())) {
            StrainData data = focus.get(ModDataComponentTypes.STRAIN_DATA.get());
            if (data != null) {
                ItemStack custom = new ItemStack(item);
                custom.set(ModDataComponentTypes.STRAIN_DATA.get(), data);
                return List.of(custom);
            }
        }
        return coloredStacks(item);
    }

    /** Like {@link #coloredStacksForIngredient(Ingredient)} but filtered to the focused strain when relevant. */
    public static List<ItemStack> coloredStacksForIngredient(Ingredient ingredient, IFocusGroup focuses) {
        String id = focusedStrainId(focuses);
        if (id == null) return coloredStacksForIngredient(ingredient);

        List<ItemStack> filtered = coloredStacksForIngredient(ingredient).stream()
                .filter(s -> id.equals(s.get(ModDataComponentTypes.STRAIN_ID.get())))
                .collect(Collectors.toList());

        if (!filtered.isEmpty()) return filtered;

        // Fallback: build a single stack from the focus's own StrainData.
        ItemStack focus = focusedStack(focuses);
        if (!focus.isEmpty() && isStrainItem(focus.getItem())) {
            StrainData data = focus.get(ModDataComponentTypes.STRAIN_DATA.get());
            if (data != null) {
                ItemStack custom = new ItemStack(focus.getItem());
                custom.set(ModDataComponentTypes.STRAIN_DATA.get(), data);
                return List.of(custom);
            }
        }
        return coloredStacksForIngredient(ingredient);
    }

    /** Like {@link #coloredDriedBudStacks(Item)} but filtered to the focused strain when relevant. */
    public static List<ItemStack> coloredDriedBudStacks(Item item, IFocusGroup focuses) {
        String id = focusedStrainId(focuses);
        if (id == null) return coloredDriedBudStacks(item);

        List<ItemStack> filtered = coloredDriedBudStacks(item).stream()
                .filter(s -> id.equals(s.get(ModDataComponentTypes.STRAIN_ID.get())))
                .collect(Collectors.toList());

        if (!filtered.isEmpty()) return filtered;

        ItemStack focus = focusedStack(focuses);
        if (!focus.isEmpty() && isStrainItem(focus.getItem())) {
            StrainData data = focus.get(ModDataComponentTypes.STRAIN_DATA.get());
            if (data != null) {
                ItemStack custom = new ItemStack(item);
                custom.set(ModDataComponentTypes.STRAIN_DATA.get(), data);
                return List.of(custom);
            }
        }
        return coloredDriedBudStacks(item);
    }

    /**
     * Returns an Ingredient with coloured (and optionally focus-filtered)
     * stacks for display in a JEI recipe slot.
     * <p>
     * When the focus is a strain item whose data does NOT match any preset
     * (i.e. a custom mutation strain), a single coloured stack is created
     * directly from the focus's {@link StrainData} so the correct colour and
     * tooltip appear in the slot.
     */
    public static Ingredient coloredIngredient(Ingredient original, IFocusGroup focuses) {
        List<ItemStack> stacks = coloredStacksForIngredient(original, focuses);
        if (!stacks.isEmpty()) {
            return Ingredient.of(stacks.stream());
        }

        // Empty after filtering — the focus is likely a custom (non‑preset) strain
        // or the focus carries StrainData without a matching STRAIN_ID.
        ItemStack focus = focusedStack(focuses);
        if (!focus.isEmpty() && isStrainItem(focus.getItem())) {
            StrainData data = focus.get(ModDataComponentTypes.STRAIN_DATA.get());
            if (data != null) {
                ItemStack customStack = new ItemStack(focus.getItem());
                customStack.set(ModDataComponentTypes.STRAIN_DATA.get(), data);
                return Ingredient.of(customStack);
            }
        }

        // No useful focus — show all 25 preset colours
        return original;
    }
}
