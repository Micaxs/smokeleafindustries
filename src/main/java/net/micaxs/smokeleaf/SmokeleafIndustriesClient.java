package net.micaxs.smokeleaf;

import net.micaxs.smokeleaf.block.ModBlocks;
import net.micaxs.smokeleaf.block.entity.ModBlockEntities;
import net.micaxs.smokeleaf.block.entity.UnidentifiedWeedCropBlockEntity;
import net.micaxs.smokeleaf.block.entity.client.DryingRackRenderer;
import net.micaxs.smokeleaf.block.entity.render.GrowPotRenderer;
import net.micaxs.smokeleaf.client.brainmelt.BrainMeltInputHandler;
import net.micaxs.smokeleaf.client.guide.GuideDataLoader;
import net.micaxs.smokeleaf.client.model.PipeGeometryLoader;
import net.micaxs.smokeleaf.component.ModDataComponentTypes;
import net.micaxs.smokeleaf.effect.ModEffects;
import net.micaxs.smokeleaf.fluid.BaseFluidType;
import net.micaxs.smokeleaf.fluid.ModFluidTypes;
import net.micaxs.smokeleaf.fluid.ModFluids;
import net.micaxs.smokeleaf.item.ModItems;
import net.micaxs.smokeleaf.item.custom.DNAStrandItem;
import net.micaxs.smokeleaf.screen.ModMenuTypes;
import net.micaxs.smokeleaf.screen.custom.*;
import net.micaxs.smokeleaf.strain.StrainData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.client.event.*;



