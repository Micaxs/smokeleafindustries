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
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
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

        // Pipe Wrench — in 1.20.1 Unbreaking/Mending eligibility comes from the item being damageable
        // (EnchantmentCategory.BREAKABLE), so no tag is needed.

        // Baja Hoodie / Reinforced Baja Hoodie — in 1.20.1 enchantability comes from ArmorItem
        // itself; trimmability is what the TRIMMABLE_ARMOR tag grants.
        this.tag(ItemTags.TRIMMABLE_ARMOR)
                .add(ModItems.BAJA_HOODIE_HELMET.get())
                .add(ModItems.REINFORCED_BAJA_HOODIE_HELMET.get())
                .add(ModItems.BAJA_HOODIE_CHESTPLATE.get())
                .add(ModItems.REINFORCED_BAJA_HOODIE_CHESTPLATE.get())
                .add(ModItems.BAJA_HOODIE_LEGGINGS.get())
                .add(ModItems.REINFORCED_BAJA_HOODIE_LEGGINGS.get())
                .add(ModItems.BAJA_HOODIE_BOOTS.get())
                .add(ModItems.REINFORCED_BAJA_HOODIE_BOOTS.get());

    }
}
