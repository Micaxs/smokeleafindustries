package net.micaxs.smokeleaf.utils;

import net.micaxs.smokeleaf.SmokeleafIndustries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.block.Block;

public class ModTags {

    public static final TagKey<Item> WEEDS = tag("weeds");
    public static final TagKey<Item> WEED_SEEDS = tag("weed_seeds");
    public static final TagKey<Item> WEED_BUDS = tag("weed_buds");
    public static final TagKey<Item> WEED_EXTRACTS = tag("weed_extracts");
    public static final TagKey<Item> WHITE_WIDOW_STRAIN = tag("white_widow_strain");
    public static final TagKey<Item> SOUR_DIESEL_STRAIN = tag("sour_diesel_strain");
    public static final TagKey<Item> LEMON_HAZE_STRAIN = tag("lemon_haze_strain");
    public static final TagKey<Item> BUBBLE_KUSH_STRAIN = tag("bubble_kush_strain");
    public static final TagKey<Item> BUBBLEGUM_STRAIN = tag("bubblegum_strain");
    public static final TagKey<Item> BLUE_ICE_STRAIN = tag("blue_ice_strain");
    public static final TagKey<Item> PURPLE_HAZE_STRAIN = tag("purple_haze_strain");

    public static final TagKey<Item> LEAVES = tag("leaves");

    public static final TagKey<Block> POT_SOILS = blockTag("pot_soils");

    // NOTE: Tag JSON will be added later; for now we treat UNIDENTIFIED_SEEDS as a valid seed in GrowPot logic.
    public static final TagKey<Item> UNIDENTIFIED_SEEDS = tag("unidentified_seeds");

    private static TagKey<Item> tag(String name) {
        return ItemTags.create(ResourceLocation.fromNamespaceAndPath(SmokeleafIndustries.MODID, name));
    }

    private static TagKey<Block> blockTag(String name) {
        return BlockTags.create(ResourceLocation.fromNamespaceAndPath(SmokeleafIndustries.MODID, name));
    }
}
