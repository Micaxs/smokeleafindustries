package net.micaxs.smokeleaf.datagen;

import net.micaxs.smokeleaf.SmokeleafIndustries;
import net.micaxs.smokeleaf.block.ModBlocks;
import net.micaxs.smokeleaf.item.ModItems;
import net.micaxs.smokeleaf.utils.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends ItemTagsProvider {

    public ModItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Block>> blockTags, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTags, SmokeleafIndustries.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        this.tag(ModTags.WEED_SEEDS).add(
                ModItems.GENERIC_SEEDS.get(),
                ModItems.HEMP_SEEDS.get(),
                ModItems.WHITE_WIDOW_SEEDS.get(),
                ModItems.SOUR_DIESEL_SEEDS.get(),
                ModItems.LEMON_HAZE_SEEDS.get(),
                ModItems.BUBBLE_KUSH_SEEDS.get(),
                ModItems.BLUE_ICE_SEEDS.get(),
                ModItems.BUBBLEGUM_SEEDS.get(),
                ModItems.PURPLE_HAZE_SEEDS.get(),
                ModItems.OG_KUSH_SEEDS.get(),
                ModItems.JACK_HERER_SEEDS.get(),
                ModItems.GARY_PEYTON_SEEDS.get(),
                ModItems.AMNESIA_HAZE_SEEDS.get(),
                ModItems.AK47_SEEDS.get(),
                ModItems.GHOST_TRAIN_SEEDS.get(),
                ModItems.GRAPE_APE_SEEDS.get(),
                ModItems.COTTON_CANDY_SEEDS.get(),
                ModItems.BANANA_KUSH_SEEDS.get(),
                ModItems.CARBON_FIBER_SEEDS.get(),
                ModItems.BIRTHDAY_CAKE_SEEDS.get(),
                ModItems.BLUE_COOKIES_SEEDS.get(),
                ModItems.AFGHANI_SEEDS.get(),
                ModItems.MOONBOW_SEEDS.get(),
                ModItems.LAVA_CAKE_SEEDS.get(),
                ModItems.JELLY_RANCHER_SEEDS.get(),
                ModItems.STRAWBERRY_SHORTCAKE_SEEDS.get(),
                ModItems.PINK_KUSH_SEEDS.get()
        );

        this.tag(ModTags.WEEDS).add(
                ModItems.GENERIC_WEED.get(),
                ModItems.WHITE_WIDOW_WEED.get(),
                ModItems.SOUR_DIESEL_WEED.get(),
                ModItems.LEMON_HAZE_WEED.get(),
                ModItems.BUBBLE_KUSH_WEED.get(),
                ModItems.BLUE_ICE_WEED.get(),
                ModItems.BUBBLEGUM_WEED.get(),
                ModItems.PURPLE_HAZE_WEED.get(),
                ModItems.OG_KUSH_WEED.get(),
                ModItems.JACK_HERER_WEED.get(),
                ModItems.GARY_PEYTON_WEED.get(),
                ModItems.AMNESIA_HAZE_WEED.get(),
                ModItems.AK47_WEED.get(),
                ModItems.GHOST_TRAIN_WEED.get(),
                ModItems.GRAPE_APE_WEED.get(),
                ModItems.COTTON_CANDY_WEED.get(),
                ModItems.BANANA_KUSH_WEED.get(),
                ModItems.CARBON_FIBER_WEED.get(),
                ModItems.BIRTHDAY_CAKE_WEED.get(),
                ModItems.BLUE_COOKIES_WEED.get(),
                ModItems.AFGHANI_WEED.get(),
                ModItems.MOONBOW_WEED.get(),
                ModItems.LAVA_CAKE_WEED.get(),
                ModItems.JELLY_RANCHER_WEED.get(),
                ModItems.STRAWBERRY_SHORTCAKE_WEED.get(),
                ModItems.PINK_KUSH_WEED.get()
        );

        this.tag(ModTags.WEED_BUDS).add(
                ModItems.GENERIC_BUD.get(),
                ModItems.WHITE_WIDOW_BUD.get(),
                ModItems.SOUR_DIESEL_BUD.get(),
                ModItems.LEMON_HAZE_BUD.get(),
                ModItems.BUBBLE_KUSH_BUD.get(),
                ModItems.BLUE_ICE_BUD.get(),
                ModItems.BUBBLEGUM_BUD.get(),
                ModItems.PURPLE_HAZE_BUD.get(),
                ModItems.OG_KUSH_BUD.get(),
                ModItems.JACK_HERER_BUD.get(),
                ModItems.GARY_PEYTON_BUD.get(),
                ModItems.AMNESIA_HAZE_BUD.get(),
                ModItems.AK47_BUD.get(),
                ModItems.GHOST_TRAIN_BUD.get(),
                ModItems.GRAPE_APE_BUD.get(),
                ModItems.COTTON_CANDY_BUD.get(),
                ModItems.BANANA_KUSH_BUD.get(),
                ModItems.CARBON_FIBER_BUD.get(),
                ModItems.BIRTHDAY_CAKE_BUD.get(),
                ModItems.BLUE_COOKIES_BUD.get(),
                ModItems.AFGHANI_BUD.get(),
                ModItems.MOONBOW_BUD.get(),
                ModItems.LAVA_CAKE_BUD.get(),
                ModItems.JELLY_RANCHER_BUD.get(),
                ModItems.STRAWBERRY_SHORTCAKE_BUD.get(),
                ModItems.PINK_KUSH_BUD.get()

        );

        this.tag(ModTags.WEED_EXTRACTS).add(
                ModItems.GENERIC_EXTRACT.get(),
                ModItems.WHITE_WIDOW_EXTRACT.get(),
                ModItems.SOUR_DIESEL_EXTRACT.get(),
                ModItems.LEMON_HAZE_EXTRACT.get(),
                ModItems.BUBBLE_KUSH_EXTRACT.get(),
                ModItems.BLUE_ICE_EXTRACT.get(),
                ModItems.BUBBLEGUM_EXTRACT.get(),
                ModItems.PURPLE_HAZE_EXTRACT.get(),
                ModItems.OG_KUSH_EXTRACT.get(),
                ModItems.JACK_HERER_EXTRACT.get(),
                ModItems.GARY_PEYTON_EXTRACT.get(),
                ModItems.AMNESIA_HAZE_EXTRACT.get(),
                ModItems.AK47_EXTRACT.get(),
                ModItems.GHOST_TRAIN_EXTRACT.get(),
                ModItems.GRAPE_APE_EXTRACT.get(),
                ModItems.COTTON_CANDY_EXTRACT.get(),
                ModItems.BANANA_KUSH_EXTRACT.get(),
                ModItems.CARBON_FIBER_EXTRACT.get(),
                ModItems.BIRTHDAY_CAKE_EXTRACT.get(),
                ModItems.BLUE_COOKIES_EXTRACT.get(),
                ModItems.AFGHANI_EXTRACT.get(),
                ModItems.MOONBOW_EXTRACT.get(),
                ModItems.LAVA_CAKE_EXTRACT.get(),
                ModItems.JELLY_RANCHER_EXTRACT.get(),
                ModItems.STRAWBERRY_SHORTCAKE_EXTRACT.get(),
                ModItems.PINK_KUSH_EXTRACT.get()
        );

        this.tag(ModTags.WHITE_WIDOW_STRAIN).add(
                ModItems.WHITE_WIDOW_WEED.get(),
                ModItems.WHITE_WIDOW_EXTRACT.get()
        );

        this.tag(ModTags.LEAVES).add(
                Items.ACACIA_LEAVES,
                Items.BIRCH_LEAVES,
                Items.DARK_OAK_LEAVES,
                Items.JUNGLE_LEAVES,
                Items.OAK_LEAVES,
                Items.SPRUCE_LEAVES,
                Items.FLOWERING_AZALEA_LEAVES,
                Items.AZALEA_LEAVES,
                Items.FLOWERING_AZALEA_LEAVES,
                Items.CHERRY_LEAVES,
                Items.MANGROVE_LEAVES
        );

    }
}
