package net.micaxs.smokeleaf.fluid;

import net.micaxs.smokeleaf.SmokeleafIndustries;
import net.micaxs.smokeleaf.block.ModBlocks;
import net.micaxs.smokeleaf.item.ModItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModFluids {
    public static final DeferredRegister<Fluid> FLUIDS =
            DeferredRegister.create(BuiltInRegistries.FLUID, SmokeleafIndustries.MODID);

    // Hemp Oil Fluid
    public static final Supplier<FlowingFluid> SOURCE_HEMP_OIL_FLUID = FLUIDS.register("hemp_oil_fluid",
            () -> new BaseFlowingFluid.Source(ModFluids.HEMP_OIL_FLUID_PROPERTIES));
    public static final Supplier<FlowingFluid> FLOWING_HEMP_OIL_FLUID = FLUIDS.register("flowing_hemp_oil",
            () -> new BaseFlowingFluid.Flowing(ModFluids.HEMP_OIL_FLUID_PROPERTIES));
    public static final DeferredBlock<LiquidBlock> HEMP_OIL_FLUID_BLOCK = ModBlocks.BLOCKS.register("hemp_oil_fluid_block",
            () -> new LiquidBlock(ModFluids.SOURCE_HEMP_OIL_FLUID.get(), BlockBehaviour.Properties.ofFullCopy(Blocks.WATER).noLootTable()));
    public static final DeferredItem<Item> HEMP_OIL_BUCKET = ModItems.ITEMS.registerItem("hemp_oil_bucket",
            properties -> new BucketItem(ModFluids.SOURCE_HEMP_OIL_FLUID.get(), properties.craftRemainder(Items.BUCKET).stacksTo(1)));
    public static final BaseFlowingFluid.Properties HEMP_OIL_FLUID_PROPERTIES = new BaseFlowingFluid.Properties(
            ModFluidTypes.HEMP_OIL_FLUID_TYPE, SOURCE_HEMP_OIL_FLUID, FLOWING_HEMP_OIL_FLUID)
            .slopeFindDistance(2)
            .levelDecreasePerBlock(2);

    // Hash Oil Fluid
    public static final Supplier<FlowingFluid> SOURCE_HASH_OIL_FLUID = FLUIDS.register("hash_oil_fluid",
            () -> new BaseFlowingFluid.Source(ModFluids.HASH_OIL_FLUID_PROPERTIES));
    public static final Supplier<FlowingFluid> FLOWING_HASH_OIL_FLUID = FLUIDS.register("flowing_hash_oil",
            () -> new BaseFlowingFluid.Flowing(ModFluids.HASH_OIL_FLUID_PROPERTIES));
    public static final DeferredBlock<LiquidBlock> HASH_OIL_FLUID_BLOCK = ModBlocks.BLOCKS.register("hash_oil_fluid_block",
            () -> new LiquidBlock(ModFluids.SOURCE_HASH_OIL_FLUID.get(), BlockBehaviour.Properties.ofFullCopy(Blocks.WATER).noLootTable()));
    public static final DeferredItem<Item> HASH_OIL_BUCKET = ModItems.ITEMS.registerItem("hash_oil_bucket",
            properties -> new BucketItem(ModFluids.SOURCE_HASH_OIL_FLUID.get(), properties.craftRemainder(Items.BUCKET).stacksTo(1)));
    public static final BaseFlowingFluid.Properties HASH_OIL_FLUID_PROPERTIES = new BaseFlowingFluid.Properties(
            ModFluidTypes.HASH_OIL_FLUID_TYPE, SOURCE_HASH_OIL_FLUID, FLOWING_HASH_OIL_FLUID)
            .slopeFindDistance(2)
            .levelDecreasePerBlock(2);

    // Hash Oil Sludge Fluid
    public static final Supplier<FlowingFluid> SOURCE_HASH_OIL_SLUDGE_FLUID = FLUIDS.register("hash_oil_sludge_fluid",
            () -> new BaseFlowingFluid.Source(ModFluids.HASH_OIL_SLUDGE_FLUID_PROPERTIES));
    public static final Supplier<FlowingFluid> FLOWING_HASH_OIL_SLUDGE_FLUID = FLUIDS.register("flowing_hash_oil_sludge",
            () -> new BaseFlowingFluid.Flowing(ModFluids.HASH_OIL_SLUDGE_FLUID_PROPERTIES));
    public static final DeferredBlock<LiquidBlock> HASH_OIL_SLUDGE_FLUID_BLOCK = ModBlocks.BLOCKS.register("hash_oil_sludge_fluid_block",
            () -> new LiquidBlock(ModFluids.SOURCE_HASH_OIL_SLUDGE_FLUID.get(), BlockBehaviour.Properties.ofFullCopy(Blocks.WATER).noLootTable()));
    public static final DeferredItem<Item> HASH_OIL_SLUDGE_BUCKET = ModItems.ITEMS.registerItem("hash_oil_sludge_bucket",
            properties -> new BucketItem(ModFluids.SOURCE_HASH_OIL_SLUDGE_FLUID.get(), properties.craftRemainder(Items.BUCKET).stacksTo(1)));
    public static final BaseFlowingFluid.Properties HASH_OIL_SLUDGE_FLUID_PROPERTIES = new BaseFlowingFluid.Properties(
            ModFluidTypes.HASH_OIL_SLUDGE_FLUID_TYPE, SOURCE_HASH_OIL_SLUDGE_FLUID, FLOWING_HASH_OIL_SLUDGE_FLUID)
            .slopeFindDistance(2)
            .levelDecreasePerBlock(2);


    // ---- Weed Extract Fluids ----

    public static final Supplier<FlowingFluid> SOURCE_WHITE_WIDOW_EXTRACT_FLUID = FLUIDS.register("white_widow_extract_fluid",
            () -> new BaseFlowingFluid.Source(ModFluids.WHITE_WIDOW_EXTRACT_FLUID_PROPERTIES));
    public static final Supplier<FlowingFluid> FLOWING_WHITE_WIDOW_EXTRACT_FLUID = FLUIDS.register("flowing_white_widow_extract",
            () -> new BaseFlowingFluid.Flowing(ModFluids.WHITE_WIDOW_EXTRACT_FLUID_PROPERTIES));
    public static final DeferredBlock<LiquidBlock> WHITE_WIDOW_EXTRACT_FLUID_BLOCK = ModBlocks.BLOCKS.register("white_widow_extract_fluid_block",
            () -> new LiquidBlock(ModFluids.SOURCE_WHITE_WIDOW_EXTRACT_FLUID.get(), BlockBehaviour.Properties.ofFullCopy(Blocks.WATER).noLootTable()));
    public static final DeferredItem<Item> WHITE_WIDOW_EXTRACT_BUCKET = ModItems.ITEMS.registerItem("white_widow_extract_bucket",
            properties -> new BucketItem(ModFluids.SOURCE_WHITE_WIDOW_EXTRACT_FLUID.get(), properties.craftRemainder(Items.BUCKET).stacksTo(1)));
    public static final BaseFlowingFluid.Properties WHITE_WIDOW_EXTRACT_FLUID_PROPERTIES = new BaseFlowingFluid.Properties(
            ModFluidTypes.WHITE_WIDOW_EXTRACT_FLUID_TYPE, SOURCE_WHITE_WIDOW_EXTRACT_FLUID, FLOWING_WHITE_WIDOW_EXTRACT_FLUID)
            .slopeFindDistance(2)
            .levelDecreasePerBlock(2);

    public static final Supplier<FlowingFluid> SOURCE_BUBBLE_KUSH_EXTRACT_FLUID = FLUIDS.register("bubble_kush_extract_fluid",
            () -> new BaseFlowingFluid.Source(ModFluids.BUBBLE_KUSH_EXTRACT_FLUID_PROPERTIES));
    public static final Supplier<FlowingFluid> FLOWING_BUBBLE_KUSH_EXTRACT_FLUID = FLUIDS.register("flowing_bubble_kush_extract",
            () -> new BaseFlowingFluid.Flowing(ModFluids.BUBBLE_KUSH_EXTRACT_FLUID_PROPERTIES));
    public static final DeferredBlock<LiquidBlock> BUBBLE_KUSH_EXTRACT_FLUID_BLOCK = ModBlocks.BLOCKS.register("bubble_kush_extract_fluid_block",
            () -> new LiquidBlock(ModFluids.SOURCE_BUBBLE_KUSH_EXTRACT_FLUID.get(), BlockBehaviour.Properties.ofFullCopy(Blocks.WATER).noLootTable()));
    public static final DeferredItem<Item> BUBBLE_KUSH_EXTRACT_BUCKET = ModItems.ITEMS.registerItem("bubble_kush_extract_bucket",
            properties -> new BucketItem(ModFluids.SOURCE_BUBBLE_KUSH_EXTRACT_FLUID.get(), properties.craftRemainder(Items.BUCKET).stacksTo(1)));
    public static final BaseFlowingFluid.Properties BUBBLE_KUSH_EXTRACT_FLUID_PROPERTIES = new BaseFlowingFluid.Properties(
            ModFluidTypes.BUBBLE_KUSH_EXTRACT_FLUID_TYPE, SOURCE_BUBBLE_KUSH_EXTRACT_FLUID, FLOWING_BUBBLE_KUSH_EXTRACT_FLUID)
            .slopeFindDistance(2)
            .levelDecreasePerBlock(2);

    public static final Supplier<FlowingFluid> SOURCE_LEMON_HAZE_EXTRACT_FLUID = FLUIDS.register("lemon_haze_extract_fluid",
            () -> new BaseFlowingFluid.Source(ModFluids.LEMON_HAZE_EXTRACT_FLUID_PROPERTIES));
    public static final Supplier<FlowingFluid> FLOWING_LEMON_HAZE_EXTRACT_FLUID = FLUIDS.register("flowing_lemon_haze_extract",
            () -> new BaseFlowingFluid.Flowing(ModFluids.LEMON_HAZE_EXTRACT_FLUID_PROPERTIES));
    public static final DeferredBlock<LiquidBlock> LEMON_HAZE_EXTRACT_FLUID_BLOCK = ModBlocks.BLOCKS.register("lemon_haze_extract_fluid_block",
            () -> new LiquidBlock(ModFluids.SOURCE_LEMON_HAZE_EXTRACT_FLUID.get(), BlockBehaviour.Properties.ofFullCopy(Blocks.WATER).noLootTable()));
    public static final DeferredItem<Item> LEMON_HAZE_EXTRACT_BUCKET = ModItems.ITEMS.registerItem("lemon_haze_extract_bucket",
            properties -> new BucketItem(ModFluids.SOURCE_LEMON_HAZE_EXTRACT_FLUID.get(), properties.craftRemainder(Items.BUCKET).stacksTo(1)));
    public static final BaseFlowingFluid.Properties LEMON_HAZE_EXTRACT_FLUID_PROPERTIES = new BaseFlowingFluid.Properties(
            ModFluidTypes.LEMON_HAZE_EXTRACT_FLUID_TYPE, SOURCE_LEMON_HAZE_EXTRACT_FLUID, FLOWING_LEMON_HAZE_EXTRACT_FLUID)
            .slopeFindDistance(2)
            .levelDecreasePerBlock(2);

    public static final Supplier<FlowingFluid> SOURCE_SOUR_DIESEL_EXTRACT_FLUID = FLUIDS.register("sour_diesel_extract_fluid",
            () -> new BaseFlowingFluid.Source(ModFluids.SOUR_DIESEL_EXTRACT_FLUID_PROPERTIES));
    public static final Supplier<FlowingFluid> FLOWING_SOUR_DIESEL_EXTRACT_FLUID = FLUIDS.register("flowing_sour_diesel_extract",
            () -> new BaseFlowingFluid.Flowing(ModFluids.SOUR_DIESEL_EXTRACT_FLUID_PROPERTIES));
    public static final DeferredBlock<LiquidBlock> SOUR_DIESEL_EXTRACT_FLUID_BLOCK = ModBlocks.BLOCKS.register("sour_diesel_extract_fluid_block",
            () -> new LiquidBlock(ModFluids.SOURCE_SOUR_DIESEL_EXTRACT_FLUID.get(), BlockBehaviour.Properties.ofFullCopy(Blocks.WATER).noLootTable()));
    public static final DeferredItem<Item> SOUR_DIESEL_EXTRACT_BUCKET = ModItems.ITEMS.registerItem("sour_diesel_extract_bucket",
            properties -> new BucketItem(ModFluids.SOURCE_SOUR_DIESEL_EXTRACT_FLUID.get(), properties.craftRemainder(Items.BUCKET).stacksTo(1)));
    public static final BaseFlowingFluid.Properties SOUR_DIESEL_EXTRACT_FLUID_PROPERTIES = new BaseFlowingFluid.Properties(
            ModFluidTypes.SOUR_DIESEL_EXTRACT_FLUID_TYPE, SOURCE_SOUR_DIESEL_EXTRACT_FLUID, FLOWING_SOUR_DIESEL_EXTRACT_FLUID)
            .slopeFindDistance(2)
            .levelDecreasePerBlock(2);

    public static final Supplier<FlowingFluid> SOURCE_BLUE_ICE_EXTRACT_FLUID = FLUIDS.register("blue_ice_extract_fluid",
            () -> new BaseFlowingFluid.Source(ModFluids.BLUE_ICE_EXTRACT_FLUID_PROPERTIES));
    public static final Supplier<FlowingFluid> FLOWING_BLUE_ICE_EXTRACT_FLUID = FLUIDS.register("flowing_blue_ice_extract",
            () -> new BaseFlowingFluid.Flowing(ModFluids.BLUE_ICE_EXTRACT_FLUID_PROPERTIES));
    public static final DeferredBlock<LiquidBlock> BLUE_ICE_EXTRACT_FLUID_BLOCK = ModBlocks.BLOCKS.register("blue_ice_extract_fluid_block",
            () -> new LiquidBlock(ModFluids.SOURCE_BLUE_ICE_EXTRACT_FLUID.get(), BlockBehaviour.Properties.ofFullCopy(Blocks.WATER).noLootTable()));
    public static final DeferredItem<Item> BLUE_ICE_EXTRACT_BUCKET = ModItems.ITEMS.registerItem("blue_ice_extract_bucket",
            properties -> new BucketItem(ModFluids.SOURCE_BLUE_ICE_EXTRACT_FLUID.get(), properties.craftRemainder(Items.BUCKET).stacksTo(1)));
    public static final BaseFlowingFluid.Properties BLUE_ICE_EXTRACT_FLUID_PROPERTIES = new BaseFlowingFluid.Properties(
            ModFluidTypes.BLUE_ICE_EXTRACT_FLUID_TYPE, SOURCE_BLUE_ICE_EXTRACT_FLUID, FLOWING_BLUE_ICE_EXTRACT_FLUID)
            .slopeFindDistance(2)
            .levelDecreasePerBlock(2);

    public static final Supplier<FlowingFluid> SOURCE_BUBBLEGUM_EXTRACT_FLUID = FLUIDS.register("bubblegum_extract_fluid",
            () -> new BaseFlowingFluid.Source(ModFluids.BUBBLEGUM_EXTRACT_FLUID_PROPERTIES));
    public static final Supplier<FlowingFluid> FLOWING_BUBBLEGUM_EXTRACT_FLUID = FLUIDS.register("flowing_bubblegum_extract",
            () -> new BaseFlowingFluid.Flowing(ModFluids.BUBBLEGUM_EXTRACT_FLUID_PROPERTIES));
    public static final DeferredBlock<LiquidBlock> BUBBLEGUM_EXTRACT_FLUID_BLOCK = ModBlocks.BLOCKS.register("bubblegum_extract_fluid_block",
            () -> new LiquidBlock(ModFluids.SOURCE_BUBBLEGUM_EXTRACT_FLUID.get(), BlockBehaviour.Properties.ofFullCopy(Blocks.WATER).noLootTable()));
    public static final DeferredItem<Item> BUBBLEGUM_EXTRACT_BUCKET = ModItems.ITEMS.registerItem("bubblegum_extract_bucket",
            properties -> new BucketItem(ModFluids.SOURCE_BUBBLEGUM_EXTRACT_FLUID.get(), properties.craftRemainder(Items.BUCKET).stacksTo(1)));
    public static final BaseFlowingFluid.Properties BUBBLEGUM_EXTRACT_FLUID_PROPERTIES = new BaseFlowingFluid.Properties(
            ModFluidTypes.BUBBLEGUM_EXTRACT_FLUID_TYPE, SOURCE_BUBBLEGUM_EXTRACT_FLUID, FLOWING_BUBBLEGUM_EXTRACT_FLUID)
            .slopeFindDistance(2)
            .levelDecreasePerBlock(2);

    public static final Supplier<FlowingFluid> SOURCE_PURPLE_HAZE_EXTRACT_FLUID = FLUIDS.register("purple_haze_extract_fluid",
            () -> new BaseFlowingFluid.Source(ModFluids.PURPLE_HAZE_EXTRACT_FLUID_PROPERTIES));
    public static final Supplier<FlowingFluid> FLOWING_PURPLE_HAZE_EXTRACT_FLUID = FLUIDS.register("flowing_purple_haze_extract",
            () -> new BaseFlowingFluid.Flowing(ModFluids.PURPLE_HAZE_EXTRACT_FLUID_PROPERTIES));
    public static final DeferredBlock<LiquidBlock> PURPLE_HAZE_EXTRACT_FLUID_BLOCK = ModBlocks.BLOCKS.register("purple_haze_extract_fluid_block",
            () -> new LiquidBlock(ModFluids.SOURCE_PURPLE_HAZE_EXTRACT_FLUID.get(), BlockBehaviour.Properties.ofFullCopy(Blocks.WATER).noLootTable()));
    public static final DeferredItem<Item> PURPLE_HAZE_EXTRACT_BUCKET = ModItems.ITEMS.registerItem("purple_haze_extract_bucket",
            properties -> new BucketItem(ModFluids.SOURCE_PURPLE_HAZE_EXTRACT_FLUID.get(), properties.craftRemainder(Items.BUCKET).stacksTo(1)));
    public static final BaseFlowingFluid.Properties PURPLE_HAZE_EXTRACT_FLUID_PROPERTIES = new BaseFlowingFluid.Properties(
            ModFluidTypes.PURPLE_HAZE_EXTRACT_FLUID_TYPE, SOURCE_PURPLE_HAZE_EXTRACT_FLUID, FLOWING_PURPLE_HAZE_EXTRACT_FLUID)
            .slopeFindDistance(2)
            .levelDecreasePerBlock(2);

    public static final Supplier<FlowingFluid> SOURCE_OG_KUSH_EXTRACT_FLUID = FLUIDS.register("og_kush_extract_fluid",
            () -> new BaseFlowingFluid.Source(ModFluids.OG_KUSH_EXTRACT_FLUID_PROPERTIES));
    public static final Supplier<FlowingFluid> FLOWING_OG_KUSH_EXTRACT_FLUID = FLUIDS.register("flowing_og_kush_extract",
            () -> new BaseFlowingFluid.Flowing(ModFluids.OG_KUSH_EXTRACT_FLUID_PROPERTIES));
    public static final DeferredBlock<LiquidBlock> OG_KUSH_EXTRACT_FLUID_BLOCK = ModBlocks.BLOCKS.register("og_kush_extract_fluid_block",
            () -> new LiquidBlock(ModFluids.SOURCE_OG_KUSH_EXTRACT_FLUID.get(), BlockBehaviour.Properties.ofFullCopy(Blocks.WATER).noLootTable()));
    public static final DeferredItem<Item> OG_KUSH_EXTRACT_BUCKET = ModItems.ITEMS.registerItem("og_kush_extract_bucket",
            properties -> new BucketItem(ModFluids.SOURCE_OG_KUSH_EXTRACT_FLUID.get(), properties.craftRemainder(Items.BUCKET).stacksTo(1)));
    public static final BaseFlowingFluid.Properties OG_KUSH_EXTRACT_FLUID_PROPERTIES = new BaseFlowingFluid.Properties(
            ModFluidTypes.OG_KUSH_EXTRACT_FLUID_TYPE, SOURCE_OG_KUSH_EXTRACT_FLUID, FLOWING_OG_KUSH_EXTRACT_FLUID)
            .slopeFindDistance(2)
            .levelDecreasePerBlock(2);

    public static final Supplier<FlowingFluid> SOURCE_JACK_HERER_EXTRACT_FLUID = FLUIDS.register("jack_herer_extract_fluid",
            () -> new BaseFlowingFluid.Source(ModFluids.JACK_HERER_EXTRACT_FLUID_PROPERTIES));
    public static final Supplier<FlowingFluid> FLOWING_JACK_HERER_EXTRACT_FLUID = FLUIDS.register("flowing_jack_herer_extract",
            () -> new BaseFlowingFluid.Flowing(ModFluids.JACK_HERER_EXTRACT_FLUID_PROPERTIES));
    public static final DeferredBlock<LiquidBlock> JACK_HERER_EXTRACT_FLUID_BLOCK = ModBlocks.BLOCKS.register("jack_herer_extract_fluid_block",
            () -> new LiquidBlock(ModFluids.SOURCE_JACK_HERER_EXTRACT_FLUID.get(), BlockBehaviour.Properties.ofFullCopy(Blocks.WATER).noLootTable()));
    public static final DeferredItem<Item> JACK_HERER_EXTRACT_BUCKET = ModItems.ITEMS.registerItem("jack_herer_extract_bucket",
            properties -> new BucketItem(ModFluids.SOURCE_JACK_HERER_EXTRACT_FLUID.get(), properties.craftRemainder(Items.BUCKET).stacksTo(1)));
    public static final BaseFlowingFluid.Properties JACK_HERER_EXTRACT_FLUID_PROPERTIES = new BaseFlowingFluid.Properties(
            ModFluidTypes.JACK_HERER_EXTRACT_FLUID_TYPE, SOURCE_JACK_HERER_EXTRACT_FLUID, FLOWING_JACK_HERER_EXTRACT_FLUID)
            .slopeFindDistance(2)
            .levelDecreasePerBlock(2);

    public static final Supplier<FlowingFluid> SOURCE_GARY_PEYTON_EXTRACT_FLUID = FLUIDS.register("gary_peyton_extract_fluid",
            () -> new BaseFlowingFluid.Source(ModFluids.GARY_PEYTON_EXTRACT_FLUID_PROPERTIES));
    public static final Supplier<FlowingFluid> FLOWING_GARY_PEYTON_EXTRACT_FLUID = FLUIDS.register("flowing_gary_peyton_extract",
            () -> new BaseFlowingFluid.Flowing(ModFluids.GARY_PEYTON_EXTRACT_FLUID_PROPERTIES));
    public static final DeferredBlock<LiquidBlock> GARY_PEYTON_EXTRACT_FLUID_BLOCK = ModBlocks.BLOCKS.register("gary_peyton_extract_fluid_block",
            () -> new LiquidBlock(ModFluids.SOURCE_GARY_PEYTON_EXTRACT_FLUID.get(), BlockBehaviour.Properties.ofFullCopy(Blocks.WATER).noLootTable()));
    public static final DeferredItem<Item> GARY_PEYTON_EXTRACT_BUCKET = ModItems.ITEMS.registerItem("gary_peyton_extract_bucket",
            properties -> new BucketItem(ModFluids.SOURCE_GARY_PEYTON_EXTRACT_FLUID.get(), properties.craftRemainder(Items.BUCKET).stacksTo(1)));
    public static final BaseFlowingFluid.Properties GARY_PEYTON_EXTRACT_FLUID_PROPERTIES = new BaseFlowingFluid.Properties(
            ModFluidTypes.GARY_PEYTON_EXTRACT_FLUID_TYPE, SOURCE_GARY_PEYTON_EXTRACT_FLUID, FLOWING_GARY_PEYTON_EXTRACT_FLUID)
            .slopeFindDistance(2)
            .levelDecreasePerBlock(2);

    public static final Supplier<FlowingFluid> SOURCE_AMNESIA_HAZE_EXTRACT_FLUID = FLUIDS.register("amnesia_haze_extract_fluid",
            () -> new BaseFlowingFluid.Source(ModFluids.AMNESIA_HAZE_EXTRACT_FLUID_PROPERTIES));
    public static final Supplier<FlowingFluid> FLOWING_AMNESIA_HAZE_EXTRACT_FLUID = FLUIDS.register("flowing_amnesia_haze_extract",
            () -> new BaseFlowingFluid.Flowing(ModFluids.AMNESIA_HAZE_EXTRACT_FLUID_PROPERTIES));
    public static final DeferredBlock<LiquidBlock> AMNESIA_HAZE_EXTRACT_FLUID_BLOCK = ModBlocks.BLOCKS.register("amnesia_haze_extract_fluid_block",
            () -> new LiquidBlock(ModFluids.SOURCE_AMNESIA_HAZE_EXTRACT_FLUID.get(), BlockBehaviour.Properties.ofFullCopy(Blocks.WATER).noLootTable()));
    public static final DeferredItem<Item> AMNESIA_HAZE_EXTRACT_BUCKET = ModItems.ITEMS.registerItem("amnesia_haze_extract_bucket",
            properties -> new BucketItem(ModFluids.SOURCE_AMNESIA_HAZE_EXTRACT_FLUID.get(), properties.craftRemainder(Items.BUCKET).stacksTo(1)));
    public static final BaseFlowingFluid.Properties AMNESIA_HAZE_EXTRACT_FLUID_PROPERTIES = new BaseFlowingFluid.Properties(
            ModFluidTypes.AMNESIA_HAZE_EXTRACT_FLUID_TYPE, SOURCE_AMNESIA_HAZE_EXTRACT_FLUID, FLOWING_AMNESIA_HAZE_EXTRACT_FLUID)
            .slopeFindDistance(2)
            .levelDecreasePerBlock(2);

    public static final Supplier<FlowingFluid> SOURCE_AK47_EXTRACT_FLUID = FLUIDS.register("ak47_extract_fluid",
            () -> new BaseFlowingFluid.Source(ModFluids.AK47_EXTRACT_FLUID_PROPERTIES));
    public static final Supplier<FlowingFluid> FLOWING_AK47_EXTRACT_FLUID = FLUIDS.register("flowing_ak47_extract",
            () -> new BaseFlowingFluid.Flowing(ModFluids.AK47_EXTRACT_FLUID_PROPERTIES));
    public static final DeferredBlock<LiquidBlock> AK47_EXTRACT_FLUID_BLOCK = ModBlocks.BLOCKS.register("ak47_extract_fluid_block",
            () -> new LiquidBlock(ModFluids.SOURCE_AK47_EXTRACT_FLUID.get(), BlockBehaviour.Properties.ofFullCopy(Blocks.WATER).noLootTable()));
    public static final DeferredItem<Item> AK47_EXTRACT_BUCKET = ModItems.ITEMS.registerItem("ak47_extract_bucket",
            properties -> new BucketItem(ModFluids.SOURCE_AK47_EXTRACT_FLUID.get(), properties.craftRemainder(Items.BUCKET).stacksTo(1)));
    public static final BaseFlowingFluid.Properties AK47_EXTRACT_FLUID_PROPERTIES = new BaseFlowingFluid.Properties(
            ModFluidTypes.AK47_EXTRACT_FLUID_TYPE, SOURCE_AK47_EXTRACT_FLUID, FLOWING_AK47_EXTRACT_FLUID)
            .slopeFindDistance(2)
            .levelDecreasePerBlock(2);

    public static final Supplier<FlowingFluid> SOURCE_GHOST_TRAIN_EXTRACT_FLUID = FLUIDS.register("ghost_train_extract_fluid",
            () -> new BaseFlowingFluid.Source(ModFluids.GHOST_TRAIN_EXTRACT_FLUID_PROPERTIES));
    public static final Supplier<FlowingFluid> FLOWING_GHOST_TRAIN_EXTRACT_FLUID = FLUIDS.register("flowing_ghost_train_extract",
            () -> new BaseFlowingFluid.Flowing(ModFluids.GHOST_TRAIN_EXTRACT_FLUID_PROPERTIES));
    public static final DeferredBlock<LiquidBlock> GHOST_TRAIN_EXTRACT_FLUID_BLOCK = ModBlocks.BLOCKS.register("ghost_train_extract_fluid_block",
            () -> new LiquidBlock(ModFluids.SOURCE_GHOST_TRAIN_EXTRACT_FLUID.get(), BlockBehaviour.Properties.ofFullCopy(Blocks.WATER).noLootTable()));
    public static final DeferredItem<Item> GHOST_TRAIN_EXTRACT_BUCKET = ModItems.ITEMS.registerItem("ghost_train_extract_bucket",
            properties -> new BucketItem(ModFluids.SOURCE_GHOST_TRAIN_EXTRACT_FLUID.get(), properties.craftRemainder(Items.BUCKET).stacksTo(1)));
    public static final BaseFlowingFluid.Properties GHOST_TRAIN_EXTRACT_FLUID_PROPERTIES = new BaseFlowingFluid.Properties(
            ModFluidTypes.GHOST_TRAIN_EXTRACT_FLUID_TYPE, SOURCE_GHOST_TRAIN_EXTRACT_FLUID, FLOWING_GHOST_TRAIN_EXTRACT_FLUID)
            .slopeFindDistance(2)
            .levelDecreasePerBlock(2);

    public static final Supplier<FlowingFluid> SOURCE_GRAPE_APE_EXTRACT_FLUID = FLUIDS.register("grape_ape_extract_fluid",
            () -> new BaseFlowingFluid.Source(ModFluids.GRAPE_APE_EXTRACT_FLUID_PROPERTIES));
    public static final Supplier<FlowingFluid> FLOWING_GRAPE_APE_EXTRACT_FLUID = FLUIDS.register("flowing_grape_ape_extract",
            () -> new BaseFlowingFluid.Flowing(ModFluids.GRAPE_APE_EXTRACT_FLUID_PROPERTIES));
    public static final DeferredBlock<LiquidBlock> GRAPE_APE_EXTRACT_FLUID_BLOCK = ModBlocks.BLOCKS.register("grape_ape_extract_fluid_block",
            () -> new LiquidBlock(ModFluids.SOURCE_GRAPE_APE_EXTRACT_FLUID.get(), BlockBehaviour.Properties.ofFullCopy(Blocks.WATER).noLootTable()));
    public static final DeferredItem<Item> GRAPE_APE_EXTRACT_BUCKET = ModItems.ITEMS.registerItem("grape_ape_extract_bucket",
            properties -> new BucketItem(ModFluids.SOURCE_GRAPE_APE_EXTRACT_FLUID.get(), properties.craftRemainder(Items.BUCKET).stacksTo(1)));
    public static final BaseFlowingFluid.Properties GRAPE_APE_EXTRACT_FLUID_PROPERTIES = new BaseFlowingFluid.Properties(
            ModFluidTypes.GRAPE_APE_EXTRACT_FLUID_TYPE, SOURCE_GRAPE_APE_EXTRACT_FLUID, FLOWING_GRAPE_APE_EXTRACT_FLUID)
            .slopeFindDistance(2)
            .levelDecreasePerBlock(2);

    public static final Supplier<FlowingFluid> SOURCE_COTTON_CANDY_EXTRACT_FLUID = FLUIDS.register("cotton_candy_extract_fluid",
            () -> new BaseFlowingFluid.Source(ModFluids.COTTON_CANDY_EXTRACT_FLUID_PROPERTIES));
    public static final Supplier<FlowingFluid> FLOWING_COTTON_CANDY_EXTRACT_FLUID = FLUIDS.register("flowing_cotton_candy_extract",
            () -> new BaseFlowingFluid.Flowing(ModFluids.COTTON_CANDY_EXTRACT_FLUID_PROPERTIES));
    public static final DeferredBlock<LiquidBlock> COTTON_CANDY_EXTRACT_FLUID_BLOCK = ModBlocks.BLOCKS.register("cotton_candy_extract_fluid_block",
            () -> new LiquidBlock(ModFluids.SOURCE_COTTON_CANDY_EXTRACT_FLUID.get(), BlockBehaviour.Properties.ofFullCopy(Blocks.WATER).noLootTable()));
    public static final DeferredItem<Item> COTTON_CANDY_EXTRACT_BUCKET = ModItems.ITEMS.registerItem("cotton_candy_extract_bucket",
            properties -> new BucketItem(ModFluids.SOURCE_COTTON_CANDY_EXTRACT_FLUID.get(), properties.craftRemainder(Items.BUCKET).stacksTo(1)));
    public static final BaseFlowingFluid.Properties COTTON_CANDY_EXTRACT_FLUID_PROPERTIES = new BaseFlowingFluid.Properties(
            ModFluidTypes.COTTON_CANDY_EXTRACT_FLUID_TYPE, SOURCE_COTTON_CANDY_EXTRACT_FLUID, FLOWING_COTTON_CANDY_EXTRACT_FLUID)
            .slopeFindDistance(2)
            .levelDecreasePerBlock(2);

    public static final Supplier<FlowingFluid> SOURCE_BANANA_KUSH_EXTRACT_FLUID = FLUIDS.register("banana_kush_extract_fluid",
            () -> new BaseFlowingFluid.Source(ModFluids.BANANA_KUSH_EXTRACT_FLUID_PROPERTIES));
    public static final Supplier<FlowingFluid> FLOWING_BANANA_KUSH_EXTRACT_FLUID = FLUIDS.register("flowing_banana_kush_extract",
            () -> new BaseFlowingFluid.Flowing(ModFluids.BANANA_KUSH_EXTRACT_FLUID_PROPERTIES));
    public static final DeferredBlock<LiquidBlock> BANANA_KUSH_EXTRACT_FLUID_BLOCK = ModBlocks.BLOCKS.register("banana_kush_extract_fluid_block",
            () -> new LiquidBlock(ModFluids.SOURCE_BANANA_KUSH_EXTRACT_FLUID.get(), BlockBehaviour.Properties.ofFullCopy(Blocks.WATER).noLootTable()));
    public static final DeferredItem<Item> BANANA_KUSH_EXTRACT_BUCKET = ModItems.ITEMS.registerItem("banana_kush_extract_bucket",
            properties -> new BucketItem(ModFluids.SOURCE_BANANA_KUSH_EXTRACT_FLUID.get(), properties.craftRemainder(Items.BUCKET).stacksTo(1)));
    public static final BaseFlowingFluid.Properties BANANA_KUSH_EXTRACT_FLUID_PROPERTIES = new BaseFlowingFluid.Properties(
            ModFluidTypes.BANANA_KUSH_EXTRACT_FLUID_TYPE, SOURCE_BANANA_KUSH_EXTRACT_FLUID, FLOWING_BANANA_KUSH_EXTRACT_FLUID)
            .slopeFindDistance(2)
            .levelDecreasePerBlock(2);

    public static final Supplier<FlowingFluid> SOURCE_CARBON_FIBER_EXTRACT_FLUID = FLUIDS.register("carbon_fiber_extract_fluid",
            () -> new BaseFlowingFluid.Source(ModFluids.CARBON_FIBER_EXTRACT_FLUID_PROPERTIES));
    public static final Supplier<FlowingFluid> FLOWING_CARBON_FIBER_EXTRACT_FLUID = FLUIDS.register("flowing_carbon_fiber_extract",
            () -> new BaseFlowingFluid.Flowing(ModFluids.CARBON_FIBER_EXTRACT_FLUID_PROPERTIES));
    public static final DeferredBlock<LiquidBlock> CARBON_FIBER_EXTRACT_FLUID_BLOCK = ModBlocks.BLOCKS.register("carbon_fiber_extract_fluid_block",
            () -> new LiquidBlock(ModFluids.SOURCE_CARBON_FIBER_EXTRACT_FLUID.get(), BlockBehaviour.Properties.ofFullCopy(Blocks.WATER).noLootTable()));
    public static final DeferredItem<Item> CARBON_FIBER_EXTRACT_BUCKET = ModItems.ITEMS.registerItem("carbon_fiber_extract_bucket",
            properties -> new BucketItem(ModFluids.SOURCE_CARBON_FIBER_EXTRACT_FLUID.get(), properties.craftRemainder(Items.BUCKET).stacksTo(1)));
    public static final BaseFlowingFluid.Properties CARBON_FIBER_EXTRACT_FLUID_PROPERTIES = new BaseFlowingFluid.Properties(
            ModFluidTypes.CARBON_FIBER_EXTRACT_FLUID_TYPE, SOURCE_CARBON_FIBER_EXTRACT_FLUID, FLOWING_CARBON_FIBER_EXTRACT_FLUID)
            .slopeFindDistance(2)
            .levelDecreasePerBlock(2);

    public static final Supplier<FlowingFluid> SOURCE_BIRTHDAY_CAKE_EXTRACT_FLUID = FLUIDS.register("birthday_cake_extract_fluid",
            () -> new BaseFlowingFluid.Source(ModFluids.BIRTHDAY_CAKE_EXTRACT_FLUID_PROPERTIES));
    public static final Supplier<FlowingFluid> FLOWING_BIRTHDAY_CAKE_EXTRACT_FLUID = FLUIDS.register("flowing_birthday_cake_extract",
            () -> new BaseFlowingFluid.Flowing(ModFluids.BIRTHDAY_CAKE_EXTRACT_FLUID_PROPERTIES));
    public static final DeferredBlock<LiquidBlock> BIRTHDAY_CAKE_EXTRACT_FLUID_BLOCK = ModBlocks.BLOCKS.register("birthday_cake_extract_fluid_block",
            () -> new LiquidBlock(ModFluids.SOURCE_BIRTHDAY_CAKE_EXTRACT_FLUID.get(), BlockBehaviour.Properties.ofFullCopy(Blocks.WATER).noLootTable()));
    public static final DeferredItem<Item> BIRTHDAY_CAKE_EXTRACT_BUCKET = ModItems.ITEMS.registerItem("birthday_cake_extract_bucket",
            properties -> new BucketItem(ModFluids.SOURCE_BIRTHDAY_CAKE_EXTRACT_FLUID.get(), properties.craftRemainder(Items.BUCKET).stacksTo(1)));
    public static final BaseFlowingFluid.Properties BIRTHDAY_CAKE_EXTRACT_FLUID_PROPERTIES = new BaseFlowingFluid.Properties(
            ModFluidTypes.BIRTHDAY_CAKE_EXTRACT_FLUID_TYPE, SOURCE_BIRTHDAY_CAKE_EXTRACT_FLUID, FLOWING_BIRTHDAY_CAKE_EXTRACT_FLUID)
            .slopeFindDistance(2)
            .levelDecreasePerBlock(2);

    public static final Supplier<FlowingFluid> SOURCE_BLUE_COOKIES_EXTRACT_FLUID = FLUIDS.register("blue_cookies_extract_fluid",
            () -> new BaseFlowingFluid.Source(ModFluids.BLUE_COOKIES_EXTRACT_FLUID_PROPERTIES));
    public static final Supplier<FlowingFluid> FLOWING_BLUE_COOKIES_EXTRACT_FLUID = FLUIDS.register("flowing_blue_cookies_extract",
            () -> new BaseFlowingFluid.Flowing(ModFluids.BLUE_COOKIES_EXTRACT_FLUID_PROPERTIES));
    public static final DeferredBlock<LiquidBlock> BLUE_COOKIES_EXTRACT_FLUID_BLOCK = ModBlocks.BLOCKS.register("blue_cookies_extract_fluid_block",
            () -> new LiquidBlock(ModFluids.SOURCE_BLUE_COOKIES_EXTRACT_FLUID.get(), BlockBehaviour.Properties.ofFullCopy(Blocks.WATER).noLootTable()));
    public static final DeferredItem<Item> BLUE_COOKIES_EXTRACT_BUCKET = ModItems.ITEMS.registerItem("blue_cookies_extract_bucket",
            properties -> new BucketItem(ModFluids.SOURCE_BLUE_COOKIES_EXTRACT_FLUID.get(), properties.craftRemainder(Items.BUCKET).stacksTo(1)));
    public static final BaseFlowingFluid.Properties BLUE_COOKIES_EXTRACT_FLUID_PROPERTIES = new BaseFlowingFluid.Properties(
            ModFluidTypes.BLUE_COOKIES_EXTRACT_FLUID_TYPE, SOURCE_BLUE_COOKIES_EXTRACT_FLUID, FLOWING_BLUE_COOKIES_EXTRACT_FLUID)
            .slopeFindDistance(2)
            .levelDecreasePerBlock(2);

    public static final Supplier<FlowingFluid> SOURCE_AFGHANI_EXTRACT_FLUID = FLUIDS.register("afghani_extract_fluid",
            () -> new BaseFlowingFluid.Source(ModFluids.AFGHANI_EXTRACT_FLUID_PROPERTIES));
    public static final Supplier<FlowingFluid> FLOWING_AFGHANI_EXTRACT_FLUID = FLUIDS.register("flowing_afghani_extract",
            () -> new BaseFlowingFluid.Flowing(ModFluids.AFGHANI_EXTRACT_FLUID_PROPERTIES));
    public static final DeferredBlock<LiquidBlock> AFGHANI_EXTRACT_FLUID_BLOCK = ModBlocks.BLOCKS.register("afghani_extract_fluid_block",
            () -> new LiquidBlock(ModFluids.SOURCE_AFGHANI_EXTRACT_FLUID.get(), BlockBehaviour.Properties.ofFullCopy(Blocks.WATER).noLootTable()));
    public static final DeferredItem<Item> AFGHANI_EXTRACT_BUCKET = ModItems.ITEMS.registerItem("afghani_extract_bucket",
            properties -> new BucketItem(ModFluids.SOURCE_AFGHANI_EXTRACT_FLUID.get(), properties.craftRemainder(Items.BUCKET).stacksTo(1)));
    public static final BaseFlowingFluid.Properties AFGHANI_EXTRACT_FLUID_PROPERTIES = new BaseFlowingFluid.Properties(
            ModFluidTypes.AFGHANI_EXTRACT_FLUID_TYPE, SOURCE_AFGHANI_EXTRACT_FLUID, FLOWING_AFGHANI_EXTRACT_FLUID)
            .slopeFindDistance(2)
            .levelDecreasePerBlock(2);

    public static final Supplier<FlowingFluid> SOURCE_MOONBOW_EXTRACT_FLUID = FLUIDS.register("moonbow_extract_fluid",
            () -> new BaseFlowingFluid.Source(ModFluids.MOONBOW_EXTRACT_FLUID_PROPERTIES));
    public static final Supplier<FlowingFluid> FLOWING_MOONBOW_EXTRACT_FLUID = FLUIDS.register("flowing_moonbow_extract",
            () -> new BaseFlowingFluid.Flowing(ModFluids.MOONBOW_EXTRACT_FLUID_PROPERTIES));
    public static final DeferredBlock<LiquidBlock> MOONBOW_EXTRACT_FLUID_BLOCK = ModBlocks.BLOCKS.register("moonbow_extract_fluid_block",
            () -> new LiquidBlock(ModFluids.SOURCE_MOONBOW_EXTRACT_FLUID.get(), BlockBehaviour.Properties.ofFullCopy(Blocks.WATER).noLootTable()));
    public static final DeferredItem<Item> MOONBOW_EXTRACT_BUCKET = ModItems.ITEMS.registerItem("moonbow_extract_bucket",
            properties -> new BucketItem(ModFluids.SOURCE_MOONBOW_EXTRACT_FLUID.get(), properties.craftRemainder(Items.BUCKET).stacksTo(1)));
    public static final BaseFlowingFluid.Properties MOONBOW_EXTRACT_FLUID_PROPERTIES = new BaseFlowingFluid.Properties(
            ModFluidTypes.MOONBOW_EXTRACT_FLUID_TYPE, SOURCE_MOONBOW_EXTRACT_FLUID, FLOWING_MOONBOW_EXTRACT_FLUID)
            .slopeFindDistance(2)
            .levelDecreasePerBlock(2);

    public static final Supplier<FlowingFluid> SOURCE_LAVA_CAKE_EXTRACT_FLUID = FLUIDS.register("lava_cake_extract_fluid",
            () -> new BaseFlowingFluid.Source(ModFluids.LAVA_CAKE_EXTRACT_FLUID_PROPERTIES));
    public static final Supplier<FlowingFluid> FLOWING_LAVA_CAKE_EXTRACT_FLUID = FLUIDS.register("flowing_lava_cake_extract",
            () -> new BaseFlowingFluid.Flowing(ModFluids.LAVA_CAKE_EXTRACT_FLUID_PROPERTIES));
    public static final DeferredBlock<LiquidBlock> LAVA_CAKE_EXTRACT_FLUID_BLOCK = ModBlocks.BLOCKS.register("lava_cake_extract_fluid_block",
            () -> new LiquidBlock(ModFluids.SOURCE_LAVA_CAKE_EXTRACT_FLUID.get(), BlockBehaviour.Properties.ofFullCopy(Blocks.WATER).noLootTable()));
    public static final DeferredItem<Item> LAVA_CAKE_EXTRACT_BUCKET = ModItems.ITEMS.registerItem("lava_cake_extract_bucket",
            properties -> new BucketItem(ModFluids.SOURCE_LAVA_CAKE_EXTRACT_FLUID.get(), properties.craftRemainder(Items.BUCKET).stacksTo(1)));
    public static final BaseFlowingFluid.Properties LAVA_CAKE_EXTRACT_FLUID_PROPERTIES = new BaseFlowingFluid.Properties(
            ModFluidTypes.LAVA_CAKE_EXTRACT_FLUID_TYPE, SOURCE_LAVA_CAKE_EXTRACT_FLUID, FLOWING_LAVA_CAKE_EXTRACT_FLUID)
            .slopeFindDistance(2)
            .levelDecreasePerBlock(2);

    public static final Supplier<FlowingFluid> SOURCE_JELLY_RANCHER_EXTRACT_FLUID = FLUIDS.register("jelly_rancher_extract_fluid",
            () -> new BaseFlowingFluid.Source(ModFluids.JELLY_RANCHER_EXTRACT_FLUID_PROPERTIES));
    public static final Supplier<FlowingFluid> FLOWING_JELLY_RANCHER_EXTRACT_FLUID = FLUIDS.register("flowing_jelly_rancher_extract",
            () -> new BaseFlowingFluid.Flowing(ModFluids.JELLY_RANCHER_EXTRACT_FLUID_PROPERTIES));
    public static final DeferredBlock<LiquidBlock> JELLY_RANCHER_EXTRACT_FLUID_BLOCK = ModBlocks.BLOCKS.register("jelly_rancher_extract_fluid_block",
            () -> new LiquidBlock(ModFluids.SOURCE_JELLY_RANCHER_EXTRACT_FLUID.get(), BlockBehaviour.Properties.ofFullCopy(Blocks.WATER).noLootTable()));
    public static final DeferredItem<Item> JELLY_RANCHER_EXTRACT_BUCKET = ModItems.ITEMS.registerItem("jelly_rancher_extract_bucket",
            properties -> new BucketItem(ModFluids.SOURCE_JELLY_RANCHER_EXTRACT_FLUID.get(), properties.craftRemainder(Items.BUCKET).stacksTo(1)));
    public static final BaseFlowingFluid.Properties JELLY_RANCHER_EXTRACT_FLUID_PROPERTIES = new BaseFlowingFluid.Properties(
            ModFluidTypes.JELLY_RANCHER_EXTRACT_FLUID_TYPE, SOURCE_JELLY_RANCHER_EXTRACT_FLUID, FLOWING_JELLY_RANCHER_EXTRACT_FLUID)
            .slopeFindDistance(2)
            .levelDecreasePerBlock(2);

    public static final Supplier<FlowingFluid> SOURCE_STRAWBERRY_SHORTCAKE_EXTRACT_FLUID = FLUIDS.register("strawberry_shortcake_extract_fluid",
            () -> new BaseFlowingFluid.Source(ModFluids.STRAWBERRY_SHORTCAKE_EXTRACT_FLUID_PROPERTIES));
    public static final Supplier<FlowingFluid> FLOWING_STRAWBERRY_SHORTCAKE_EXTRACT_FLUID = FLUIDS.register("flowing_strawberry_shortcake_extract",
            () -> new BaseFlowingFluid.Flowing(ModFluids.STRAWBERRY_SHORTCAKE_EXTRACT_FLUID_PROPERTIES));
    public static final DeferredBlock<LiquidBlock> STRAWBERRY_SHORTCAKE_EXTRACT_FLUID_BLOCK = ModBlocks.BLOCKS.register("strawberry_shortcake_extract_fluid_block",
            () -> new LiquidBlock(ModFluids.SOURCE_STRAWBERRY_SHORTCAKE_EXTRACT_FLUID.get(), BlockBehaviour.Properties.ofFullCopy(Blocks.WATER).noLootTable()));
    public static final DeferredItem<Item> STRAWBERRY_SHORTCAKE_EXTRACT_BUCKET = ModItems.ITEMS.registerItem("strawberry_shortcake_extract_bucket",
            properties -> new BucketItem(ModFluids.SOURCE_STRAWBERRY_SHORTCAKE_EXTRACT_FLUID.get(), properties.craftRemainder(Items.BUCKET).stacksTo(1)));
    public static final BaseFlowingFluid.Properties STRAWBERRY_SHORTCAKE_EXTRACT_FLUID_PROPERTIES = new BaseFlowingFluid.Properties(
            ModFluidTypes.STRAWBERRY_SHORTCAKE_EXTRACT_FLUID_TYPE, SOURCE_STRAWBERRY_SHORTCAKE_EXTRACT_FLUID, FLOWING_STRAWBERRY_SHORTCAKE_EXTRACT_FLUID)
            .slopeFindDistance(2)
            .levelDecreasePerBlock(2);

    public static final Supplier<FlowingFluid> SOURCE_PINK_KUSH_EXTRACT_FLUID = FLUIDS.register("pink_kush_extract_fluid",
            () -> new BaseFlowingFluid.Source(ModFluids.PINK_KUSH_EXTRACT_FLUID_PROPERTIES));
    public static final Supplier<FlowingFluid> FLOWING_PINK_KUSH_EXTRACT_FLUID = FLUIDS.register("flowing_pink_kush_extract",
            () -> new BaseFlowingFluid.Flowing(ModFluids.PINK_KUSH_EXTRACT_FLUID_PROPERTIES));
    public static final DeferredBlock<LiquidBlock> PINK_KUSH_EXTRACT_FLUID_BLOCK = ModBlocks.BLOCKS.register("pink_kush_extract_fluid_block",
            () -> new LiquidBlock(ModFluids.SOURCE_PINK_KUSH_EXTRACT_FLUID.get(), BlockBehaviour.Properties.ofFullCopy(Blocks.WATER).noLootTable()));
    public static final DeferredItem<Item> PINK_KUSH_EXTRACT_BUCKET = ModItems.ITEMS.registerItem("pink_kush_extract_bucket",
            properties -> new BucketItem(ModFluids.SOURCE_PINK_KUSH_EXTRACT_FLUID.get(), properties.craftRemainder(Items.BUCKET).stacksTo(1)));
    public static final BaseFlowingFluid.Properties PINK_KUSH_EXTRACT_FLUID_PROPERTIES = new BaseFlowingFluid.Properties(
            ModFluidTypes.PINK_KUSH_EXTRACT_FLUID_TYPE, SOURCE_PINK_KUSH_EXTRACT_FLUID, FLOWING_PINK_KUSH_EXTRACT_FLUID)
            .slopeFindDistance(2)
            .levelDecreasePerBlock(2);


    // Late wiring to avoid null bucket/block during properties construction
    static {
        HASH_OIL_FLUID_PROPERTIES.block(HASH_OIL_FLUID_BLOCK).bucket(HASH_OIL_BUCKET);
        HASH_OIL_SLUDGE_FLUID_PROPERTIES.block(HASH_OIL_SLUDGE_FLUID_BLOCK).bucket(HASH_OIL_SLUDGE_BUCKET);
        HEMP_OIL_FLUID_PROPERTIES.block(HEMP_OIL_FLUID_BLOCK).bucket(HEMP_OIL_BUCKET);

        WHITE_WIDOW_EXTRACT_FLUID_PROPERTIES.block(WHITE_WIDOW_EXTRACT_FLUID_BLOCK).bucket(WHITE_WIDOW_EXTRACT_BUCKET);
        BUBBLE_KUSH_EXTRACT_FLUID_PROPERTIES.block(BUBBLE_KUSH_EXTRACT_FLUID_BLOCK).bucket(BUBBLE_KUSH_EXTRACT_BUCKET);
        LEMON_HAZE_EXTRACT_FLUID_PROPERTIES.block(LEMON_HAZE_EXTRACT_FLUID_BLOCK).bucket(LEMON_HAZE_EXTRACT_BUCKET);
        SOUR_DIESEL_EXTRACT_FLUID_PROPERTIES.block(SOUR_DIESEL_EXTRACT_FLUID_BLOCK).bucket(SOUR_DIESEL_EXTRACT_BUCKET);
        BLUE_ICE_EXTRACT_FLUID_PROPERTIES.block(BLUE_ICE_EXTRACT_FLUID_BLOCK).bucket(BLUE_ICE_EXTRACT_BUCKET);
        BUBBLEGUM_EXTRACT_FLUID_PROPERTIES.block(BUBBLEGUM_EXTRACT_FLUID_BLOCK).bucket(BUBBLEGUM_EXTRACT_BUCKET);
        PURPLE_HAZE_EXTRACT_FLUID_PROPERTIES.block(PURPLE_HAZE_EXTRACT_FLUID_BLOCK).bucket(PURPLE_HAZE_EXTRACT_BUCKET);
        OG_KUSH_EXTRACT_FLUID_PROPERTIES.block(OG_KUSH_EXTRACT_FLUID_BLOCK).bucket(OG_KUSH_EXTRACT_BUCKET);
        JACK_HERER_EXTRACT_FLUID_PROPERTIES.block(JACK_HERER_EXTRACT_FLUID_BLOCK).bucket(JACK_HERER_EXTRACT_BUCKET);
        GARY_PEYTON_EXTRACT_FLUID_PROPERTIES.block(GARY_PEYTON_EXTRACT_FLUID_BLOCK).bucket(GARY_PEYTON_EXTRACT_BUCKET);
        AMNESIA_HAZE_EXTRACT_FLUID_PROPERTIES.block(AMNESIA_HAZE_EXTRACT_FLUID_BLOCK).bucket(AMNESIA_HAZE_EXTRACT_BUCKET);
        AK47_EXTRACT_FLUID_PROPERTIES.block(AK47_EXTRACT_FLUID_BLOCK).bucket(AK47_EXTRACT_BUCKET);
        GHOST_TRAIN_EXTRACT_FLUID_PROPERTIES.block(GHOST_TRAIN_EXTRACT_FLUID_BLOCK).bucket(GHOST_TRAIN_EXTRACT_BUCKET);
        GRAPE_APE_EXTRACT_FLUID_PROPERTIES.block(GRAPE_APE_EXTRACT_FLUID_BLOCK).bucket(GRAPE_APE_EXTRACT_BUCKET);
        COTTON_CANDY_EXTRACT_FLUID_PROPERTIES.block(COTTON_CANDY_EXTRACT_FLUID_BLOCK).bucket(COTTON_CANDY_EXTRACT_BUCKET);
        BANANA_KUSH_EXTRACT_FLUID_PROPERTIES.block(BANANA_KUSH_EXTRACT_FLUID_BLOCK).bucket(BANANA_KUSH_EXTRACT_BUCKET);
        CARBON_FIBER_EXTRACT_FLUID_PROPERTIES.block(CARBON_FIBER_EXTRACT_FLUID_BLOCK).bucket(CARBON_FIBER_EXTRACT_BUCKET);
        BIRTHDAY_CAKE_EXTRACT_FLUID_PROPERTIES.block(BIRTHDAY_CAKE_EXTRACT_FLUID_BLOCK).bucket(BIRTHDAY_CAKE_EXTRACT_BUCKET);
        BLUE_COOKIES_EXTRACT_FLUID_PROPERTIES.block(BLUE_COOKIES_EXTRACT_FLUID_BLOCK).bucket(BLUE_COOKIES_EXTRACT_BUCKET);
        AFGHANI_EXTRACT_FLUID_PROPERTIES.block(AFGHANI_EXTRACT_FLUID_BLOCK).bucket(AFGHANI_EXTRACT_BUCKET);
        MOONBOW_EXTRACT_FLUID_PROPERTIES.block(MOONBOW_EXTRACT_FLUID_BLOCK).bucket(MOONBOW_EXTRACT_BUCKET);
        LAVA_CAKE_EXTRACT_FLUID_PROPERTIES.block(LAVA_CAKE_EXTRACT_FLUID_BLOCK).bucket(LAVA_CAKE_EXTRACT_BUCKET);
        JELLY_RANCHER_EXTRACT_FLUID_PROPERTIES.block(JELLY_RANCHER_EXTRACT_FLUID_BLOCK).bucket(JELLY_RANCHER_EXTRACT_BUCKET);
        STRAWBERRY_SHORTCAKE_EXTRACT_FLUID_PROPERTIES.block(STRAWBERRY_SHORTCAKE_EXTRACT_FLUID_BLOCK).bucket(STRAWBERRY_SHORTCAKE_EXTRACT_BUCKET);
        PINK_KUSH_EXTRACT_FLUID_PROPERTIES.block(PINK_KUSH_EXTRACT_FLUID_BLOCK).bucket(PINK_KUSH_EXTRACT_BUCKET);
    }

    public static void register(IEventBus eventBus) {
        FLUIDS.register(eventBus);
    }

}
