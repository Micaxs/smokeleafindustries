package net.micaxs.smokeleaf.fluid;

import net.micaxs.smokeleaf.SmokeleafIndustries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.SoundAction;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.joml.Vector3f;

import java.util.List;
import java.util.function.Supplier;

public class ModFluidTypes {
    public static final ResourceLocation WATER_STILL_RL = ResourceLocation.parse("block/water_still");
    public static final ResourceLocation WATER_FLOWING_RL = ResourceLocation.parse("block/water_flow");
    public static final ResourceLocation WATER_OVERLAY_RL = ResourceLocation.parse("block/water_overlay");

    public static final DeferredRegister<FluidType> FLUID_TYPES =
            DeferredRegister.create(NeoForgeRegistries.Keys.FLUID_TYPES, SmokeleafIndustries.MODID);


    public static final Supplier<FluidType> HASH_OIL_FLUID_TYPE = registerFluidType("hash_oil_fluid", new BaseFluidType(WATER_STILL_RL, WATER_FLOWING_RL, WATER_OVERLAY_RL,
            0xA1F2932E, new Vector3f(242f / 255f, 147f / 255f, 46f / 255f), FluidType.Properties.create().canExtinguish(true).lightLevel(3).density(15).viscosity(5).sound(SoundAction.get("drink"), SoundEvents.HONEY_DRINK)));

    public static final Supplier<FluidType> HASH_OIL_SLUDGE_FLUID_TYPE = registerFluidType("hash_oil_sludge_fluid", new BaseFluidType(WATER_STILL_RL, WATER_FLOWING_RL, WATER_OVERLAY_RL,
            0xFF3b3b3b, new Vector3f(59f / 255f, 59f / 255f, 59f / 255f), FluidType.Properties.create().canExtinguish(true).lightLevel(3).density(15).viscosity(5).sound(SoundAction.get("drink"), SoundEvents.HONEY_DRINK)));


    // Player-made mixture fluid (dynamic tint via STRAIN_DATA)
    public static final Supplier<WeedFluidType> UNIDENTIFIED_MIXTURE_FLUID_TYPE = registerWeedFluidType("unidentified_mixture_fluid", new MixtureWeedFluidType(
            WATER_STILL_RL, WATER_FLOWING_RL, WATER_OVERLAY_RL,
            0xFFAAAAAA, new Vector3f(170f / 255f, 170f / 255f, 170f / 255f),
            FluidType.Properties.create().canExtinguish(true).lightLevel(5).density(10).viscosity(3).sound(SoundAction.get("drink"), SoundEvents.HONEY_DRINK),
            0, 0,
            "Unidentified",
            "Mixture"
    ));

    private static Supplier<FluidType> registerFluidType(String name, FluidType fluidType) {
        return FLUID_TYPES.register(name, () -> fluidType);
    }

    private static Supplier<WeedFluidType> registerWeedFluidType(String name, WeedFluidType fluidType) {
        return FLUID_TYPES.register(name, () -> fluidType);
    }

    public static void register(IEventBus eventBus) {
        FLUID_TYPES.register(eventBus);
    }
}
