package net.micaxs.smokeleaf.loot;

import com.mojang.serialization.MapCodec;
import net.micaxs.smokeleaf.SmokeleafIndustries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModLootItemFunctions {

    public static final DeferredRegister<MapCodec<? extends LootItemFunction>> LOOT_FUNCTION_TYPES =
            DeferredRegister.create(Registries.LOOT_FUNCTION_TYPE, SmokeleafIndustries.MODID);

    public static final DeferredHolder<MapCodec<? extends LootItemFunction>, MapCodec<ApplyBudStats>> APPLY_BUD_STATS =
            LOOT_FUNCTION_TYPES.register("apply_bud_stats", () -> ApplyBudStats.CODEC);
    public static final DeferredHolder<MapCodec<? extends LootItemFunction>, MapCodec<ApplyUnidentifiedStrain>> APPLY_UNIDENTIFIED_STRAIN =
            LOOT_FUNCTION_TYPES.register("apply_unidentified_strain", () -> ApplyUnidentifiedStrain.CODEC);
    public static final DeferredHolder<MapCodec<? extends LootItemFunction>, MapCodec<SetPresetStrain>> SET_PRESET_STRAIN =
            LOOT_FUNCTION_TYPES.register("set_preset_strain", () -> SetPresetStrain.CODEC);

    public static void register(IEventBus bus) {
        LOOT_FUNCTION_TYPES.register(bus);
    }
}
