package net.micaxs.smokeleaf.loot;

import net.micaxs.smokeleaf.SmokeleafIndustries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
public class ModLootItemFunctions {

    public static final DeferredRegister<LootItemFunctionType> LOOT_FUNCTION_TYPES =
            DeferredRegister.create(Registries.LOOT_FUNCTION_TYPE, SmokeleafIndustries.MODID);

    public static final RegistryObject<LootItemFunctionType> APPLY_BUD_STATS =
            LOOT_FUNCTION_TYPES.register("apply_bud_stats", () -> new LootItemFunctionType(new ApplyBudStats.Serializer()));
    public static final RegistryObject<LootItemFunctionType> APPLY_UNIDENTIFIED_STRAIN =
            LOOT_FUNCTION_TYPES.register("apply_unidentified_strain", () -> new LootItemFunctionType(new ApplyUnidentifiedStrain.Serializer()));

    public static void register(IEventBus bus) {
        LOOT_FUNCTION_TYPES.register(bus);
    }
}