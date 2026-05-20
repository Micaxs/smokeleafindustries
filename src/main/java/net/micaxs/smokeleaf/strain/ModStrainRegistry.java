package net.micaxs.smokeleaf.strain;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;

public final class ModStrainRegistry {

    public static final ResourceKey<Registry<StrainData>> STRAIN_REGISTRY_KEY =
            ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath("smokeleafindustries", "strain"));

    private ModStrainRegistry() {
    }

    public static void register(IEventBus modBus) {
        modBus.addListener(ModStrainRegistry::onNewRegistry);
    }

    private static void onNewRegistry(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(STRAIN_REGISTRY_KEY, StrainData.CODEC, StrainData.CODEC);
    }
}
