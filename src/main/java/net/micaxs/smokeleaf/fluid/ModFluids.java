package net.micaxs.smokeleaf.fluid;

import net.micaxs.smokeleaf.SmokeleafIndustries;
import net.micaxs.smokeleaf.block.ModBlocks;
import net.micaxs.smokeleaf.item.ModItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.micaxs.smokeleaf.item.custom.UnidentifiedMixtureBucketItem;
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

    // Hemp oil removed: no longer registered (obsolete).
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


    // ---- Player-made mixture fluid ----

    public static final Supplier<FlowingFluid> SOURCE_UNIDENTIFIED_MIXTURE_FLUID = FLUIDS.register("unidentified_mixture_fluid",
            () -> new BaseFlowingFluid.Source(ModFluids.UNIDENTIFIED_MIXTURE_FLUID_PROPERTIES));
    public static final Supplier<FlowingFluid> FLOWING_UNIDENTIFIED_MIXTURE_FLUID = FLUIDS.register("flowing_unidentified_mixture",
            () -> new BaseFlowingFluid.Flowing(ModFluids.UNIDENTIFIED_MIXTURE_FLUID_PROPERTIES));
    public static final DeferredBlock<LiquidBlock> UNIDENTIFIED_MIXTURE_FLUID_BLOCK = ModBlocks.BLOCKS.register("unidentified_mixture_fluid_block",
            () -> new LiquidBlock(ModFluids.SOURCE_UNIDENTIFIED_MIXTURE_FLUID.get(), BlockBehaviour.Properties.ofFullCopy(Blocks.WATER).noLootTable()));
    public static final DeferredItem<Item> UNIDENTIFIED_MIXTURE_BUCKET = ModItems.ITEMS.registerItem("unidentified_mixture_bucket",
            properties -> new UnidentifiedMixtureBucketItem(ModFluids.SOURCE_UNIDENTIFIED_MIXTURE_FLUID.get(), properties.craftRemainder(Items.BUCKET).stacksTo(1)));
    public static final BaseFlowingFluid.Properties UNIDENTIFIED_MIXTURE_FLUID_PROPERTIES = new BaseFlowingFluid.Properties(
            ModFluidTypes.UNIDENTIFIED_MIXTURE_FLUID_TYPE, SOURCE_UNIDENTIFIED_MIXTURE_FLUID, FLOWING_UNIDENTIFIED_MIXTURE_FLUID)
            .slopeFindDistance(2)
            .levelDecreasePerBlock(2);


    // Late wiring to avoid null bucket/block during properties construction
    static {
        HASH_OIL_FLUID_PROPERTIES.block(HASH_OIL_FLUID_BLOCK).bucket(HASH_OIL_BUCKET);
        HASH_OIL_SLUDGE_FLUID_PROPERTIES.block(HASH_OIL_SLUDGE_FLUID_BLOCK).bucket(HASH_OIL_SLUDGE_BUCKET);

        UNIDENTIFIED_MIXTURE_FLUID_PROPERTIES.block(UNIDENTIFIED_MIXTURE_FLUID_BLOCK).bucket(UNIDENTIFIED_MIXTURE_BUCKET);
    }

    public static void register(IEventBus eventBus) {
        FLUIDS.register(eventBus);
    }

    /**
     * Returns true if the given fluid is one of this mod's generic "oil" fluids — the ones a strain's
     * StrainData actually rides in (Hash Oil from weed, Unidentified Mixture from a generic Extract).
     * Used by machines that accept any oil as an input (Mixer, Confectioner).
     *
     * <p>This used to match on a {@code "*_extract_fluid"} name suffix, back when every preset strain
     * had its own dedicated extract fluid — but those per-strain fluids haven't been produced by
     * anything since the mod moved to the generic StrainData-component system, so that check silently
     * matched nothing real. Concretely: Hash Oil (from Liquifying weed) never matched it, so it could
     * never actually be poured into the Mixer or Confectioner despite both machines being built around
     * accepting "any oil" — only Unidentified Mixture Fluid worked, purely because it also had its own
     * explicit check at every call site.
     */
    public static boolean isExtractFluid(net.minecraft.world.level.material.Fluid fluid) {
        if (fluid == null) return false;
        return fluid == SOURCE_HASH_OIL_FLUID.get() || fluid == SOURCE_UNIDENTIFIED_MIXTURE_FLUID.get();
    }
}
