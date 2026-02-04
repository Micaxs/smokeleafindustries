package net.micaxs.smokeleaf;

import net.micaxs.smokeleaf.block.ModBlocks;
import net.micaxs.smokeleaf.block.entity.ModBlockEntities;
import net.micaxs.smokeleaf.component.ModDataComponentTypes;
import net.micaxs.smokeleaf.effect.ModEffects;
import net.micaxs.smokeleaf.effect.ModParticles;
import net.micaxs.smokeleaf.fluid.ModFluidTypes;
import net.micaxs.smokeleaf.fluid.ModFluids;
import net.micaxs.smokeleaf.item.ModItems;
import net.micaxs.smokeleaf.item.custom.BaseWeedItem;
import net.micaxs.smokeleaf.loot.ModLootItemFunctions;
import net.micaxs.smokeleaf.loot.ModLootModifiers;
import net.micaxs.smokeleaf.network.ModPayloads;
import net.micaxs.smokeleaf.recipe.ModRecipes;
import net.micaxs.smokeleaf.screen.ModMenuTypes;
import net.micaxs.smokeleaf.sound.ModSounds;
import net.micaxs.smokeleaf.villager.ModVillagers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.ComposterBlock;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

import java.util.List;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(SmokeleafIndustries.MODID)
public class SmokeleafIndustries {
    public static final String MODID = "smokeleafindustries";
    public static final Logger LOGGER = LogUtils.getLogger();


    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public SmokeleafIndustries(IEventBus modEventBus, ModContainer modContainer) {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register ourselves for server and other game events we are interested in.
        // Note that this is necessary if and only if we want *this* class (SmokeleafIndustries) to respond directly to events.
        // Do not add this line if there are no @SubscribeEvent-annotated functions in this class, like onServerStarting() below.
        NeoForge.EVENT_BUS.register(this);

        ModCreativeModeTabs.register(modEventBus);

        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModMenuTypes.register(modEventBus);
        ModRecipes.register(modEventBus);

        ModEffects.register(modEventBus);
        ModSounds.register(modEventBus);

        ModDataComponentTypes.register(modEventBus);
        ModLootModifiers.register(modEventBus);
        ModLootItemFunctions.register(modEventBus);

        ModParticles.register(modEventBus);

        ModVillagers.register(modEventBus);

        ModFluidTypes.register(modEventBus);
        ModFluids.register(modEventBus);

        modEventBus.register(ModPayloads.class);

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        // Some common setup code
        LOGGER.info("SmokeleafIndustries loading...");

        event.enqueueWork(() -> {
            BaseWeedItem.setAdditionalEffectPool(List.of(
                    ResourceLocation.fromNamespaceAndPath(SmokeleafIndustries.MODID, "stoned"),
                    ResourceLocation.fromNamespaceAndPath(SmokeleafIndustries.MODID, "sleepy"),
                    ResourceLocation.fromNamespaceAndPath(SmokeleafIndustries.MODID, "rainbow"),
                    ResourceLocation.fromNamespaceAndPath(SmokeleafIndustries.MODID, "breathing"),
                    ResourceLocation.fromNamespaceAndPath(SmokeleafIndustries.MODID, "bubbled"),
                    ResourceLocation.fromNamespaceAndPath(SmokeleafIndustries.MODID, "melted"),
                    ResourceLocation.fromNamespaceAndPath(SmokeleafIndustries.MODID, "dizzy"),
                    ResourceLocation.fromNamespaceAndPath(SmokeleafIndustries.MODID, "paranoia"),
                    ResourceLocation.fromNamespaceAndPath(SmokeleafIndustries.MODID, "dry_eyes"),
                    ResourceLocation.fromNamespaceAndPath(SmokeleafIndustries.MODID, "brain_melt")
            ));
        });

    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("Smokeleaf Industries server starting...");
    }
}
