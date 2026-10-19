package net.micaxs.smokeleaf.villager;

import com.google.common.collect.ImmutableSet;
import net.micaxs.smokeleaf.SmokeleafIndustries;
import net.micaxs.smokeleaf.block.ModBlocks;
import net.micaxs.smokeleaf.sound.ModSounds;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModVillagers {

    public static DeferredRegister<PoiType> POI_TYPES = DeferredRegister.create(BuiltInRegistries.POINT_OF_INTEREST_TYPE, SmokeleafIndustries.MODID);
    public static DeferredRegister<VillagerProfession> VILLAGER_PROFESSIONS = DeferredRegister.create(BuiltInRegistries.VILLAGER_PROFESSION, SmokeleafIndustries.MODID);

    // Point of Interest Types
    public static final Holder<PoiType> DEALER_POI = POI_TYPES.register("dealer_poi", () -> new PoiType(ImmutableSet.copyOf(ModBlocks.GRINDER.get().getStateDefinition().getPossibleStates()), 1, 1));
    public static final Holder<PoiType> STONER_POI = POI_TYPES.register("stoner_poi", () -> new PoiType(ImmutableSet.copyOf(ModBlocks.HEMP_CHISELED_STONE.get().getStateDefinition().getPossibleStates()), 1, 1));

    // Villagers
    public static final Holder<VillagerProfession> DEALER = VILLAGER_PROFESSIONS.register("dealer", () -> new VillagerProfession(Component.translatable("entity.minecraft.villager.smokeleafindustries.dealer"),
            holder -> holder.value() == DEALER_POI.value(), holder -> holder.value() == DEALER_POI.value(),
            ImmutableSet.of(), ImmutableSet.of(), ModSounds.MANUAL_GRINDER.get(), ModTrades.DEALER_TRADE_SETS));
    public static final Holder<VillagerProfession> STONER = VILLAGER_PROFESSIONS.register("stoner", () -> new VillagerProfession(Component.translatable("entity.minecraft.villager.smokeleafindustries.stoner"),
            holder -> holder.value() == STONER_POI.value(), holder -> holder.value() == STONER_POI.value(),
            ImmutableSet.of(), ImmutableSet.of(), ModSounds.BONG_HIT.get(), ModTrades.STONER_TRADE_SETS));


    public static void register(IEventBus eventBus) {
        POI_TYPES.register(eventBus);
        VILLAGER_PROFESSIONS.register(eventBus);
    }
}
