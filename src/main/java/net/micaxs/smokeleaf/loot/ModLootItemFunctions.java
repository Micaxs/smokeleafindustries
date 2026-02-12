package net.micaxs.smokeleaf.loot;

import net.micaxs.smokeleaf.SmokeleafIndustries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModLootItemFunctions {

    public static final DeferredRegister<LootItemFunctionType<?>> LOOT_FUNCTION_TYPES =
            DeferredRegister.create(Registries.LOOT_FUNCTION_TYPE, SmokeleafIndustries.MODID);

    public static final DeferredHolder<LootItemFunctionType<?>, LootItemFunctionType<ApplyBudStats>> APPLY_BUD_STATS =
            LOOT_FUNCTION_TYPES.register("apply_bud_stats", () -> new LootItemFunctionType<>(ApplyBudStats.CODEC));
    public static final DeferredHolder<LootItemFunctionType<?>, LootItemFunctionType<ApplyUnidentifiedStrain>> APPLY_UNIDENTIFIED_STRAIN =
            LOOT_FUNCTION_TYPES.register("apply_unidentified_strain", () -> new LootItemFunctionType<>(ApplyUnidentifiedStrain.CODEC));

    public static void register(IEventBus bus) {
        LOOT_FUNCTION_TYPES.register(bus);
    }
}