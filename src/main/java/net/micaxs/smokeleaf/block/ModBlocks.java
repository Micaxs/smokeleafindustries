package net.micaxs.smokeleaf.block;

import net.micaxs.smokeleaf.SmokeleafIndustries;
import net.micaxs.smokeleaf.block.custom.*;
import net.micaxs.smokeleaf.item.ModItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(SmokeleafIndustries.MODID);


    public static final DeferredBlock<Block> HEMP_STONE = registerBlock("hemp_stone",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(1f).requiresCorrectToolForDrops().sound(SoundType.STONE)));
    public static final DeferredBlock<Block> HEMP_STONE_STAIRS = registerBlock("hemp_stone_stairs",
            () -> new StairBlock(ModBlocks.HEMP_STONE.get().defaultBlockState(), BlockBehaviour.Properties.of()
                    .strength(1f).requiresCorrectToolForDrops().sound(SoundType.STONE)));
    public static final DeferredBlock<Block> HEMP_STONE_SLAB = registerBlock("hemp_stone_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.of()
                    .strength(1f).requiresCorrectToolForDrops().sound(SoundType.STONE)));
    public static final DeferredBlock<Block> HEMP_STONE_PRESSURE_PLATE = registerBlock("hemp_stone_pressure_plate",
            () -> new PressurePlateBlock(BlockSetType.STONE, BlockBehaviour.Properties.of()
                    .strength(1f).requiresCorrectToolForDrops().sound(SoundType.STONE)));
    public static final DeferredBlock<Block> HEMP_STONE_BUTTON = registerBlock("hemp_stone_button",
            () -> new ButtonBlock(BlockSetType.STONE, 10, BlockBehaviour.Properties.of().noCollission()
                    .strength(1f).requiresCorrectToolForDrops().sound(SoundType.STONE)));
    public static final DeferredBlock<Block> HEMP_STONE_WALL = registerBlock("hemp_stone_wall",
            () -> new WallBlock(BlockBehaviour.Properties.of()
                    .strength(1f).requiresCorrectToolForDrops().sound(SoundType.STONE)));


    public static final DeferredBlock<Block> HEMP_PLANKS = registerBlock("hemp_planks",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(1f).requiresCorrectToolForDrops().sound(SoundType.WOOD)));
    public static final DeferredBlock<Block> HEMP_PLANK_STAIRS = registerBlock("hemp_plank_stairs",
            () -> new StairBlock(ModBlocks.HEMP_PLANKS.get().defaultBlockState(), BlockBehaviour.Properties.of()
                    .strength(1f).requiresCorrectToolForDrops().sound(SoundType.WOOD)));
    public static final DeferredBlock<Block> HEMP_PLANK_SLAB = registerBlock("hemp_plank_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.of()
                    .strength(1f).requiresCorrectToolForDrops().sound(SoundType.WOOD)));
    public static final DeferredBlock<Block> HEMP_PLANK_PRESSURE_PLATE = registerBlock("hemp_plank_pressure_plate",
            () -> new PressurePlateBlock(BlockSetType.OAK, BlockBehaviour.Properties.of()
                    .strength(1f).requiresCorrectToolForDrops().sound(SoundType.WOOD)));
    public static final DeferredBlock<Block> HEMP_PLANK_BUTTON = registerBlock("hemp_plank_button",
            () -> new ButtonBlock(BlockSetType.OAK, 10, BlockBehaviour.Properties.of().noCollission()
                    .strength(1f).requiresCorrectToolForDrops().sound(SoundType.WOOD)));
    public static final DeferredBlock<Block> HEMP_PLANK_FENCE = registerBlock("hemp_plank_fence",
            () -> new FenceBlock(BlockBehaviour.Properties.of()
                    .strength(1f).requiresCorrectToolForDrops().sound(SoundType.WOOD)));
    public static final DeferredBlock<Block> HEMP_PLANK_FENCE_GATE = registerBlock("hemp_plank_fence_gate",
            () -> new FenceGateBlock(WoodType.OAK, BlockBehaviour.Properties.of()
                    .strength(1f).requiresCorrectToolForDrops().sound(SoundType.WOOD)));
    public static final DeferredBlock<Block> HEMP_PLANK_DOOR = registerBlock("hemp_plank_door",
            () -> new DoorBlock(BlockSetType.OAK, BlockBehaviour.Properties.of().noOcclusion()
                    .strength(1f).requiresCorrectToolForDrops().sound(SoundType.WOOD)));
    public static final DeferredBlock<Block> HEMP_PLANK_TRAPDOOR = registerBlock("hemp_plank_trapdoor",
            () -> new TrapDoorBlock(BlockSetType.OAK, BlockBehaviour.Properties.of().noOcclusion()
                    .strength(1f).requiresCorrectToolForDrops().sound(SoundType.WOOD)));


    public static final DeferredBlock<Block> HEMP_BRICKS = registerBlock("hemp_bricks",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(1f).requiresCorrectToolForDrops().sound(SoundType.STONE)));
    public static final DeferredBlock<Block> HEMP_BRICK_STAIRS = registerBlock("hemp_brick_stairs",
            () -> new StairBlock(ModBlocks.HEMP_BRICKS.get().defaultBlockState(), BlockBehaviour.Properties.of()
                    .strength(1f).requiresCorrectToolForDrops().sound(SoundType.STONE)));
    public static final DeferredBlock<Block> HEMP_BRICK_SLAB = registerBlock("hemp_brick_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.of()
                    .strength(1f).requiresCorrectToolForDrops().sound(SoundType.STONE)));
    public static final DeferredBlock<Block> HEMP_BRICK_WALL = registerBlock("hemp_brick_wall",
            () -> new WallBlock(BlockBehaviour.Properties.of()
                    .strength(1f).requiresCorrectToolForDrops().sound(SoundType.STONE)));


    public static final DeferredBlock<Block> HEMP_CHISELED_STONE = registerBlock("hemp_chiseled_stone",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(1f).requiresCorrectToolForDrops().sound(SoundType.STONE)));
    public static final DeferredBlock<Block> HEMP_CHISELED_STONE_STAIRS = registerBlock("hemp_chiseled_stone_stairs",
            () -> new StairBlock(ModBlocks.HEMP_CHISELED_STONE.get().defaultBlockState(), BlockBehaviour.Properties.of()
                    .strength(1f).requiresCorrectToolForDrops().sound(SoundType.STONE)));
    public static final DeferredBlock<Block> HEMP_CHISELED_STONE_SLAB = registerBlock("hemp_chiseled_stone_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.of()
                    .strength(1f).requiresCorrectToolForDrops().sound(SoundType.STONE)));
    public static final DeferredBlock<Block> HEMP_CHISELED_STONE_WALL = registerBlock("hemp_chiseled_stone_wall",
            () -> new WallBlock(BlockBehaviour.Properties.of()
                    .strength(1f).requiresCorrectToolForDrops().sound(SoundType.STONE)));


    public static final DeferredBlock<Block> GROW_POT = registerBlock("grow_pot",
            () -> new GrowPotBlock(BlockBehaviour.Properties.of().requiresCorrectToolForDrops()
                    .strength(1f).noOcclusion().sound(SoundType.METAL)));


    public static final DeferredBlock<Block> LED_LIGHT = registerBlock("led_light",
            () -> new GrowLightBlock(BlockBehaviour.Properties.of().requiresCorrectToolForDrops()
                    .strength(1f).lightLevel(state -> state.getValue(GrowLightBlock.CLICKED) ? 15 : 0).noOcclusion().sound(SoundType.GLASS)));

    public static final DeferredBlock<Block> REFLECTOR = registerBlock("reflector",
            () -> new ReflectorBlock(BlockBehaviour.Properties.of().requiresCorrectToolForDrops()
                    .strength(1f).noOcclusion().sound(SoundType.METAL)
                    .lightLevel(state -> state.getValue(ReflectorBlock.HAS_LAMP) ? 15 : 0)));

    // Tobacco Crop
    public static final DeferredBlock<Block> TOBACCO_CROP = BLOCKS.register("tobacco_crop", () ->
            new TobaccoCropBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT).noOcclusion().noCollission(), ModItems.TOBACCO_SEEDS));


    // Weed Crops
    public static final DeferredBlock<Block> HEMP_CROP = BLOCKS.register("hemp_crop", () ->
            new BaseWeedCropBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT).noOcclusion().noCollission(), ModItems.HEMP_SEEDS));


    private static BlockBehaviour.Properties props() {
        return BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT).noOcclusion().noCollission();
    }

    // 1-2 fertilizers needed (5 crops)
    public static final DeferredBlock<Block> WHITE_WIDOW_CROP = BLOCKS.register("white_widow_crop", () ->
            new BaseWeedCropBlock(props(), ModItems.WHITE_WIDOW_SEEDS, 9, 6, 11, 6, 15, 10)); // diff: (2,2,-2)
    public static final DeferredBlock<Block> BUBBLE_KUSH_CROP = BLOCKS.register("bubble_kush_crop", () ->
            new BaseWeedCropBlock(props(), ModItems.BUBBLE_KUSH_SEEDS, 10, 8, 16, 7, 20, 5)); // diff: (2,2,2)
    public static final DeferredBlock<Block> PURPLE_HAZE_CROP = BLOCKS.register("purple_haze_crop", () ->
            new BaseWeedCropBlock(props(), ModItems.PURPLE_HAZE_SEEDS, 8, 11, 6, 7, 16, 9)); // diff: (2,2,1)
    public static final DeferredBlock<Block> AMNESIA_HAZE_CROP = BLOCKS.register("amnesia_haze_crop", () ->
            new BaseWeedCropBlock(props(), ModItems.AMNESIA_HAZE_SEEDS, 8, 11, 7, 6, 19, 6)); // diff: (2,2,2)
    public static final DeferredBlock<Block> BLUE_COOKIES_CROP = BLOCKS.register("blue_cookies_crop", () ->
            new BaseWeedCropBlock(props(), ModItems.BLUE_COOKIES_SEEDS, 8, 11, 7, 6, 17, 8)); // diff: (2,2,2)

    // 2-3 fertilizers needed (10 crops)
    public static final DeferredBlock<Block> LEMON_HAZE_CROP = BLOCKS.register("lemon_haze_crop", () ->
            new BaseWeedCropBlock(props(), ModItems.LEMON_HAZE_SEEDS, 7, 10, 12, 6, 19, 6)); // diff: (2,2,3)
    public static final DeferredBlock<Block> SOUR_DIESEL_CROP = BLOCKS.register("sour_diesel_crop", () ->
            new BaseWeedCropBlock(props(), ModItems.SOUR_DIESEL_SEEDS, 9, 11, 8, 6, 19, 6)); // diff: (2,2,2)
    public static final DeferredBlock<Block> JACK_HERER_CROP = BLOCKS.register("jack_herer_crop", () ->
            new BaseWeedCropBlock(props(), ModItems.JACK_HERER_SEEDS, 8, 12, 9, 7, 18, 7)); // diff: (2,2,2)
    public static final DeferredBlock<Block> GHOST_TRAIN_CROP = BLOCKS.register("ghost_train_crop", () ->
            new BaseWeedCropBlock(props(), ModItems.GHOST_TRAIN_SEEDS, 11, 10, 8, 6, 19, 6)); // diff: (2,2,2)
    public static final DeferredBlock<Block> GRAPE_APE_CROP = BLOCKS.register("grape_ape_crop", () ->
            new BaseWeedCropBlock(props(), ModItems.GRAPE_APE_SEEDS, 13, 11, 12, 7, 18, 7)); // diff: (2,2,2)
    public static final DeferredBlock<Block> COTTON_CANDY_CROP = BLOCKS.register("cotton_candy_crop", () ->
            new BaseWeedCropBlock(props(), ModItems.COTTON_CANDY_SEEDS, 7, 11, 8, 7, 19, 6)); // diff: (2,2,2)
    public static final DeferredBlock<Block> AFGHANI_CROP = BLOCKS.register("afghani_crop", () ->
            new BaseWeedCropBlock(props(), ModItems.AFGHANI_SEEDS, 9, 8, 15, 6, 18, 7)); // diff: (2,2,2)
    public static final DeferredBlock<Block> LAVA_CAKE_CROP = BLOCKS.register("lava_cake_crop", () ->
            new BaseWeedCropBlock(props(), ModItems.LAVA_CAKE_SEEDS, 12, 10, 9, 6, 22, 3)); // diff: (2,2,2)
    public static final DeferredBlock<Block> JELLY_RANCHER_CROP = BLOCKS.register("jelly_rancher_crop", () ->
            new BaseWeedCropBlock(props(), ModItems.JELLY_RANCHER_SEEDS, 8, 12, 8, 6, 20, 5)); // diff: (2,2,2)
    public static final DeferredBlock<Block> STRAWBERRY_SHORTCAKE_CROP = BLOCKS.register("strawberry_shortcake_crop", () ->
            new BaseWeedCropBlock(props(), ModItems.STRAWBERRY_SHORTCAKE_SEEDS, 13, 10, 8, 6, 16, 9)); // diff: (2,2,2)

    // 3-4 fertilizers needed (6 crops)
    public static final DeferredBlock<Block> BLUE_ICE_CROP = BLOCKS.register("blue_ice_crop", () ->
            new BaseWeedCropBlock(props(), ModItems.BLUE_ICE_SEEDS, 10, 4, 10, 6, 20, 5)); // diff: (4,2,2)
    public static final DeferredBlock<Block> BUBBLEGUM_CROP = BLOCKS.register("bubblegum_crop", () ->
            new BaseWeedCropBlock(props(), ModItems.BUBBLEGUM_SEEDS, 12, 7, 11, 7, 17, 8)); // diff: (2,2,2)
    public static final DeferredBlock<Block> GARY_PEYTON_CROP = BLOCKS.register("gary_peyton_crop", () ->
            new BaseWeedCropBlock(props(), ModItems.GARY_PEYTON_SEEDS, 11, 8, 8, 6, 22, 3)); // diff: (4,4,2)
    public static final DeferredBlock<Block> AK47_CROP = BLOCKS.register("ak47_crop", () ->
            new BaseWeedCropBlock(props(), ModItems.AK47_SEEDS, 14, 4, 6, 7, 19, 6)); // diff: (4,2,2)
    public static final DeferredBlock<Block> BANANA_KUSH_CROP = BLOCKS.register("banana_kush_crop", () ->
            new BaseWeedCropBlock(props(), ModItems.BANANA_KUSH_SEEDS, 11, 8, 11, 6, 21, 4)); // diff: (4,2,2)
    public static final DeferredBlock<Block> PINK_KUSH_CROP = BLOCKS.register("pink_kush_crop", () ->
            new BaseWeedCropBlock(props(), ModItems.PINK_KUSH_SEEDS, 14, 5, 7, 6, 19, 6)); // diff: (4,2,3)

    // 5 fertilizers needed (4 crops)
    public static final DeferredBlock<Block> OG_KUSH_CROP = BLOCKS.register("og_kush_crop", () ->
            new BaseWeedCropBlock(props(), ModItems.OG_KUSH_SEEDS, 10, 7, 12, 6, 25, 0)); // diff: (4,4,2)
    public static final DeferredBlock<Block> CARBON_FIBER_CROP = BLOCKS.register("carbon_fiber_crop", () ->
            new BaseWeedCropBlock(props(), ModItems.CARBON_FIBER_SEEDS, 13, 5, 13, 6, 24, 1)); // diff: (4,4,2)
    public static final DeferredBlock<Block> BIRTHDAY_CAKE_CROP = BLOCKS.register("birthday_cake_crop", () ->
            new BaseWeedCropBlock(props(), ModItems.BIRTHDAY_CAKE_SEEDS, 9, 7, 13, 7, 23, 2)); // diff: (4,4,2)
    public static final DeferredBlock<Block> MOONBOW_CROP = BLOCKS.register("moonbow_crop", () ->
            new BaseWeedCropBlock(props(), ModItems.MOONBOW_SEEDS, 14, 1, 17, 6, 30, 13)); // diff: (4,2,5)



    // Machines
    public static final DeferredBlock<Block> GENERATOR = registerBlock("generator", () -> new GeneratorBlock(BlockBehaviour.Properties.of().strength(1f).requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> GRINDER = registerBlock("grinder", () -> new GrinderBlock(BlockBehaviour.Properties.of().strength(1f).requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> EXTRACTOR = registerBlock("extractor", () -> new ExtractorBlock(BlockBehaviour.Properties.of().strength(1f).requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> LIQUIFIER = registerBlock("liquifier", () -> new LiquifierBlock(BlockBehaviour.Properties.of().strength(1f).requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> MUTATOR = registerBlock("mutator", () -> new MutatorBlock(BlockBehaviour.Properties.of().strength(1f).requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> SYNTHESIZER = registerBlock("synthesizer", () -> new SynthesizerBlock(BlockBehaviour.Properties.of().strength(1f).requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> SEQUENCER = registerBlock("sequencer", () -> new SequencerBlock(BlockBehaviour.Properties.of().strength(1f).requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> DRYER = registerBlock("dryer", () -> new DryerBlock(BlockBehaviour.Properties.of().strength(1f).requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> MIXER = registerBlock("mixer", () -> new MixerBlock(BlockBehaviour.Properties.of().strength(1f).requiresCorrectToolForDrops()));


    // Utility Blocks
    public static final DeferredBlock<Block> DRYING_RACK = registerBlock("drying_rack", () -> new DryingRackBlock(BlockBehaviour.Properties.of().strength(1f).noOcclusion().requiresCorrectToolForDrops()));



    // Helper Functions
    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block) {
        DeferredBlock<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }
    public static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block) {
        ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    // Register
    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }

}
