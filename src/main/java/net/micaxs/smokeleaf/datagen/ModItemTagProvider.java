package net.micaxs.smokeleaf.datagen;

import net.micaxs.smokeleaf.SmokeleafIndustries;
import net.micaxs.smokeleaf.item.ModItems;
import net.micaxs.smokeleaf.utils.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
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

        this.copy(ModTags.HEMP_WOOL_BLOCKS, ModTags.HEMP_WOOL);
        this.copy(BlockTags.WOOL, ItemTags.WOOL);

        // Pipe Wrench — grants Unbreaking (and Mending) eligibility, same as any vanilla tool.
        this.tag(ItemTags.DURABILITY_ENCHANTABLE).add(ModItems.PIPE_WRENCH.get());

        // Baja Hoodie / Reinforced Baja Hoodie — membership in these base slot tags is what
        // grants vanilla armor's trimmability, enchantability, and durability-loss-on-vanish
        // behavior for free (they're all built from these four tags).
        this.tag(ItemTags.HEAD_ARMOR)
                .add(ModItems.BAJA_HOODIE_HELMET.get())
                .add(ModItems.REINFORCED_BAJA_HOODIE_HELMET.get());
        this.tag(ItemTags.CHEST_ARMOR)
                .add(ModItems.BAJA_HOODIE_CHESTPLATE.get())
                .add(ModItems.REINFORCED_BAJA_HOODIE_CHESTPLATE.get());
        this.tag(ItemTags.LEG_ARMOR)
                .add(ModItems.BAJA_HOODIE_LEGGINGS.get())
                .add(ModItems.REINFORCED_BAJA_HOODIE_LEGGINGS.get());
        this.tag(ItemTags.FOOT_ARMOR)
                .add(ModItems.BAJA_HOODIE_BOOTS.get())
                .add(ModItems.REINFORCED_BAJA_HOODIE_BOOTS.get());

    }
}
