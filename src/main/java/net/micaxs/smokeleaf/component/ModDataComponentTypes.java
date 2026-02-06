package net.micaxs.smokeleaf.component;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import net.micaxs.smokeleaf.SmokeleafIndustries;
import net.micaxs.smokeleaf.fluid.WeedFluidData;
import net.micaxs.smokeleaf.strain.StrainData;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.UnaryOperator;

public class ModDataComponentTypes {

    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES =
            DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, SmokeleafIndustries.MODID);

    private static final Codec<JsonArray> JSON_ARRAY_CODEC = Codec.STRING.xmap(
            s -> {
                try {
                    return JsonParser.parseString(s).getAsJsonArray();
                } catch (Exception e) {
                    return new JsonArray();
                }
            },
            JsonArray::toString
    );


    // Weed Ingredients (Effects)
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<String>> ACTIVE_INGREDIENT = register("active_ingredient", builder -> builder.persistent(Codec.STRING));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<JsonArray>> ACTIVE_INGREDIENTS = register("active_ingredients", builder -> builder.persistent(JSON_ARRAY_CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> EFFECT_DURATION = register("effect_duration", builder -> builder.persistent(Codec.INT));

    // Weed Drying Time
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> DRY = register("dry", builder -> builder.persistent(Codec.BOOL));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> DRYING_TIME = register("drying_time", builder -> builder.persistent(Codec.INT));

    // Weed THC/CBD Content
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> THC = register("thc", builder -> builder.persistent(Codec.INT));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> CBD = register("cbd", builder -> builder.persistent(Codec.INT));

    // Weed Extract Fluid payload (effects attached at machine-time to FluidStacks)
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<WeedFluidData>> WEED_FLUID_DATA =
            register("weed_fluid_data", b -> b.persistent(WeedFluidData.CODEC));

    // Plant Nutrients (Increase THC with: 5N 10P 14K) (Increase CBD with: 8N 4P 13K) (Optimal pH: 6.0 - 7.0)
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> PH = register("ph", builder -> builder.persistent(Codec.INT));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> NITROGEN = register("nitrogen", builder -> builder.persistent(Codec.INT));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> PHOSPHORUS = register("phosphorus", builder -> builder.persistent(Codec.INT));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> POTASSIUM = register("potassium", builder -> builder.persistent(Codec.INT));

    // DNA Contents
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<DNAContents>> DNA_CONTENTS =
            register("dna_contents", b -> b.persistent(DNAContents.CODEC));


    // Manual Grinder stored contents (immutable)
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ManualGrinderContents>> MANUAL_GRINDER_CONTENTS =
            register("manual_grinder_contents", b -> b.persistent(ManualGrinderContents.CODEC));

    // Custom strain payload for player-created strains (items and mixture fluids)
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<StrainData>> STRAIN_DATA =
            register("strain_data", b -> b.persistent(StrainData.CODEC));

    private static <T>DeferredHolder<DataComponentType<?>, DataComponentType<T>> register(String name, UnaryOperator<DataComponentType.Builder<T>> builderOperator) {
        return DATA_COMPONENT_TYPES.register(name, () -> builderOperator.apply(DataComponentType.builder()).build());
    }

    public static void register(IEventBus eventBus) {
        DATA_COMPONENT_TYPES.register(eventBus);
    }

}
