package net.micaxs.smokeleaf.block;

import net.micaxs.smokeleaf.SmokeleafIndustries;
import net.micaxs.smokeleaf.block.custom.*;
import net.micaxs.smokeleaf.item.ModItems;
import net.micaxs.smokeleaf.item.custom.TooltipBlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(SmokeleafIndustries.MODID);


    public static final DeferredBlock<Block> HEMP_STONE = registerBlock("hemp_stone",
            props -> new Block(props), () -> BlockBehaviour.Properties.of()
                    .strength(1f).requiresCorrectToolForDrops().sound(SoundType.STONE));
    public static final DeferredBlock<Block> HEMP_STONE_STAIRS = registerBlock("hemp_stone_stairs",
            props -> new StairBlock(ModBlocks.HEMP_STONE.get().defaultBlockState(), props), () -> BlockBehaviour.Properties.of()
                    .strength(1f).requiresCorrectToolForDrops().sound(SoundType.STONE));
    public static final DeferredBlock<Block> HEMP_STONE_SLAB = registerBlock("hemp_stone_slab",
            props -> new SlabBlock(props), () -> BlockBehaviour.Properties.of()
                    .strength(1f).requiresCorrectToolForDrops().sound(SoundType.STONE));
    public static final DeferredBlock<Block> HEMP_STONE_PRESSURE_PLATE = registerBlock("hemp_stone_pressure_plate",
            props -> new PressurePlateBlock(BlockSetType.STONE, props), () -> BlockBehaviour.Properties.of()
                    .strength(1f).requiresCorrectToolForDrops().sound(SoundType.STONE));
    public static final DeferredBlock<Block> HEMP_STONE_BUTTON = registerBlock("hemp_stone_button",
            props -> new ButtonBlock(BlockSetType.STONE, 10, props), () -> BlockBehaviour.Properties.of().noCollision()
                    .strength(1f).requiresCorrectToolForDrops().sound(SoundType.STONE));
    public static final DeferredBlock<Block> HEMP_STONE_WALL = registerBlock("hemp_stone_wall",
            props -> new WallBlock(props), () -> BlockBehaviour.Properties.of()
                    .strength(1f).requiresCorrectToolForDrops().sound(SoundType.STONE));


    public static final DeferredBlock<Block> HEMP_PLANKS = registerBlock("hemp_planks",
            props -> new Block(props), () -> BlockBehaviour.Properties.of()
                    .strength(1f).requiresCorrectToolForDrops().sound(SoundType.WOOD));
    public static final DeferredBlock<Block> HEMP_PLANK_STAIRS = registerBlock("hemp_plank_stairs",
            props -> new StairBlock(ModBlocks.HEMP_PLANKS.get().defaultBlockState(), props), () -> BlockBehaviour.Properties.of()
                    .strength(1f).requiresCorrectToolForDrops().sound(SoundType.WOOD));
    public static final DeferredBlock<Block> HEMP_PLANK_SLAB = registerBlock("hemp_plank_slab",
            props -> new SlabBlock(props), () -> BlockBehaviour.Properties.of()
                    .strength(1f).requiresCorrectToolForDrops().sound(SoundType.WOOD));
    public static final DeferredBlock<Block> HEMP_PLANK_PRESSURE_PLATE = registerBlock("hemp_plank_pressure_plate",
            props -> new PressurePlateBlock(BlockSetType.OAK, props), () -> BlockBehaviour.Properties.of()
                    .strength(1f).requiresCorrectToolForDrops().sound(SoundType.WOOD));
    public static final DeferredBlock<Block> HEMP_PLANK_BUTTON = registerBlock("hemp_plank_button",
            props -> new ButtonBlock(BlockSetType.OAK, 10, props), () -> BlockBehaviour.Properties.of().noCollision()
                    .strength(1f).requiresCorrectToolForDrops().sound(SoundType.WOOD));
    public static final DeferredBlock<Block> HEMP_PLANK_FENCE = registerBlock("hemp_plank_fence",
            props -> new FenceBlock(props), () -> BlockBehaviour.Properties.of()
                    .strength(1f).requiresCorrectToolForDrops().sound(SoundType.WOOD));
    public static final DeferredBlock<Block> HEMP_PLANK_FENCE_GATE = registerBlock("hemp_plank_fence_gate",
            props -> new FenceGateBlock(WoodType.OAK, props), () -> BlockBehaviour.Properties.of()
                    .strength(1f).requiresCorrectToolForDrops().sound(SoundType.WOOD));
    public static final DeferredBlock<Block> HEMP_PLANK_DOOR = registerBlock("hemp_plank_door",
            props -> new DoorBlock(BlockSetType.OAK, props), () -> BlockBehaviour.Properties.of().noOcclusion()
                    .strength(1f).requiresCorrectToolForDrops().sound(SoundType.WOOD));
    public static final DeferredBlock<Block> HEMP_PLANK_TRAPDOOR = registerBlock("hemp_plank_trapdoor",
            props -> new TrapDoorBlock(BlockSetType.OAK, props), () -> BlockBehaviour.Properties.of().noOcclusion()
                    .strength(1f).requiresCorrectToolForDrops().sound(SoundType.WOOD));


    public static final DeferredBlock<Block> HEMP_BRICKS = registerBlock("hemp_bricks",
            props -> new Block(props), () -> BlockBehaviour.Properties.of()
                    .strength(1f).requiresCorrectToolForDrops().sound(SoundType.STONE));
    public static final DeferredBlock<Block> HEMP_BRICK_STAIRS = registerBlock("hemp_brick_stairs",
            props -> new StairBlock(ModBlocks.HEMP_BRICKS.get().defaultBlockState(), props), () -> BlockBehaviour.Properties.of()
                    .strength(1f).requiresCorrectToolForDrops().sound(SoundType.STONE));
    public static final DeferredBlock<Block> HEMP_BRICK_SLAB = registerBlock("hemp_brick_slab",
            props -> new SlabBlock(props), () -> BlockBehaviour.Properties.of()
                    .strength(1f).requiresCorrectToolForDrops().sound(SoundType.STONE));
    public static final DeferredBlock<Block> HEMP_BRICK_WALL = registerBlock("hemp_brick_wall",
            props -> new WallBlock(props), () -> BlockBehaviour.Properties.of()
                    .strength(1f).requiresCorrectToolForDrops().sound(SoundType.STONE));


    public static final DeferredBlock<Block> HEMP_CHISELED_STONE = registerBlock("hemp_chiseled_stone",
            props -> new Block(props), () -> BlockBehaviour.Properties.of()
                    .strength(1f).requiresCorrectToolForDrops().sound(SoundType.STONE));
    public static final DeferredBlock<Block> HEMP_CHISELED_STONE_STAIRS = registerBlock("hemp_chiseled_stone_stairs",
            props -> new StairBlock(ModBlocks.HEMP_CHISELED_STONE.get().defaultBlockState(), props), () -> BlockBehaviour.Properties.of()
                    .strength(1f).requiresCorrectToolForDrops().sound(SoundType.STONE));
    public static final DeferredBlock<Block> HEMP_CHISELED_STONE_SLAB = registerBlock("hemp_chiseled_stone_slab",
            props -> new SlabBlock(props), () -> BlockBehaviour.Properties.of()
                    .strength(1f).requiresCorrectToolForDrops().sound(SoundType.STONE));
    public static final DeferredBlock<Block> HEMP_CHISELED_STONE_WALL = registerBlock("hemp_chiseled_stone_wall",
            props -> new WallBlock(props), () -> BlockBehaviour.Properties.of()
                    .strength(1f).requiresCorrectToolForDrops().sound(SoundType.STONE));


    public static final DeferredBlock<Block> GROW_POT = registerBlock("grow_pot",
            props -> new GrowPotBlock(props), () -> BlockBehaviour.Properties.of().requiresCorrectToolForDrops()
                    .strength(1f).noOcclusion().sound(SoundType.METAL));


    public static final DeferredBlock<Block> LED_LIGHT = registerBlock("led_light",
            props -> new GrowLightBlock(props), () -> BlockBehaviour.Properties.of().requiresCorrectToolForDrops()
                    .strength(1f).lightLevel(state -> state.getValue(GrowLightBlock.CLICKED) ? 15 : 0).noOcclusion().sound(SoundType.GLASS));

    public static final DeferredBlock<Block> REFLECTOR = registerBlock("reflector",
            props -> new ReflectorBlock(props), () -> BlockBehaviour.Properties.of().requiresCorrectToolForDrops()
                    .strength(1f).noOcclusion().sound(SoundType.METAL)
                    .lightLevel(state -> state.getValue(ReflectorBlock.HAS_LAMP) ? 15 : 0));

    // Tobacco Crop
    public static final DeferredBlock<Block> TOBACCO_CROP = BLOCKS.registerBlock("tobacco_crop", props -> new TobaccoCropBlock(props, ModItems.TOBACCO_SEEDS), () -> cropProperties());


    // Weed Crops
    public static final DeferredBlock<Block> HEMP_CROP = BLOCKS.registerBlock("hemp_crop", props -> new BaseWeedCropBlock(props, ModItems.HEMP_SEEDS), () -> cropProperties());

    // Player-made strain crop
    public static final DeferredBlock<Block> UNIDENTIFIED_WEED_CROP = BLOCKS.registerBlock("unidentified_weed_crop", props -> new UnidentifiedWeedCropBlock(props), () -> cropProperties());


    // Machines
    public static final DeferredBlock<Block> GENERATOR = registerBlock("generator", props -> new GeneratorBlock(props), () -> BlockBehaviour.Properties.of().strength(1f).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> GRINDER = registerBlock("grinder", props -> new GrinderBlock(props), () -> BlockBehaviour.Properties.of().strength(1f).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> EXTRACTOR = registerBlock("extractor", props -> new ExtractorBlock(props), () -> BlockBehaviour.Properties.of().strength(1f).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> LIQUIFIER = registerBlock("liquifier", props -> new LiquifierBlock(props), () -> BlockBehaviour.Properties.of().strength(1f).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> MUTATOR = registerBlock("mutator", props -> new MutatorBlock(props), () -> BlockBehaviour.Properties.of().strength(1f).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> SYNTHESIZER = registerBlock("synthesizer", props -> new SynthesizerBlock(props), () -> BlockBehaviour.Properties.of().strength(1f).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> SEQUENCER = registerBlock("sequencer", props -> new SequencerBlock(props), () -> BlockBehaviour.Properties.of().strength(1f).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> DRYER = registerBlock("dryer", props -> new DryerBlock(props), () -> BlockBehaviour.Properties.of().strength(1f).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> MIXER = registerBlock("mixer", props -> new MixerBlock(props), () -> BlockBehaviour.Properties.of().strength(1f).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> STRAIN_MODIFIER = registerBlock("strain_modifier", props -> new StrainModifierBlock(props), () -> BlockBehaviour.Properties.of().strength(1f).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> GUMMY_MACHINE = registerBlock("gummy_machine", props -> new GummyMachineBlock(props), () -> BlockBehaviour.Properties.of().strength(1f).requiresCorrectToolForDrops());

    // Pipe — registered directly (not via registerBlock helper) since it has no default BlockItem;
    // the 3 pipe types are separate PipeItems (see ModItems.java) that merge into this same block.
    public static final DeferredBlock<Block> PIPE = BLOCKS.registerBlock("pipe",
            props -> new LogisticsPipeBlock(props), () -> BlockBehaviour.Properties.of().strength(1f).noOcclusion().requiresCorrectToolForDrops());


    // Utility Blocks
    public static final DeferredBlock<Block> DRYING_RACK = registerBlock("drying_rack", props -> new DryingRackBlock(props), () -> BlockBehaviour.Properties.of().strength(1f).noOcclusion().requiresCorrectToolForDrops());


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
                    props -> new Block(props), () -> BlockBehaviour.Properties.of()
                            .strength(0.8f).sound(SoundType.WOOL).ignitedByLava()));
        }
        return map;
    }


    // Helper Functions

    /**
     * Wheat-like crop properties. (Copying Wheat's properties no longer works: its map color is a
     * function of Wheat's own age property, which other crop blocks don't have.)
     */
    private static BlockBehaviour.Properties cropProperties() {
        return BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollision().noOcclusion().randomTicks()
                .instabreak().sound(SoundType.CROP).pushReaction(PushReaction.DESTROY);
    }
    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Function<BlockBehaviour.Properties, T> block,
                                                                    Supplier<BlockBehaviour.Properties> properties) {
        DeferredBlock<T> toReturn = BLOCKS.registerBlock(name, block, properties);
        registerBlockItem(name, toReturn);
        return toReturn;
    }
    public static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block) {
        if (name.equals("grow_pot")) {
            ModItems.ITEMS.registerItem(name, props -> new TooltipBlockItem(block.get(), props.useBlockDescriptionPrefix(), TooltipBlockItem.growPotLines()));
            return;
        }
        ModItems.ITEMS.registerSimpleBlockItem(name, block);
    }

    // Register
    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }

}
