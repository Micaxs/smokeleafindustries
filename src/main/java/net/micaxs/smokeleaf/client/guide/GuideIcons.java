package net.micaxs.smokeleaf.client.guide;

import net.micaxs.smokeleaf.component.ModDataComponentTypes;
import net.micaxs.smokeleaf.strain.StrainRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * Builds the {@link ItemStack}s the guide book renders for tab icons, entry-list rows, and
 * spotlight pages. The mod's bud/weed/seed/extract/gummy items are colored entirely through their
 * {@code StrainData} data component (see {@code SmokeleafIndustriesClient#onItemColor}) — a plain
 * {@code new ItemStack(item)} with no strain data renders every one of those items as flat grey,
 * since their {@code ItemColor} handlers fall back to white/no-tint when the component is absent.
 * Every guide icon therefore always carries <i>some</i> strain's data: the entry's own strain when
 * known, or {@link #DEFAULT_STRAIN} otherwise — attaching it to an item whose color handler doesn't
 * look at it (a book, a machine block, an ingot) is simply a no-op, so this is safe to do
 * unconditionally rather than special-casing which items need it.
 */
public final class GuideIcons {
    private GuideIcons() {}

    /** Flagship preset used to color any icon that isn't tied to one specific strain. */
    public static final String DEFAULT_STRAIN = "og_kush";

    public static ItemStack stack(ResourceLocation itemId, String strainId) {
        Item item = BuiltInRegistries.ITEM.get(itemId);
        ItemStack stack = new ItemStack(item);
        String effective = (strainId != null && !strainId.isBlank()) ? strainId : DEFAULT_STRAIN;
        StrainRegistry.get(effective).ifPresent(data -> {
            stack.set(ModDataComponentTypes.STRAIN_DATA.get(), data);
            stack.set(ModDataComponentTypes.STRAIN_ID.get(), effective);
        });
        return stack;
    }
}
