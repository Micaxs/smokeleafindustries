package net.micaxs.smokeleaf.datagen;

import net.micaxs.smokeleaf.SmokeleafIndustries;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = SmokeleafIndustries.MODID)
public class DataGenerators {

    // The data run is a client data run (it needs the client-side model classes), which generates
    // both the resource pack and the data pack.
    @SubscribeEvent
    public static void gatherData(GatherDataEvent.Client event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        generator.addProvider(true, new LootTableProvider(packOutput, Collections.emptySet(), List.of(new LootTableProvider.SubProviderEntry(ModBlockLootTableProvider::new, LootContextParamSets.BLOCK)), lookupProvider));

        generator.addProvider(true, new ModRecipeProvider.Runner(packOutput, lookupProvider));

        generator.addProvider(true, new ModBlockTagProvider(packOutput, lookupProvider));
        generator.addProvider(true, new ModItemTagProvider(packOutput, lookupProvider));
        generator.addProvider(true, new ModFluidTagsProvider(packOutput, lookupProvider));

        generator.addProvider(true, new ModModelProvider(packOutput));

        generator.addProvider(true, new ModDataMapProvder(packOutput, lookupProvider));
        generator.addProvider(true, new ModGlobalLootModifierProvider(packOutput, lookupProvider));
        generator.addProvider(true, new ModAdvancementProvider(packOutput, lookupProvider));

        generator.addProvider(true, new ModWorldgenProvider(packOutput, lookupProvider));

    }

}
