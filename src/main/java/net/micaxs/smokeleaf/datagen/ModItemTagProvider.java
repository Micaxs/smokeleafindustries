package net.micaxs.smokeleaf.datagen;

import net.micaxs.smokeleaf.SmokeleafIndustries;
import net.micaxs.smokeleaf.item.ModItems;
import net.micaxs.smokeleaf.utils.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ItemTagsProvider;
import net.micaxs.smokeleaf.block.ModBlocks;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends ItemTagsProvider {

    public ModItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, SmokeleafIndustries.MODID);
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

        // Item tags can no longer copy block tags, so the hemp wool items are listed directly
        // (mirroring the block tags in ModBlockTagProvider).
        for (DeferredBlock<Block> wool : ModBlocks.HEMP_WOOL.values()) {
            this.tag(ModTags.HEMP_WOOL).add(wool.get().asItem());
            this.tag(ItemTags.WOOL).add(wool.get().asItem());
        }

        this.tag(ModTags.REPAIRS_BAJA_HOODIE).add(ModItems.HEMP_FABRIC.get());

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
