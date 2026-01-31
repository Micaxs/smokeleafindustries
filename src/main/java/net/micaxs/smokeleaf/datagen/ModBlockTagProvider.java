package net.micaxs.smokeleaf.datagen;

import net.micaxs.smokeleaf.SmokeleafIndustries;
import net.micaxs.smokeleaf.block.ModBlocks;
import net.micaxs.smokeleaf.utils.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
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
                .add(ModBlocks.SYNTHESIZER.get())
                .add(ModBlocks.DRYER.get())
                .add(ModBlocks.GROW_POT.get())
                .add(ModBlocks.HEMP_STONE_SLAB.get())
                .add(ModBlocks.HEMP_BRICK_SLAB.get())
                .add(ModBlocks.HEMP_CHISELED_STONE_SLAB.get())
                .add(ModBlocks.HEMP_STONE_STAIRS.get())
                .add(ModBlocks.HEMP_BRICK_STAIRS.get())
                .add(ModBlocks.HEMP_CHISELED_STONE_STAIRS.get());

        this.tag(BlockTags.NEEDS_STONE_TOOL)
                .add(ModBlocks.HEMP_STONE.get())
                .add(ModBlocks.LED_LIGHT.get())
                .add(ModBlocks.REFLECTOR.get())
                .add(ModBlocks.HEMP_CHISELED_STONE.get())
                .add(ModBlocks.HEMP_BRICKS.get())
                .add(ModBlocks.DRYING_RACK.get())
                .add(ModBlocks.HEMP_PLANK_SLAB.get())
                .add(ModBlocks.HEMP_STONE_SLAB.get())
                .add(ModBlocks.HEMP_BRICK_SLAB.get())
                .add(ModBlocks.HEMP_CHISELED_STONE_SLAB.get())
                .add(ModBlocks.HEMP_PLANK_STAIRS.get())
                .add(ModBlocks.HEMP_STONE_STAIRS.get())
                .add(ModBlocks.HEMP_BRICK_STAIRS.get())
                .add(ModBlocks.HEMP_CHISELED_STONE_STAIRS.get());

        this.tag(BlockTags.NEEDS_IRON_TOOL)
                .add(ModBlocks.GRINDER.get())
                .add(ModBlocks.EXTRACTOR.get())
                .add(ModBlocks.GENERATOR.get())
                .add(ModBlocks.MUTATOR.get())
                .add(ModBlocks.LIQUIFIER.get())
                .add(ModBlocks.SEQUENCER.get())
                .add(ModBlocks.SYNTHESIZER.get());

        this.tag(BlockTags.PLANKS)
                .add(ModBlocks.HEMP_PLANKS.get())
                .add(ModBlocks.HEMP_PLANK_SLAB.get())
                .add(ModBlocks.HEMP_PLANK_STAIRS.get());

        this.tag(BlockTags.WOODEN_FENCES).add(ModBlocks.HEMP_PLANK_FENCE.get());
        this.tag(BlockTags.FENCES).add(ModBlocks.HEMP_PLANK_FENCE.get());
        this.tag(BlockTags.FENCE_GATES).add(ModBlocks.HEMP_PLANK_FENCE_GATE.get());
        this.tag(BlockTags.WALLS)
                .add(ModBlocks.HEMP_STONE_WALL.get())
                .add(ModBlocks.HEMP_BRICK_WALL.get())
                .add(ModBlocks.HEMP_CHISELED_STONE_WALL.get());


        this.tag(ModTags.POT_SOILS)
                .add(Blocks.DIRT)
                .add(Blocks.FARMLAND)
                .add(Blocks.PODZOL);

    }


}
