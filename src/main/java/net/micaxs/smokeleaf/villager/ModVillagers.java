package net.micaxs.smokeleaf.villager;

import net.minecraft.core.registries.Registries;

import com.google.common.collect.ImmutableSet;
import net.micaxs.smokeleaf.SmokeleafIndustries;
import net.micaxs.smokeleaf.block.ModBlocks;
import net.micaxs.smokeleaf.sound.ModSounds;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
public class ModVillagers {

    public static DeferredRegister<PoiType> POI_TYPES = DeferredRegister.create(Registries.POINT_OF_INTEREST_TYPE, SmokeleafIndustries.MODID);
    public static DeferredRegister<VillagerProfession> VILLAGER_PROFESSIONS = DeferredRegister.create(Registries.VILLAGER_PROFESSION, SmokeleafIndustries.MODID);

    // Point of Interest Types
    public static final RegistryObject<PoiType> DEALER_POI = POI_TYPES.register("dealer_poi", () -> new PoiType(ImmutableSet.copyOf(ModBlocks.GRINDER.get().getStateDefinition().getPossibleStates()), 1, 1));
    public static final RegistryObject<PoiType> STONER_POI = POI_TYPES.register("stoner_poi", () -> new PoiType(ImmutableSet.copyOf(ModBlocks.HEMP_CHISELED_STONE.get().getStateDefinition().getPossibleStates()), 1, 1));

    // Villagers
    public static final RegistryObject<VillagerProfession> DEALER = VILLAGER_PROFESSIONS.register("dealer", () -> new VillagerProfession("dealer", holder -> holder.value() == DEALER_POI.get(), holder -> holder.value() == DEALER_POI.get(),
            ImmutableSet.of(), ImmutableSet.of(), ModSounds.MANUAL_GRINDER.get()));
    public static final RegistryObject<VillagerProfession> STONER = VILLAGER_PROFESSIONS.register("stoner", () -> new VillagerProfession("stoner", holder -> holder.value() == STONER_POI.get(), holder -> holder.value() == STONER_POI.get(),
            ImmutableSet.of(), ImmutableSet.of(), ModSounds.BONG_HIT.get()));


    public static void register(IEventBus eventBus) {
        POI_TYPES.register(eventBus);
        VILLAGER_PROFESSIONS.register(eventBus);
    }
}
