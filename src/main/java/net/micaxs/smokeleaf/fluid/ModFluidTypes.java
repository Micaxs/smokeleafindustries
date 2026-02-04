package net.micaxs.smokeleaf.fluid;

import net.micaxs.smokeleaf.SmokeleafIndustries;
import net.micaxs.smokeleaf.effect.ModEffects;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffects;
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

    public static final Supplier<FluidType> HEMP_OIL_FLUID_TYPE = registerFluidType("hemp_oil_fluid", new BaseFluidType(WATER_STILL_RL, WATER_FLOWING_RL, WATER_OVERLAY_RL,
            0xFF58EB34, new Vector3f(59f / 255f, 59f / 255f, 59f / 255f), FluidType.Properties.create().canExtinguish(true).lightLevel(3).density(15).viscosity(5).sound(SoundAction.get("drink"), SoundEvents.HONEY_DRINK)));


    public static final Supplier<WeedFluidType> WHITE_WIDOW_EXTRACT_FLUID_TYPE = registerWeedFluidType("white_widow_extract_fluid", new WeedFluidType(
            WATER_STILL_RL, WATER_FLOWING_RL, WATER_OVERLAY_RL,
            0xFFcad9b3, new Vector3f(239f / 255f, 239f / 255f, 239f / 255f),
            FluidType.Properties.create().canExtinguish(true).lightLevel(5).density(10).viscosity(3).sound(SoundAction.get("drink"), SoundEvents.HONEY_DRINK),
            200, 15,
            "White",
            "Widow"
    ));

    public static final Supplier<WeedFluidType> BUBBLE_KUSH_EXTRACT_FLUID_TYPE = registerWeedFluidType("bubble_kush_extract_fluid", new WeedFluidType(
            WATER_STILL_RL, WATER_FLOWING_RL, WATER_OVERLAY_RL,
            0xFFe3d292, new Vector3f(179f / 255f, 229f / 255f, 252f / 255f),
            FluidType.Properties.create().canExtinguish(true).lightLevel(5).density(10).viscosity(3).sound(SoundAction.get("drink"), SoundEvents.HONEY_DRINK),
            180, 20,
            "Bubble",
            "Kush"
    ));

    public static final Supplier<WeedFluidType> LEMON_HAZE_EXTRACT_FLUID_TYPE = registerWeedFluidType("lemon_haze_extract_fluid", new WeedFluidType(
            WATER_STILL_RL, WATER_FLOWING_RL, WATER_OVERLAY_RL,
            0xFFcddc98, new Vector3f(249f / 255f, 251f / 255f, 231f / 255f),
            FluidType.Properties.create().canExtinguish(true).lightLevel(5).density(10).viscosity(3).sound(SoundAction.get("drink"), SoundEvents.HONEY_DRINK),
            160, 19,
            "Lemon",
            "Haze"
    ));

    public static final Supplier<WeedFluidType> SOUR_DIESEL_EXTRACT_FLUID_TYPE = registerWeedFluidType("sour_diesel_extract_fluid", new WeedFluidType(
            WATER_STILL_RL, WATER_FLOWING_RL, WATER_OVERLAY_RL,
            0xFFffbdb8, new Vector3f(62f / 255f, 74f / 255f, 50f / 255f),
            FluidType.Properties.create().canExtinguish(true).lightLevel(5).density(10).viscosity(3).sound(SoundAction.get("drink"), SoundEvents.HONEY_DRINK),
            170, 19,
            "Sour",
            "Diesel"
    ));

    public static final Supplier<WeedFluidType> BLUE_ICE_EXTRACT_FLUID_TYPE = registerWeedFluidType("blue_ice_extract_fluid", new WeedFluidType(
            WATER_STILL_RL, WATER_FLOWING_RL, WATER_OVERLAY_RL,
            0xFFb6e9ba, new Vector3f(127f / 255f, 217f / 255f, 255f / 255f),
            FluidType.Properties.create().canExtinguish(true).lightLevel(5).density(10).viscosity(3).sound(SoundAction.get("drink"), SoundEvents.HONEY_DRINK),
            190, 20,
            "Blue",
            "Ice"
    ));

    public static final Supplier<WeedFluidType> BUBBLEGUM_EXTRACT_FLUID_TYPE = registerWeedFluidType("bubblegum_extract_fluid", new WeedFluidType(
            WATER_STILL_RL, WATER_FLOWING_RL, WATER_OVERLAY_RL,
            0xFFcaa375, new Vector3f(255f / 255f, 154f / 255f, 213f / 255f),
            FluidType.Properties.create().canExtinguish(true).lightLevel(5).density(10).viscosity(3).sound(SoundAction.get("drink"), SoundEvents.HONEY_DRINK),
            150, 17,
            "Bubble",
            "Gum"
    ));

    public static final Supplier<WeedFluidType> PURPLE_HAZE_EXTRACT_FLUID_TYPE = registerWeedFluidType("purple_haze_extract_fluid", new WeedFluidType(
            WATER_STILL_RL, WATER_FLOWING_RL, WATER_OVERLAY_RL,
            0xFFd481f3, new Vector3f(138f / 255f, 43f / 255f, 226f / 255f),
            FluidType.Properties.create().canExtinguish(true).lightLevel(5).density(10).viscosity(3).sound(SoundAction.get("drink"), SoundEvents.HONEY_DRINK),
            140, 16,
            "Purple",
            "Haze"
    ));

    public static final Supplier<WeedFluidType> OG_KUSH_EXTRACT_FLUID_TYPE = registerWeedFluidType("og_kush_extract_fluid", new WeedFluidType(
            WATER_STILL_RL, WATER_FLOWING_RL, WATER_OVERLAY_RL,
            0xFF91bb70, new Vector3f(46f / 255f, 94f / 255f, 46f / 255f),
            FluidType.Properties.create().canExtinguish(true).lightLevel(5).density(10).viscosity(3).sound(SoundAction.get("drink"), SoundEvents.HONEY_DRINK),
            210, 25,
            "OG",
            "Kush"
    ));

    public static final Supplier<WeedFluidType> JACK_HERER_EXTRACT_FLUID_TYPE = registerWeedFluidType("jack_herer_extract_fluid", new WeedFluidType(
            WATER_STILL_RL, WATER_FLOWING_RL, WATER_OVERLAY_RL,
            0xFFddd084, new Vector3f(154f / 255f, 205f / 255f, 50f / 255f),
            FluidType.Properties.create().canExtinguish(true).lightLevel(5).density(10).viscosity(3).sound(SoundAction.get("drink"), SoundEvents.HONEY_DRINK),
            205, 18,
            "Jack",
            "Herer"
    ));

    public static final Supplier<WeedFluidType> GARY_PEYTON_EXTRACT_FLUID_TYPE = registerWeedFluidType("gary_peyton_extract_fluid", new WeedFluidType(
            WATER_STILL_RL, WATER_FLOWING_RL, WATER_OVERLAY_RL,
            0xFFe3d3b6, new Vector3f(176f / 255f, 175f / 255f, 175f / 255f),
            FluidType.Properties.create().canExtinguish(true).lightLevel(5).density(10).viscosity(3).sound(SoundAction.get("drink"), SoundEvents.HONEY_DRINK),
            195, 22,
            "Gary",
            "Peyton"
    ));

    public static final Supplier<WeedFluidType> AMNESIA_HAZE_EXTRACT_FLUID_TYPE = registerWeedFluidType("amnesia_haze_extract_fluid", new WeedFluidType(
            WATER_STILL_RL, WATER_FLOWING_RL, WATER_OVERLAY_RL,
            0xFFa5b57d, new Vector3f(214f / 255f, 245f / 255f, 214f / 255f),
            FluidType.Properties.create().canExtinguish(true).lightLevel(5).density(10).viscosity(3).sound(SoundAction.get("drink"), SoundEvents.HONEY_DRINK),
            185, 19,
            "Amnesia",
            "Haze"
    ));

    public static final Supplier<WeedFluidType> AK47_EXTRACT_FLUID_TYPE = registerWeedFluidType("ak47_extract_fluid", new WeedFluidType(
            WATER_STILL_RL, WATER_FLOWING_RL, WATER_OVERLAY_RL,
            0xFFafae61, new Vector3f(85f / 255f, 107f / 255f, 47f / 255f),
            FluidType.Properties.create().canExtinguish(true).lightLevel(5).density(10).viscosity(3).sound(SoundAction.get("drink"), SoundEvents.HONEY_DRINK),
            190, 19,
            "AK",
            "47"
    ));

    public static final Supplier<WeedFluidType> GHOST_TRAIN_EXTRACT_FLUID_TYPE = registerWeedFluidType("ghost_train_extract_fluid", new WeedFluidType(
            WATER_STILL_RL, WATER_FLOWING_RL, WATER_OVERLAY_RL,
            0xFFfff0d7, new Vector3f(184f / 255f, 255f / 255f, 240f / 255f),
            FluidType.Properties.create().canExtinguish(true).lightLevel(5).density(10).viscosity(3).sound(SoundAction.get("drink"), SoundEvents.HONEY_DRINK),
            220, 19,
            "Ghost",
            "Train"
    ));
    public static final Supplier<WeedFluidType> GRAPE_APE_EXTRACT_FLUID_TYPE = registerWeedFluidType("grape_ape_extract_fluid", new WeedFluidType(
            WATER_STILL_RL, WATER_FLOWING_RL, WATER_OVERLAY_RL,
            0xFF98ae9e, new Vector3f(111f / 255f, 45f / 255f, 168f / 255f),
            FluidType.Properties.create().canExtinguish(true).lightLevel(5).density(10).viscosity(3).sound(SoundAction.get("drink"), SoundEvents.HONEY_DRINK),
            175, 18,
            "Grape",
            "Ape"
    ));
    public static final Supplier<WeedFluidType> COTTON_CANDY_EXTRACT_FLUID_TYPE = registerWeedFluidType("cotton_candy_extract_fluid", new WeedFluidType(
            WATER_STILL_RL, WATER_FLOWING_RL, WATER_OVERLAY_RL,
            0xFFefc5e1, new Vector3f(255f / 255f, 193f / 255f, 227f / 255f),
            FluidType.Properties.create().canExtinguish(true).lightLevel(5).density(10).viscosity(3).sound(SoundAction.get("drink"), SoundEvents.HONEY_DRINK),
            165, 19,
            "Cotton",
            "Candy"
    ));
    public static final Supplier<WeedFluidType> BANANA_KUSH_EXTRACT_FLUID_TYPE = registerWeedFluidType("banana_kush_extract_fluid", new WeedFluidType(
            WATER_STILL_RL, WATER_FLOWING_RL, WATER_OVERLAY_RL,
            0xFFd8d6a1, new Vector3f(255f / 255f, 225f / 255f, 53f / 255f),
            FluidType.Properties.create().canExtinguish(true).lightLevel(5).density(10).viscosity(3).sound(SoundAction.get("drink"), SoundEvents.HONEY_DRINK),
            200, 21,
            "Banana",
            "Kush"
    ));
    public static final Supplier<WeedFluidType> CARBON_FIBER_EXTRACT_FLUID_TYPE = registerWeedFluidType("carbon_fiber_extract_fluid", new WeedFluidType(
            WATER_STILL_RL, WATER_FLOWING_RL, WATER_OVERLAY_RL,
            0xFFa1a8a9, new Vector3f(75f / 255f, 75f / 255f, 75f / 255f),
            FluidType.Properties.create().canExtinguish(true).lightLevel(5).density(10).viscosity(3).sound(SoundAction.get("drink"), SoundEvents.HONEY_DRINK),
            230, 24,
            "Carbon",
            "Fiber"
    ));
    public static final Supplier<WeedFluidType> BIRTHDAY_CAKE_EXTRACT_FLUID_TYPE = registerWeedFluidType("birthday_cake_extract_fluid", new WeedFluidType(
            WATER_STILL_RL, WATER_FLOWING_RL, WATER_OVERLAY_RL,
            0xFFe9fdd1, new Vector3f(222f / 255f, 184f / 255f, 135f / 255f),
            FluidType.Properties.create().canExtinguish(true).lightLevel(5).density(10).viscosity(3).sound(SoundAction.get("drink"), SoundEvents.HONEY_DRINK),
            170, 23,
            "Birthday",
            "Cake"
    ));
    public static final Supplier<WeedFluidType> BLUE_COOKIES_EXTRACT_FLUID_TYPE = registerWeedFluidType("blue_cookies_extract_fluid", new WeedFluidType(
            WATER_STILL_RL, WATER_FLOWING_RL, WATER_OVERLAY_RL,
            0xFF9fb29f, new Vector3f(79f / 255f, 163f / 255f, 255f / 255f),
            FluidType.Properties.create().canExtinguish(true).lightLevel(5).density(10).viscosity(3).sound(SoundAction.get("drink"), SoundEvents.HONEY_DRINK),
            180, 17,
            "Blue",
            "Cookies"
    ));
    public static final Supplier<WeedFluidType> AFGHANI_EXTRACT_FLUID_TYPE = registerWeedFluidType("afghani_extract_fluid", new WeedFluidType(
            WATER_STILL_RL, WATER_FLOWING_RL, WATER_OVERLAY_RL,
            0xFFffcc95, new Vector3f(107f / 255f, 79f / 255f, 58f / 255f),
            FluidType.Properties.create().canExtinguish(true).lightLevel(5).density(10).viscosity(3).sound(SoundAction.get("drink"), SoundEvents.HONEY_DRINK),
            240, 18,
            "Afghani",
            "Kush"
    ));

    public static final Supplier<WeedFluidType> MOONBOW_EXTRACT_FLUID_TYPE = registerWeedFluidType("moonbow_extract_fluid", new WeedFluidType(
            WATER_STILL_RL, WATER_FLOWING_RL, WATER_OVERLAY_RL,
            0xFFcdde90, new Vector3f(185f / 255f, 255f / 255f, 253f / 255f),
            FluidType.Properties.create().canExtinguish(true).lightLevel(5).density(10).viscosity(3).sound(SoundAction.get("drink"), SoundEvents.HONEY_DRINK),
            215, 30,
            "Moonbow",
            "Kush"
    ));
    public static final Supplier<WeedFluidType> LAVA_CAKE_EXTRACT_FLUID_TYPE = registerWeedFluidType("lava_cake_extract_fluid", new WeedFluidType(
            WATER_STILL_RL, WATER_FLOWING_RL, WATER_OVERLAY_RL,
            0xFFe5916c, new Vector3f(255f / 255f, 69f / 255f, 0f / 255f),
            FluidType.Properties.create().canExtinguish(true).lightLevel(5).density(10).viscosity(3).sound(SoundAction.get("drink"), SoundEvents.HONEY_DRINK),
            175, 22,
            "Lava",
            "Cake"
    ));
    public static final Supplier<WeedFluidType> JELLY_RANCHER_EXTRACT_FLUID_TYPE = registerWeedFluidType("jelly_rancher_extract_fluid", new WeedFluidType(
            WATER_STILL_RL, WATER_FLOWING_RL, WATER_OVERLAY_RL,
            0xFFfff7b5, new Vector3f(123f / 255f, 104f / 255f, 238f / 255f),
            FluidType.Properties.create().canExtinguish(true).lightLevel(5).density(10).viscosity(3).sound(SoundAction.get("drink"), SoundEvents.HONEY_DRINK),
            165, 20,
            "Jelly",
            "Rancher"
    ));
    public static final Supplier<WeedFluidType> STRAWBERRY_SHORTCAKE_EXTRACT_FLUID_TYPE = registerWeedFluidType("strawberry_shortcake_extract_fluid", new WeedFluidType(
            WATER_STILL_RL, WATER_FLOWING_RL, WATER_OVERLAY_RL,
            0xFFdbcdc6, new Vector3f(255f / 255f, 99f / 255f, 132f / 255f),
            FluidType.Properties.create().canExtinguish(true).lightLevel(5).density(10).viscosity(3).sound(SoundAction.get("drink"), SoundEvents.HONEY_DRINK),
            160, 16,
            "Strawberry",
            "Shortcake"
    ));
    public static final Supplier<WeedFluidType> PINK_KUSH_EXTRACT_FLUID_TYPE = registerWeedFluidType("pink_kush_extract_fluid", new WeedFluidType(
            WATER_STILL_RL, WATER_FLOWING_RL, WATER_OVERLAY_RL,
            0xFFbe9c9a, new Vector3f(255f / 255f, 105f / 255f, 180f / 255f),
            FluidType.Properties.create().canExtinguish(true).lightLevel(5).density(10).viscosity(3).sound(SoundAction.get("drink"), SoundEvents.HONEY_DRINK),
            205, 19,
            "Pink",
            "Kush"
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
