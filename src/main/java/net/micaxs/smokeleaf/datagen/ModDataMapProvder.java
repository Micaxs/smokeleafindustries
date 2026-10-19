package net.micaxs.smokeleaf.datagen;

import net.micaxs.smokeleaf.item.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DataMapProvider;
import net.neoforged.neoforge.registries.datamaps.builtin.FurnaceFuel;
import net.neoforged.neoforge.registries.datamaps.builtin.NeoForgeDataMaps;

import java.util.concurrent.CompletableFuture;

public class ModDataMapProvder extends DataMapProvider {
    protected ModDataMapProvder(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(packOutput, lookupProvider);
    }

    @Override
    protected void gather(HolderLookup.Provider provider) {
    }

//    @Override
//    protected void gather(HolderLookup.Provider provider) {
//        this.builder(NeoForgeDataMaps.FURNACE_FUELS)
//                .add(ModItems.SOMEWEEDFUEL.getId(), new FurnaceFuel(1200), false);
//    }
}
