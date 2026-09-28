package net.micaxs.smokeleaf.strain;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DataPackRegistryEvent;
public final class ModStrainRegistry {

    public static final ResourceKey<Registry<StrainData>> STRAIN_REGISTRY_KEY =
            ResourceKey.createRegistryKey(new ResourceLocation("smokeleafindustries", "strain"));

    private ModStrainRegistry() {
    }

    public static void register(IEventBus modBus) {
        modBus.addListener(ModStrainRegistry::onNewRegistry);
    }

    private static void onNewRegistry(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(STRAIN_REGISTRY_KEY, StrainData.CODEC, StrainData.CODEC);
    }
}
