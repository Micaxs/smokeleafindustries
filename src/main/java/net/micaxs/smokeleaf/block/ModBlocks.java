package net.micaxs.smokeleaf.block;

import net.micaxs.smokeleaf.SmokeleafIndustries;
import net.micaxs.smokeleaf.block.custom.*;
import net.micaxs.smokeleaf.item.ModItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.EnumMap;
import java.util.Map;
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

    // Player-made strain crop
    public static final DeferredBlock<Block> UNIDENTIFIED_WEED_CROP = BLOCKS.register("unidentified_weed_crop", () ->
            new UnidentifiedWeedCropBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT).noOcclusion().noCollission()));


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
    public static final DeferredBlock<Block> STRAIN_MODIFIER = registerBlock("strain_modifier", () -> new StrainModifierBlock(BlockBehaviour.Properties.of().strength(1f).requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> GUMMY_MACHINE = registerBlock("gummy_machine", () -> new GummyMachineBlock(BlockBehaviour.Properties.of().strength(1f).requiresCorrectToolForDrops()));

    // Pipe — registered directly (not via registerBlock helper) since it has no default BlockItem;
    // the 3 pipe types are separate PipeItems (see ModItems.java) that merge into this same block.
    public static final DeferredBlock<Block> PIPE = BLOCKS.register("pipe",
            () -> new LogisticsPipeBlock(BlockBehaviour.Properties.of().strength(1f).noOcclusion().requiresCorrectToolForDrops()));


    // Utility Blocks
    public static final DeferredBlock<Block> DRYING_RACK = registerBlock("drying_rack", () -> new DryingRackBlock(BlockBehaviour.Properties.of().strength(1f).noOcclusion().requiresCorrectToolForDrops()));


    // Hemp Wool — one block per DyeColor, dyed the same way as vanilla wool. WHITE is the
    // plain "hemp_wool" produced by the base recipe; every other color is named
    // "<color>_hemp_wool" to match vanilla's own wool naming.
    public static final Map<DyeColor, DeferredBlock<Block>> HEMP_WOOL = registerHempWool();

    public static String hempWoolName(DyeColor color) {
        return color == DyeColor.WHITE ? "hemp_wool" : color.getSerializedName() + "_hemp_wool";
    }

    private static Map<DyeColor, DeferredBlock<Block>> registerHempWool() {
        Map<DyeColor, DeferredBlock<Block>> map = new EnumMap<>(DyeColor.class);
        for (DyeColor color : DyeColor.values()) {
            map.put(color, registerBlock(hempWoolName(color),
                    () -> new Block(BlockBehaviour.Properties.of()
                            .strength(0.8f).sound(SoundType.WOOL).ignitedByLava())));
        }
        return map;
    }


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
