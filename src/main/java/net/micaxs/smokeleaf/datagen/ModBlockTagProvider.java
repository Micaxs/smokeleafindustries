package net.micaxs.smokeleaf.datagen;

import net.micaxs.smokeleaf.SmokeleafIndustries;
import net.micaxs.smokeleaf.block.ModBlocks;
import net.micaxs.smokeleaf.utils.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends BlockTagsProvider  {

    public ModBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, SmokeleafIndustries.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(ModBlocks.HEMP_STONE.get())
                .add(ModBlocks.LED_LIGHT.get())
                .add(ModBlocks.REFLECTOR.get())
                .add(ModBlocks.HEMP_CHISELED_STONE.get())
                .add(ModBlocks.HEMP_BRICKS.get())
                .add(ModBlocks.DRYING_RACK.get())
                .add(ModBlocks.GRINDER.get())
                .add(ModBlocks.EXTRACTOR.get())
                .add(ModBlocks.GENERATOR.get())
                .add(ModBlocks.MUTATOR.get())
                .add(ModBlocks.LIQUIFIER.get())
                .add(ModBlocks.SEQUENCER.get())
                .add(ModBlocks.SYNTHESIZER.get());

        this.tag(BlockTags.NEEDS_STONE_TOOL)
                .add(ModBlocks.HEMP_STONE.get())
                .add(ModBlocks.LED_LIGHT.get())
                .add(ModBlocks.REFLECTOR.get())
                .add(ModBlocks.HEMP_CHISELED_STONE.get())
                .add(ModBlocks.HEMP_BRICKS.get())
                .add(ModBlocks.DRYING_RACK.get());

        this.tag(BlockTags.NEEDS_IRON_TOOL)
                .add(ModBlocks.GRINDER.get())
                .add(ModBlocks.EXTRACTOR.get())
                .add(ModBlocks.GENERATOR.get())
                .add(ModBlocks.MUTATOR.get())
                .add(ModBlocks.LIQUIFIER.get())
                .add(ModBlocks.SEQUENCER.get())
                .add(ModBlocks.SYNTHESIZER.get());

        this.tag(BlockTags.PLANKS)
                .add(ModBlocks.HEMP_PLANKS.get());

    }


}
