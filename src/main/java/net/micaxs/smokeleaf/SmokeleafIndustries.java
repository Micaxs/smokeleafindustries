package net.micaxs.smokeleaf;

import net.micaxs.smokeleaf.block.ModBlocks;
import net.micaxs.smokeleaf.block.entity.ModBlockEntities;
import net.micaxs.smokeleaf.effect.ModEffects;
import net.micaxs.smokeleaf.effect.ModParticles;
import net.micaxs.smokeleaf.fluid.ModFluidTypes;
import net.micaxs.smokeleaf.fluid.ModFluids;
import net.micaxs.smokeleaf.item.ModItems;
import net.micaxs.smokeleaf.item.custom.BaseWeedItem;
import net.micaxs.smokeleaf.loot.ModLootItemFunctions;
import net.micaxs.smokeleaf.loot.ModLootModifiers;
import net.micaxs.smokeleaf.network.ModNetwork;
import net.micaxs.smokeleaf.recipe.ModRecipes;
import net.micaxs.smokeleaf.screen.ModMenuTypes;
import net.micaxs.smokeleaf.sound.ModSounds;
import net.micaxs.smokeleaf.strain.ModStrainRegistry;
import net.micaxs.smokeleaf.strain.StrainRegistry;
import net.micaxs.smokeleaf.villager.ModVillagers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.ComposterBlock;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraft.world.level.ItemLike;

import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import java.util.List;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(SmokeleafIndustries.MODID)
public class SmokeleafIndustries {
    public static final String MODID = "smokeleafindustries";
    public static final Logger LOGGER = LogUtils.getLogger();


    // The constructor for the mod class is the first code that is run when your mod is loaded.
    public SmokeleafIndustries() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);
        ModStrainRegistry.register(modEventBus);

        // Register ourselves for server and other game events we are interested in.
        // Note that this is necessary if and only if we want *this* class (SmokeleafIndustries) to respond directly to events.
        // Do not add this line if there are no @SubscribeEvent-annotated functions in this class, like onServerStarting() below.
        MinecraftForge.EVENT_BUS.register(this);

        ModCreativeModeTabs.register(modEventBus);

        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModMenuTypes.register(modEventBus);
        ModRecipes.register(modEventBus);

        ModEffects.register(modEventBus);
        ModSounds.register(modEventBus);

        ModLootModifiers.register(modEventBus);
        ModLootItemFunctions.register(modEventBus);

        ModParticles.register(modEventBus);

        ModVillagers.register(modEventBus);

        ModFluidTypes.register(modEventBus);
        ModFluids.register(modEventBus);

        // Register our mod's ForgeConfigSpec so that FML can create and load the config file for us
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        // Some common setup code
        LOGGER.info("SmokeleafIndustries loading...");

        event.enqueueWork(() -> {
            ModNetwork.register();

            // Compostables (was a NeoForge data map in 1.21)
            ComposterBlock.COMPOSTABLES.put(ModItems.HEMP_LEAF.get(), 0.4F);

            BaseWeedItem.setAdditionalEffectPool(List.of(
                    new ResourceLocation(SmokeleafIndustries.MODID, "stoned"),
                    new ResourceLocation(SmokeleafIndustries.MODID, "sleepy"),
                    new ResourceLocation(SmokeleafIndustries.MODID, "rainbow"),
                    new ResourceLocation(SmokeleafIndustries.MODID, "breathing"),
                    new ResourceLocation(SmokeleafIndustries.MODID, "bubbled"),
                    new ResourceLocation(SmokeleafIndustries.MODID, "melted"),
                    new ResourceLocation(SmokeleafIndustries.MODID, "dizzy"),
                    new ResourceLocation(SmokeleafIndustries.MODID, "paranoia"),
                    new ResourceLocation(SmokeleafIndustries.MODID, "dry_eyes"),
                    new ResourceLocation(SmokeleafIndustries.MODID, "brain_melt")
            ));
        });

    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("Smokeleaf Industries server starting...");
        try {
            var registry = event.getServer().registryAccess()
                    .registryOrThrow(ModStrainRegistry.STRAIN_REGISTRY_KEY);
            StrainRegistry.reload(registry);
        } catch (Exception e) {
            LOGGER.error("Failed to reload StrainRegistry from datapack registry", e);
        }
    }
}