import net.minecraftforge.common.MinecraftForge;
import java.awt.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Mod.EventBusSubscriber(modid = SmokeleafIndustries.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class SmokeleafIndustriesClient {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, BrainMeltInputHandler::onInputUpdate);
        event.enqueueWork(() -> {
            ItemBlockRenderTypes.setRenderLayer(ModFluids.SOURCE_HASH_OIL_FLUID.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(ModFluids.FLOWING_HASH_OIL_FLUID.get(), RenderType.translucent());


            ItemBlockRenderTypes.setRenderLayer(ModFluids.SOURCE_HASH_OIL_SLUDGE_FLUID.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(ModFluids.FLOWING_HASH_OIL_SLUDGE_FLUID.get(), RenderType.translucent());

            ItemBlockRenderTypes.setRenderLayer(ModFluids.SOURCE_UNIDENTIFIED_MIXTURE_FLUID.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(ModFluids.FLOWING_UNIDENTIFIED_MIXTURE_FLUID.get(), RenderType.translucent());



            ItemProperties.register(ModItems.DNA_STRAND.get(), new ResourceLocation(SmokeleafIndustries.MODID, "full"), (stack, level, entity, seed) -> DNAStrandItem.isFull(stack) ? 1.0F : 0.0F);

            ItemProperties.register(ModItems.MANUAL_GRINDER.get(), new ResourceLocation(SmokeleafIndustries.MODID, "filled"), (stack, level, entity, seed) -> ModDataComponentTypes.MANUAL_GRINDER_CONTENTS.has(stack) ? 1.0F : 0.0F);

            ItemBlockRenderTypes.setRenderLayer(ModBlocks.REFLECTOR.get(), RenderType.translucent());

            MenuScreens.register(ModMenuTypes.GENERATOR_MENU.get(), GeneratorScreen::new);
            MenuScreens.register(ModMenuTypes.GRINDER_MENU.get(), GrinderScreen::new);
            MenuScreens.register(ModMenuTypes.EXTRACTOR_MENU.get(), ExtractorScreen::new);
            MenuScreens.register(ModMenuTypes.LIQUIFIER_MENU.get(), LiquifierScreen::new);
            MenuScreens.register(ModMenuTypes.MUTATOR_MENU.get(), MutatorScreen::new);
            MenuScreens.register(ModMenuTypes.SYNTHESIZER_MENU.get(), SynthesizerScreen::new);
            MenuScreens.register(ModMenuTypes.SEQUENCER_MENU.get(), SequencerScreen::new);
            MenuScreens.register(ModMenuTypes.DRYER_MENU.get(), DryerScreen::new);
            MenuScreens.register(ModMenuTypes.MIXER_MENU.get(), MixerScreen::new);
            MenuScreens.register(ModMenuTypes.STRAIN_MODIFIER_MENU.get(), StrainModifierScreen::new);
            MenuScreens.register(ModMenuTypes.GUMMY_MACHINE_MENU.get(), GummyMachineScreen::new);
        });
    }

    @SubscribeEvent
    public static void registerBER(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.DRYING_RACK_BE.get(), DryingRackRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.GROW_POT.get(), GrowPotRenderer::new);

    }

    @SubscribeEvent
    public static void onRegisterGeometryLoaders(ModelEvent.RegisterGeometryLoaders event) {
        event.register("pipe", PipeGeometryLoader.INSTANCE);
    }

    @SubscribeEvent
    public static void onRegisterReloadListeners(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(new GuideDataLoader());
    }

    @SubscribeEvent
    public static void onAddLayers(EntityRenderersEvent.AddLayers event) {
        for (var skin : event.getSkins()) {
            if (event.getSkin(skin) instanceof net.minecraft.client.renderer.entity.player.PlayerRenderer renderer) {
                renderer.addLayer(new net.micaxs.smokeleaf.client.render.RedEyesLayer(renderer));
            }
        }
    }


    @SubscribeEvent
    public static void onBlockColor(RegisterColorHandlersEvent.Block event) {
        BlockColor blockColor = (state, level, pos, tintIndex) -> {
            if (state == null || state.getBlock() != ModBlocks.UNIDENTIFIED_WEED_CROP.get()) {
                return 0xFFFFFFFF;
            }

            // tintIndex 0 => base leaf layer, use strain leafColor.
            if (tintIndex == 0 && level != null && pos != null) {
                BlockEntity be0 = level.getBlockEntity(pos);
                if (!(be0 instanceof UnidentifiedWeedCropBlockEntity) && state.hasProperty(net.micaxs.smokeleaf.block.custom.UnidentifiedWeedCropBlock.TOP)
                        && Boolean.TRUE.equals(state.getValue(net.micaxs.smokeleaf.block.custom.UnidentifiedWeedCropBlock.TOP))) {
                    be0 = level.getBlockEntity(pos.below());
                }
                if (be0 instanceof UnidentifiedWeedCropBlockEntity leafBe) {
                    StrainData ld = leafBe.getStrain();
                    if (ld != null && ld != StrainData.EMPTY) return ld.leafColor();
                }
                return 0xFF99D335; // fallback green
            }

            // tintIndex 1 => mask layer, strain color (in-world rendering only).
            if (tintIndex == 1 && level != null && pos != null) {
                BlockEntity be = level.getBlockEntity(pos);

                // StrainData is stored on the bottom half BE; top half has no BE, so look down one block.
                if (!(be instanceof UnidentifiedWeedCropBlockEntity) && state.hasProperty(net.micaxs.smokeleaf.block.custom.UnidentifiedWeedCropBlock.TOP)
                        && Boolean.TRUE.equals(state.getValue(net.micaxs.smokeleaf.block.custom.UnidentifiedWeedCropBlock.TOP))) {
                    be = level.getBlockEntity(pos.below());
                }

                if (be instanceof UnidentifiedWeedCropBlockEntity cropBe) {
                    StrainData d = cropBe.getStrain();
                    if (d != null && d != StrainData.EMPTY) {
                        return d.colorArgb();
                    }
                }
            }

            return 0xFFFFFFFF;
        };

        event.register(blockColor, ModBlocks.UNIDENTIFIED_WEED_CROP.get());
    }

    /** Scales an ARGB color's RGB channels by {@code factor} (alpha untouched) — used to darken/mute dried bud colors. */
    private static int darkenColor(int argb, float factor) {
        int a = (argb >> 24) & 0xFF;
        int r = Math.round(((argb >> 16) & 0xFF) * factor);
        int g = Math.round(((argb >> 8) & 0xFF) * factor);
        int b = Math.round((argb & 0xFF) * factor);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    @SubscribeEvent
    public static void onItemColor(RegisterColorHandlersEvent.Item event) {
        // Bud item color: uses leafColor (layer0) and colorArgb (layer1). Dried buds keep the
        // strain's own hue — just a bit darker/muted — instead of a hardcoded flat grey that
        // erased strain identity entirely.
        ItemColor budItemColor = (stack, tintIndex) -> {
            StrainData d = ModDataComponentTypes.STRAIN_DATA.get(stack);
            if (d != null) {
                Boolean isDry = ModDataComponentTypes.DRY.get(stack);
                boolean dry = Boolean.TRUE.equals(isDry);
                if (tintIndex == 0) return dry ? darkenColor(d.leafColor(), 0.88f) : d.leafColor();
                if (tintIndex == 1) return dry ? darkenColor(d.colorArgb(), 0.8f) : d.colorArgb();
            }
            return 0xFFFFFFFF;
        };

        // Weed item color: uses per-type weed colors, falling back to bud colors
        ItemColor weedItemColor = (stack, tintIndex) -> {
            StrainData d = ModDataComponentTypes.STRAIN_DATA.get(stack);
            if (d != null) {
                if (tintIndex == 0) return d.weedLeafColorEffective();
                if (tintIndex == 1) return d.weedColorArgbEffective();
            }
            return 0xFFFFFFFF;
        };

        // Seeds item color: uses per-type seeds colors, falling back to bud colors
        ItemColor seedsItemColor = (stack, tintIndex) -> {
            StrainData d = ModDataComponentTypes.STRAIN_DATA.get(stack);
            if (d != null) {
                if (tintIndex == 0) return d.seedsLeafColorEffective();
                if (tintIndex == 1) return d.seedsColorArgbEffective();
            }
            return 0xFFFFFFFF;
        };

        // Extract item color: base layer is untinted; only the mask (layer1) gets the strain color
        ItemColor extractItemColor = (stack, tintIndex) -> {
            StrainData d = ModDataComponentTypes.STRAIN_DATA.get(stack);
            if (d != null && tintIndex == 1) return d.extractColorArgbEffective();
            return 0xFFFFFFFF;
        };

        // Bud items
        event.register(budItemColor, ModItems.GENERIC_BUD.get());

        // Mixture bucket: base texture uncolored, only mask (layer1) gets the strain color
        ItemColor bucketItemColor = (stack, tintIndex) -> {
            if (tintIndex == 1) {
                StrainData d = ModDataComponentTypes.STRAIN_DATA.get(stack);
                if (d != null) return d.colorArgb();
            }
            return 0xFFFFFFFF;
        };
        event.register(bucketItemColor, ModFluids.UNIDENTIFIED_MIXTURE_BUCKET.get());

        // Weed items
        event.register(weedItemColor, ModItems.GENERIC_WEED.get());

        // Seeds items
        event.register(seedsItemColor, ModItems.GENERIC_SEEDS.get());

        // Extract items
        event.register(extractItemColor, ModItems.GENERIC_EXTRACT.get());

        // Bag uses 4 layers: bg (no tint), weed (leafColor), weed_mask (colorArgb), top_overlay (no tint)
        ItemColor bagItemColor = (stack, tintIndex) -> {
            if (tintIndex == 0 || tintIndex == 3) return 0xFFFFFFFF;
            StrainData d = ModDataComponentTypes.STRAIN_DATA.get(stack);
            if (d != null) {
                if (tintIndex == 1) return d.leafColor();
                if (tintIndex == 2) return d.colorArgb();
            }
            return 0xFFFFFFFF;
        };
        event.register(bagItemColor, ModItems.GENERIC_BAG.get());

        // Gummy Bear: 3 layers — bg (leafColor), mask1 (colorArgb), mask2 (lighter tone of colorArgb)
        ItemColor gummyItemColor = (stack, tintIndex) -> {
            StrainData d = ModDataComponentTypes.STRAIN_DATA.get(stack);
            if (d != null) {
                if (tintIndex == 0) return d.leafColor();
                if (tintIndex == 1) return d.colorArgb();
                if (tintIndex == 2) return lightenColor(d.colorArgb());
            }
            return 0xFFFFFFFF;
        };
        event.register(gummyItemColor, ModItems.GENERIC_GUMMY.get());

        // Gummy Worm: bg stays untinted, mask1 is the bud/strain color, mask2 is the plant/leaf color.
        ItemColor gummyWormItemColor = (stack, tintIndex) -> {
            StrainData d = ModDataComponentTypes.STRAIN_DATA.get(stack);
            if (d != null) {
                if (tintIndex == 1) return d.colorArgb();
                if (tintIndex == 2) return d.leafColor();
            }
            return 0xFFFFFFFF;
        };
        event.register(gummyWormItemColor, ModItems.GENERIC_GUMMY_WORM.get());
    }

    /** Blends each RGB channel 50% toward white, keeping original alpha. */
    private static int lightenColor(int argb) {
        int a = (argb >> 24) & 0xFF;
        int r = (argb >> 16) & 0xFF;
        int g = (argb >> 8) & 0xFF;
        int b = argb & 0xFF;
        r = r + (255 - r) / 2;
        g = g + (255 - g) / 2;
        b = b + (255 - b) / 2;
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

}
