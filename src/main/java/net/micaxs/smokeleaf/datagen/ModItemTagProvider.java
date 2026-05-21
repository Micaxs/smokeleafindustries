package net.micaxs.smokeleaf.datagen;

import net.micaxs.smokeleaf.SmokeleafIndustries;
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
                ModItems.HEMP_SEEDS.get()
        );

        this.tag(ModTags.WEEDS).add(
                ModItems.GENERIC_WEED.get()
        );

        this.tag(ModTags.WEED_BUDS).add(
                ModItems.GENERIC_BUD.get()

        );

        this.tag(ModTags.WEED_EXTRACTS).add(
                ModItems.GENERIC_EXTRACT.get()
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
