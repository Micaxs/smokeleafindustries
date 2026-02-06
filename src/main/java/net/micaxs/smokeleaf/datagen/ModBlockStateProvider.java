package net.micaxs.smokeleaf.datagen;

import net.micaxs.smokeleaf.SmokeleafIndustries;
import net.micaxs.smokeleaf.block.ModBlocks;
import net.micaxs.smokeleaf.block.custom.BaseWeedCropBlock;
import net.micaxs.smokeleaf.block.custom.GrowLightBlock;
import net.micaxs.smokeleaf.block.custom.ReflectorBlock;
import net.micaxs.smokeleaf.block.custom.TobaccoCropBlock;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;

import java.util.function.Function;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, SmokeleafIndustries.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        blockWithItem(ModBlocks.HEMP_STONE);
        stairsBlock(((StairBlock) ModBlocks.HEMP_STONE_STAIRS.get()), blockTexture(ModBlocks.HEMP_STONE.get()));
        slabBlock((((SlabBlock) ModBlocks.HEMP_STONE_SLAB.get())), blockTexture(ModBlocks.HEMP_STONE.get()), blockTexture(ModBlocks.HEMP_STONE.get()));
        blockItem(ModBlocks.HEMP_STONE_SLAB);
        blockItem(ModBlocks.HEMP_STONE_STAIRS);
        pressurePlateBlock(((PressurePlateBlock) ModBlocks.HEMP_STONE_PRESSURE_PLATE.get()), blockTexture(ModBlocks.HEMP_STONE.get()));
        buttonBlock(((ButtonBlock) ModBlocks.HEMP_STONE_BUTTON.get()), blockTexture(ModBlocks.HEMP_STONE.get()));
        blockItem(ModBlocks.HEMP_STONE_PRESSURE_PLATE);
        wallBlock(((WallBlock) ModBlocks.HEMP_STONE_WALL.get()), blockTexture(ModBlocks.HEMP_STONE.get()));

        blockWithItem(ModBlocks.HEMP_BRICKS);
        stairsBlock(((StairBlock) ModBlocks.HEMP_BRICK_STAIRS.get()), blockTexture(ModBlocks.HEMP_BRICKS.get()));
        slabBlock((((SlabBlock) ModBlocks.HEMP_BRICK_SLAB.get())), blockTexture(ModBlocks.HEMP_BRICKS.get()), blockTexture(ModBlocks.HEMP_BRICKS.get()));
        blockItem(ModBlocks.HEMP_BRICK_SLAB);
        blockItem(ModBlocks.HEMP_BRICK_STAIRS);
        wallBlock(((WallBlock) ModBlocks.HEMP_BRICK_WALL.get()), blockTexture(ModBlocks.HEMP_BRICKS.get()));

        blockWithItem(ModBlocks.HEMP_CHISELED_STONE);
        stairsBlock(((StairBlock) ModBlocks.HEMP_CHISELED_STONE_STAIRS.get()), blockTexture(ModBlocks.HEMP_CHISELED_STONE.get()));
        slabBlock((((SlabBlock) ModBlocks.HEMP_CHISELED_STONE_SLAB.get())), blockTexture(ModBlocks.HEMP_CHISELED_STONE.get()), blockTexture(ModBlocks.HEMP_CHISELED_STONE.get()));
        blockItem(ModBlocks.HEMP_CHISELED_STONE_SLAB);
        blockItem(ModBlocks.HEMP_CHISELED_STONE_STAIRS);
        wallBlock(((WallBlock) ModBlocks.HEMP_CHISELED_STONE_WALL.get()), blockTexture(ModBlocks.HEMP_CHISELED_STONE.get()));

        blockWithItem(ModBlocks.HEMP_PLANKS);
        stairsBlock(((StairBlock) ModBlocks.HEMP_PLANK_STAIRS.get()), blockTexture(ModBlocks.HEMP_PLANKS.get()));
        slabBlock((((SlabBlock) ModBlocks.HEMP_PLANK_SLAB.get())), blockTexture(ModBlocks.HEMP_PLANKS.get()), blockTexture(ModBlocks.HEMP_PLANKS.get()));
        blockItem(ModBlocks.HEMP_PLANK_SLAB);
        blockItem(ModBlocks.HEMP_PLANK_STAIRS);
        pressurePlateBlock(((PressurePlateBlock) ModBlocks.HEMP_PLANK_PRESSURE_PLATE.get()), blockTexture(ModBlocks.HEMP_PLANKS.get()));
        buttonBlock(((ButtonBlock) ModBlocks.HEMP_PLANK_BUTTON.get()), blockTexture(ModBlocks.HEMP_PLANKS.get()));
        blockItem(ModBlocks.HEMP_PLANK_PRESSURE_PLATE);
        fenceBlock(((FenceBlock) ModBlocks.HEMP_PLANK_FENCE.get()), blockTexture(ModBlocks.HEMP_PLANKS.get()));
        fenceGateBlock(((FenceGateBlock) ModBlocks.HEMP_PLANK_FENCE_GATE.get()), blockTexture(ModBlocks.HEMP_PLANKS.get()));
        blockItem(ModBlocks.HEMP_PLANK_FENCE_GATE);
        doorBlockWithRenderType(((DoorBlock) ModBlocks.HEMP_PLANK_DOOR.get()), modLoc("block/hemp_plank_door_bottom"), modLoc("block/hemp_plank_door_top"), "cutout");
        trapdoorBlockWithRenderType(((TrapDoorBlock) ModBlocks.HEMP_PLANK_TRAPDOOR.get()), modLoc("block/hemp_plank_trapdoor"), true, "cutout");

        blockItem(ModBlocks.HEMP_PLANK_TRAPDOOR, "_bottom");

        makeWeedCrop((BaseWeedCropBlock) ModBlocks.HEMP_CROP.get(), "hemp_stage_", "hemp/hemp_stage_");
        makeWeedCrop((BaseWeedCropBlock) ModBlocks.WHITE_WIDOW_CROP.get(), "white_widow_stage_", "white_widow/white_widow_stage_");
        makeWeedCrop((BaseWeedCropBlock) ModBlocks.BUBBLE_KUSH_CROP.get(), "bubble_kush_stage_", "bubble_kush/bubble_kush_stage_");
        makeWeedCrop((BaseWeedCropBlock) ModBlocks.LEMON_HAZE_CROP.get(), "lemon_haze_stage_", "lemon_haze/lemon_haze_stage_");
        makeWeedCrop((BaseWeedCropBlock) ModBlocks.SOUR_DIESEL_CROP.get(), "sour_diesel_stage_", "sour_diesel/sour_diesel_stage_");
        makeWeedCrop((BaseWeedCropBlock) ModBlocks.BLUE_ICE_CROP.get(), "blue_ice_stage_", "blue_ice/blue_ice_stage_");
        makeWeedCrop((BaseWeedCropBlock) ModBlocks.BUBBLEGUM_CROP.get(), "bubblegum_stage_", "bubblegum/bubblegum_stage_");
        makeWeedCrop((BaseWeedCropBlock) ModBlocks.PURPLE_HAZE_CROP.get(), "purple_haze_stage_", "purple_haze/purple_haze_stage_");
        makeWeedCrop((BaseWeedCropBlock) ModBlocks.OG_KUSH_CROP.get(), "og_kush_stage_", "og_kush/og_kush_stage_");
        makeWeedCrop((BaseWeedCropBlock) ModBlocks.JACK_HERER_CROP.get(), "jack_herer_stage_", "jack_herer/jack_herer_stage_");
        makeWeedCrop((BaseWeedCropBlock) ModBlocks.GARY_PEYTON_CROP.get(), "gary_peyton_stage_", "gary_peyton/gary_peyton_stage_");
        makeWeedCrop((BaseWeedCropBlock) ModBlocks.AMNESIA_HAZE_CROP.get(), "amnesia_haze_stage_", "amnesia_haze/amnesia_haze_stage_");
        makeWeedCrop((BaseWeedCropBlock) ModBlocks.AK47_CROP.get(), "ak47_stage_", "ak47/ak47_stage_");
        makeWeedCrop((BaseWeedCropBlock) ModBlocks.GHOST_TRAIN_CROP.get(), "ghost_train_stage_", "ghost_train/ghost_train_stage_");
        makeWeedCrop((BaseWeedCropBlock) ModBlocks.GRAPE_APE_CROP.get(), "grape_ape_stage_", "grape_ape/grape_ape_stage_");
        makeWeedCrop((BaseWeedCropBlock) ModBlocks.COTTON_CANDY_CROP.get(), "cotton_candy_stage_", "cotton_candy/cotton_candy_stage_");
        makeWeedCrop((BaseWeedCropBlock) ModBlocks.BANANA_KUSH_CROP.get(), "banana_kush_stage_", "banana_kush/banana_kush_stage_");
        makeWeedCrop((BaseWeedCropBlock) ModBlocks.CARBON_FIBER_CROP.get(), "carbon_fiber_stage_", "carbon_fiber/carbon_fiber_stage_");
        makeWeedCrop((BaseWeedCropBlock) ModBlocks.BIRTHDAY_CAKE_CROP.get(), "birthday_cake_stage_", "birthday_cake/birthday_cake_stage_");
        makeWeedCrop((BaseWeedCropBlock) ModBlocks.BLUE_COOKIES_CROP.get(), "blue_cookies_stage_", "blue_cookies/blue_cookies_stage_");
        makeWeedCrop((BaseWeedCropBlock) ModBlocks.AFGHANI_CROP.get(), "afghani_stage_", "afghani/afghani_stage_");
        makeWeedCrop((BaseWeedCropBlock) ModBlocks.MOONBOW_CROP.get(), "moonbow_stage_", "moonbow/moonbow_stage_");
        makeWeedCrop((BaseWeedCropBlock) ModBlocks.LAVA_CAKE_CROP.get(), "lava_cake_stage_", "lava_cake/lava_cake_stage_");
        makeWeedCrop((BaseWeedCropBlock) ModBlocks.JELLY_RANCHER_CROP.get(), "jelly_rancher_stage_", "jelly_rancher/jelly_rancher_stage_");
        makeWeedCrop((BaseWeedCropBlock) ModBlocks.STRAWBERRY_SHORTCAKE_CROP.get(), "strawberry_shortcake_stage_", "strawberry_shortcake/strawberry_shortcake_stage_");
        makeWeedCrop((BaseWeedCropBlock) ModBlocks.PINK_KUSH_CROP.get(), "pink_kush_stage_", "pink_kush/pink_kush_stage_");

        makeTobaccoCrop((TobaccoCropBlock) ModBlocks.TOBACCO_CROP.get(), "tobacco_stage_", "tobacco/tobacco_stage_");


        growLightWithClicked();
        //horizontalBlock(ModBlocks.REFLECTOR.get(), new ModelFile.UncheckedModelFile(modLoc("block/reflector")));

        horizontalBooleanVariants(ModBlocks.REFLECTOR.get(), ReflectorBlock.FACING, ReflectorBlock.HAS_LAMP, "_lamp", "", false);


        horizontalBooleanVariants(ModBlocks.GENERATOR.get(), BlockStateProperties.HORIZONTAL_FACING, BlockStateProperties.POWERED, "_on", "", false);
        horizontalBooleanVariants(ModBlocks.GRINDER.get(), BlockStateProperties.HORIZONTAL_FACING, BlockStateProperties.POWERED, "_on", "", false);
        horizontalBooleanVariants(ModBlocks.EXTRACTOR.get(), BlockStateProperties.HORIZONTAL_FACING, BlockStateProperties.POWERED, "_on", "", false);
        horizontalBooleanVariants(ModBlocks.LIQUIFIER.get(), BlockStateProperties.HORIZONTAL_FACING, BlockStateProperties.POWERED, "_on", "", false);
        horizontalBooleanVariants(ModBlocks.MUTATOR.get(), BlockStateProperties.HORIZONTAL_FACING, BlockStateProperties.POWERED, "_on", "", false);
        horizontalBooleanVariants(ModBlocks.SYNTHESIZER.get(), BlockStateProperties.HORIZONTAL_FACING, BlockStateProperties.POWERED, "_on", "", false);
        horizontalBooleanVariants(ModBlocks.SEQUENCER.get(), BlockStateProperties.HORIZONTAL_FACING, BlockStateProperties.POWERED, "_on", "", false);
        horizontalBooleanVariants(ModBlocks.DRYER.get(), BlockStateProperties.HORIZONTAL_FACING, BlockStateProperties.POWERED, "_on", "", false);
        horizontalBooleanVariants(ModBlocks.MIXER.get(), BlockStateProperties.HORIZONTAL_FACING, BlockStateProperties.POWERED, "_on", "", false);



        horizontalBlock(ModBlocks.DRYING_RACK.get(), new ModelFile.UncheckedModelFile(modLoc("block/drying_rack")));

        horizontalBlock(ModBlocks.GROW_POT.get(), new ModelFile.UncheckedModelFile(modLoc("block/grow_pot")));

    }


    private void blockWithItem(DeferredBlock<?> deferredBlock) {
        simpleBlockWithItem(deferredBlock.get(), cubeAll(deferredBlock.get()));
    }

    private void growLightWithClicked() {
        var block = ModBlocks.LED_LIGHT.get();
        var vb = getVariantBuilder(block);
        String baseName = BuiltInRegistries.BLOCK.getKey(block).getPath();
        vb.forAllStates(state -> {
            Direction facing = state.getValue(GrowLightBlock.FACING);
            boolean clicked = state.getValue(GrowLightBlock.CLICKED);
            String suffix = clicked ? "_on" : "_off";

            ModelFile model = models().getExistingFile(modLoc("block/" + baseName + suffix));

            return ConfiguredModel.builder()
                    .modelFile(model)
                    .rotationY(((int) facing.toYRot()) % 360)
                    .build();
        });
        simpleBlockItem(block, models().getExistingFile(modLoc("block/" + baseName + "_off")));
    }

    private void makeTobaccoCrop(TobaccoCropBlock cropBlock, String modelName, String textureName) {
        Function<BlockState, ConfiguredModel[]> function =
                state -> tobaccoCropModel(state, cropBlock, modelName, textureName);
        getVariantBuilder(cropBlock).forAllStates(function);
    }

    private void makeWeedCrop(BaseWeedCropBlock cropBlock, String modelName, String textureName) {
        Function<BlockState, ConfiguredModel[]> function = (blockState) -> {
            int age = blockState.getValue(cropBlock.getAgeProperty());
            boolean isTop = blockState.getValue(cropBlock.getTop());
            String modelFile;
            String textureFile;
            if (isTop) {
                // Top
                modelFile = modelName + age;
                textureFile = textureName + age;
            } else {
                // Bottom
                if (age <= 6) {
                    modelFile = modelName + age;
                    textureFile = textureName + age;
                } else if (age <= 8) {
                    modelFile = modelName + "6";
                    textureFile = textureName + "6";
                } else {
                    modelFile = modelName + "6_full";
                    textureFile = textureName + "6_full";
                }
            }
            ResourceLocation tex = ResourceLocation.fromNamespaceAndPath(SmokeleafIndustries.MODID, "block/" + textureFile);
            ConfiguredModel cm = new ConfiguredModel(models().cross(modelFile, tex).renderType("cutout"));
            return new ConfiguredModel[]{cm};
        };
        getVariantBuilder(cropBlock).forAllStates(function);
    }

    private ConfiguredModel[] tobaccoCropModel(BlockState blockState, TobaccoCropBlock cropBlock, String modelName, String textureName) {
        ConfiguredModel[] models = new ConfiguredModel[1];
        models[0] = new ConfiguredModel(models().cross(
                modelName + blockState.getValue(cropBlock.getAgeProperty()),
                ResourceLocation.fromNamespaceAndPath(SmokeleafIndustries.MODID, "block/" + textureName + blockState.getValue(cropBlock.getAgeProperty()))
        ).renderType("cutout"));
        return models;
    }

    private ConfiguredModel[] weedCropModel(BlockState blockState, BaseWeedCropBlock cropBlock, String modelName, String textureName) {
        int age = blockState.getValue(cropBlock.getAgeProperty());
        boolean isTop = blockState.getValue(cropBlock.getTop());

        String model;

        // Special case: bottom block at age 8 uses "_stage_6_full"
        if (!isTop && (age == 9 || age == 10)) {
            model = modelName + "6_full";
        } else {
            model = modelName + age;
        }

        ConfiguredModel[] models = new ConfiguredModel[1];
        models[0] = new ConfiguredModel(models().cross(
                model,
                ResourceLocation.fromNamespaceAndPath(
                        SmokeleafIndustries.MODID,
                        "block/" + textureName + ((age == 9 || age == 10) && !isTop ? "6_full" : age)
                )
        ).renderType("cutout"));

        return models;
    }

    private void horizontalBooleanVariants(Block block, DirectionProperty facingProp, BooleanProperty flagProp, String trueSuffix, String falseSuffix, boolean itemUsesTrueVariant) {
        var vb = getVariantBuilder(block);
        String baseName = BuiltInRegistries.BLOCK.getKey(block).getPath(); // e.g., "generator" or "grow_light"

        vb.forAllStates(state -> {
            Direction facing = state.getValue(facingProp);
            boolean flag = state.getValue(flagProp);

            String suffix = flag ? trueSuffix : falseSuffix;
            ModelFile model = models().getExistingFile(modLoc("block/" + baseName + suffix));

            return ConfiguredModel.builder()
                    .modelFile(model)
                    .rotationY(((int) facing.toYRot()) % 360)
                    .build();
        });

        String itemSuffix = itemUsesTrueVariant ? trueSuffix : falseSuffix;
        simpleBlockItem(block, models().getExistingFile(modLoc("block/" + baseName + itemSuffix)));
    }

    private void blockItem(DeferredBlock<Block> deferredBlock) {
            simpleBlockItem(deferredBlock.get(), new ModelFile.UncheckedModelFile("smokeleafindustries:block/" + deferredBlock.getId().getPath()));
    }

    private void blockItem(DeferredBlock<Block> deferredBlock, String appendix) {
            simpleBlockItem(deferredBlock.get(), new ModelFile.UncheckedModelFile("smokeleafindustries:block/" + deferredBlock.getId().getPath() + appendix));
    }


}
