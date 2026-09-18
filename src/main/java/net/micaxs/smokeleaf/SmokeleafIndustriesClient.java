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
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.common.NeoForge;

import java.awt.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Mod(value = SmokeleafIndustries.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = SmokeleafIndustries.MODID, value = Dist.CLIENT)
public class SmokeleafIndustriesClient {

    private static final Set<ResourceLocation> WIGGLED = new HashSet<>();

    private record WiggleSpec(double sx, double ax, double axLvl, boolean ix,
                              double sy, double ay, double ayLvl, boolean iy) {}

    private static final Map<ResourceLocation, WiggleSpec> WIGGLE_SPECS = new HashMap<>();
    static {
        WIGGLE_SPECS.put(VanillaGuiLayers.HOTBAR,             new WiggleSpec(0.10, 3.0, 1.5, false, 0.18, 1.4, 0.6, false));
        WIGGLE_SPECS.put(VanillaGuiLayers.PLAYER_HEALTH,      new WiggleSpec(0.13, 3.0, 1.5, true,  0.22, 1.8, 0.7, false));
        WIGGLE_SPECS.put(VanillaGuiLayers.FOOD_LEVEL,         new WiggleSpec(0.17, 2.5, 1.2, false, 0.25, 1.2, 0.5, true));
        WIGGLE_SPECS.put(VanillaGuiLayers.CHAT,               new WiggleSpec(0.21, 4.0, 1.0, false, 0.07, 6.0, 1.5, false));
        WIGGLE_SPECS.put(VanillaGuiLayers.TAB_LIST,           new WiggleSpec(0.15, 5.0, 2.0, true,  0.11, 3.5, 1.2, true));
        WIGGLE_SPECS.put(VanillaGuiLayers.CROSSHAIR,          new WiggleSpec(0.11, 2.0, 1.0, false, 0.19, 2.2, 0.8, false));
        WIGGLE_SPECS.put(VanillaGuiLayers.EFFECTS,            new WiggleSpec(0.19, 2.0, 1.0, false, 0.16, 1.6, 0.6, false));
        WIGGLE_SPECS.put(VanillaGuiLayers.EXPERIENCE_BAR,     new WiggleSpec(0.19, 2.0, 1.0, false, 0.27, 1.0, 0.4, true));
        WIGGLE_SPECS.put(VanillaGuiLayers.EXPERIENCE_LEVEL,   new WiggleSpec(0.11, 2.0, 1.0, false, 0.31, 0.8, 0.3, false));
        WIGGLE_SPECS.put(VanillaGuiLayers.SELECTED_ITEM_NAME, new WiggleSpec(0.08, 2.0, 1.0, false, 0.24, 1.3, 0.5, true));
    }

    public SmokeleafIndustriesClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    private static boolean hasMelted(Player p) {
        return p != null && p.getEffect(ModEffects.MELTED) != null;
    }

    private static float partial(RenderGuiLayerEvent event) {
        return event.getPartialTick().getGameTimeDeltaPartialTick(false);
    }

    private static double partial(ViewportEvent.ComputeFov event) {
        return event.getPartialTick();
    }



    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, BrainMeltInputHandler::onInputUpdate);
        event.enqueueWork(() -> {
            ItemBlockRenderTypes.setRenderLayer(ModFluids.SOURCE_HASH_OIL_FLUID.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(ModFluids.FLOWING_HASH_OIL_FLUID.get(), RenderType.translucent());


            ItemBlockRenderTypes.setRenderLayer(ModFluids.SOURCE_HASH_OIL_SLUDGE_FLUID.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(ModFluids.FLOWING_HASH_OIL_SLUDGE_FLUID.get(), RenderType.translucent());

            ItemBlockRenderTypes.setRenderLayer(ModFluids.SOURCE_UNIDENTIFIED_MIXTURE_FLUID.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(ModFluids.FLOWING_UNIDENTIFIED_MIXTURE_FLUID.get(), RenderType.translucent());



            ItemProperties.register(ModItems.DNA_STRAND.get(), ResourceLocation.fromNamespaceAndPath(SmokeleafIndustries.MODID, "full"), (stack, level, entity, seed) -> DNAStrandItem.isFull(stack) ? 1.0F : 0.0F);

            ItemProperties.register(ModItems.MANUAL_GRINDER.get(), ResourceLocation.fromNamespaceAndPath(SmokeleafIndustries.MODID, "filled"), (stack, level, entity, seed) -> stack.has(ModDataComponentTypes.MANUAL_GRINDER_CONTENTS.get()) ? 1.0F : 0.0F);

            ItemBlockRenderTypes.setRenderLayer(ModBlocks.REFLECTOR.get(), RenderType.translucent());
        });
    }

    @SubscribeEvent
    public static void registerBER(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.DRYING_RACK_BE.get(), DryingRackRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.GROW_POT.get(), GrowPotRenderer::new);

    }

    @SubscribeEvent
    public static void onRegisterGeometryLoaders(ModelEvent.RegisterGeometryLoaders event) {
        event.register(ResourceLocation.fromNamespaceAndPath(SmokeleafIndustries.MODID, "pipe"), PipeGeometryLoader.INSTANCE);
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
    public static void onClientExtensions(RegisterClientExtensionsEvent event) {
        event.registerFluidType(((BaseFluidType) ModFluidTypes.HASH_OIL_FLUID_TYPE.get()).getClientFluidTypeExtensions(), ModFluidTypes.HASH_OIL_FLUID_TYPE.get());
        event.registerFluidType(((BaseFluidType) ModFluidTypes.HASH_OIL_SLUDGE_FLUID_TYPE.get()).getClientFluidTypeExtensions(), ModFluidTypes.HASH_OIL_SLUDGE_FLUID_TYPE.get());


        event.registerFluidType((ModFluidTypes.UNIDENTIFIED_MIXTURE_FLUID_TYPE.get()).getClientFluidTypeExtensions(), ModFluidTypes.UNIDENTIFIED_MIXTURE_FLUID_TYPE.get());


    }

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenuTypes.GENERATOR_MENU.get(), GeneratorScreen::new);
        event.register(ModMenuTypes.GRINDER_MENU.get(), GrinderScreen::new);
        event.register(ModMenuTypes.EXTRACTOR_MENU.get(), ExtractorScreen::new);
        event.register(ModMenuTypes.LIQUIFIER_MENU.get(), LiquifierScreen::new);
        event.register(ModMenuTypes.MUTATOR_MENU.get(), MutatorScreen::new);
        event.register(ModMenuTypes.SYNTHESIZER_MENU.get(), SynthesizerScreen::new);
        event.register(ModMenuTypes.SEQUENCER_MENU.get(), SequencerScreen::new);
        event.register(ModMenuTypes.DRYER_MENU.get(), DryerScreen::new);
        event.register(ModMenuTypes.MIXER_MENU.get(), MixerScreen::new);
        event.register(ModMenuTypes.STRAIN_MODIFIER_MENU.get(), StrainModifierScreen::new);
        event.register(ModMenuTypes.GUMMY_MACHINE_MENU.get(), GummyMachineScreen::new);
    }

    @SubscribeEvent
    public static void onRenderGuiLayerPre(RenderGuiLayerEvent.Pre event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null || !hasMelted(player)) return;

        ResourceLocation id = event.getName();
        WiggleSpec spec = WIGGLE_SPECS.get(id);
        if (spec == null) return;

        MobEffectInstance melted = player.getEffect(ModEffects.MELTED);
        int ampLvl = melted != null ? melted.getAmplifier() : 0;

        double t = player.tickCount + partial(event);

        double phaseX = Math.sin(t * spec.sx());
        double phaseY = Math.sin(t * spec.sy() + Math.PI / 2.0); // phase shift to decorrelate
        double xAmp = spec.ax() + ampLvl * spec.axLvl();
        double yAmp = spec.ay() + ampLvl * spec.ayLvl();

        double xOffset = phaseX * xAmp * (spec.ix() ? -1 : 1);
        double yOffset = phaseY * yAmp * (spec.iy() ? -1 : 1);

        GuiGraphics g = event.getGuiGraphics();
        g.pose().pushPose();
        g.pose().translate(xOffset, yOffset, 0);
        WIGGLED.add(id);
    }

    @SubscribeEvent
    public static void onRenderGuiLayerPost(RenderGuiLayerEvent.Post event) {
        if (!WIGGLED.remove(event.getName())) return;
        event.getGuiGraphics().pose().popPose();
    }

    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null) return;
        if (!player.hasEffect(ModEffects.RAINBOW)) return;

        GuiGraphics gg = event.getGuiGraphics();
        float t = (System.currentTimeMillis() % 5000L) / 5000F;
        int rgb = Color.HSBtoRGB(t, 1F, 1F);
        int color = (80 << 24) | (rgb & 0xFFFFFF);
        gg.fill(0, 0, mc.getWindow().getGuiScaledWidth(), mc.getWindow().getGuiScaledHeight(), color);
    }

    @SubscribeEvent
    public static void onComputeFov(ViewportEvent.ComputeFov event) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null) return;

        double p = partial(event);
        MobEffectInstance breathing = player.getEffect(ModEffects.BREATHING);
        MobEffectInstance bubbled = player.getEffect(ModEffects.BUBBLED);

        if (breathing != null) {
            float time = (float)(player.tickCount + p);
            int amp = breathing.getAmplifier();
            float amplitude = 0.015f + amp * 0.010f;
            float speed = 0.15f + amp * 0.05f;
            float wave = (float)Math.sin(time * speed) * amplitude;
            event.setFOV(event.getFOV() * (1.0 + wave));
        }

        if (bubbled != null) {
            int amp = bubbled.getAmplifier();
            double boost = -0.50 + amp * 0.12;
            double modified = event.getFOV() * (1.0 + boost);
            event.setFOV(Math.min(170.0, modified));
        }

        // Stoned: very slow, deep FOV pulse — like a long relaxed inhale (~4.8s cycle).
        // Gated the same way the trip shader is: only once the streak requirement is met.
        MobEffectInstance stoned = player.getEffect(ModEffects.STONED);
        if (stoned != null && stoned.isVisible()) {
            float time = (float)(player.tickCount + p);
            int amp = stoned.getAmplifier();
            float amplitude = 0.009f + amp * 0.004f;
            float wave = (float)Math.sin(time * 0.065f) * amplitude;
            event.setFOV(event.getFOV() * (1.0 + wave));
        }
    }

    @SubscribeEvent
    public static void onCameraAngles(ViewportEvent.ComputeCameraAngles event) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null) return;

        MobEffectInstance stoned = player.getEffect(ModEffects.STONED);
        if (stoned != null && stoned.isVisible()) {
            int amp = stoned.getAmplifier();
            float time = (float)(player.tickCount + event.getPartialTick());
            // Two slow sines with different periods — feels organic, not mechanical
            float sway = (float)Math.sin(time * 0.020f) * (0.40f + amp * 0.15f);
            float drift = (float)Math.sin(time * 0.013f + 1.8f) * (0.20f + amp * 0.08f);
            event.setRoll(event.getRoll() + sway + drift);
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
            StrainData d = stack.get(ModDataComponentTypes.STRAIN_DATA.get());
            if (d != null) {
                Boolean isDry = stack.get(ModDataComponentTypes.DRY);
                boolean dry = Boolean.TRUE.equals(isDry);
                if (tintIndex == 0) return dry ? darkenColor(d.leafColor(), 0.88f) : d.leafColor();
                if (tintIndex == 1) return dry ? darkenColor(d.colorArgb(), 0.8f) : d.colorArgb();
            }
            return 0xFFFFFFFF;
        };

        // Weed item color: uses per-type weed colors, falling back to bud colors
        ItemColor weedItemColor = (stack, tintIndex) -> {
            StrainData d = stack.get(ModDataComponentTypes.STRAIN_DATA.get());
            if (d != null) {
                if (tintIndex == 0) return d.weedLeafColorEffective();
                if (tintIndex == 1) return d.weedColorArgbEffective();
            }
            return 0xFFFFFFFF;
        };

        // Seeds item color: uses per-type seeds colors, falling back to bud colors
        ItemColor seedsItemColor = (stack, tintIndex) -> {
            StrainData d = stack.get(ModDataComponentTypes.STRAIN_DATA.get());
            if (d != null) {
                if (tintIndex == 0) return d.seedsLeafColorEffective();
                if (tintIndex == 1) return d.seedsColorArgbEffective();
            }
            return 0xFFFFFFFF;
        };

        // Extract item color: base layer is untinted; only the mask (layer1) gets the strain color
        ItemColor extractItemColor = (stack, tintIndex) -> {
            StrainData d = stack.get(ModDataComponentTypes.STRAIN_DATA.get());
            if (d != null && tintIndex == 1) return d.extractColorArgbEffective();
            return 0xFFFFFFFF;
        };

        // Bud items
        event.register(budItemColor, ModItems.GENERIC_BUD.get());

        // Mixture bucket: base texture uncolored, only mask (layer1) gets the strain color
        ItemColor bucketItemColor = (stack, tintIndex) -> {
            if (tintIndex == 1) {
                StrainData d = stack.get(ModDataComponentTypes.STRAIN_DATA.get());
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
            StrainData d = stack.get(ModDataComponentTypes.STRAIN_DATA.get());
            if (d != null) {
                if (tintIndex == 1) return d.leafColor();
                if (tintIndex == 2) return d.colorArgb();
            }
            return 0xFFFFFFFF;
        };
        event.register(bagItemColor, ModItems.GENERIC_BAG.get());

        // Gummy Bear: 3 layers — bg (leafColor), mask1 (colorArgb), mask2 (lighter tone of colorArgb)
        ItemColor gummyItemColor = (stack, tintIndex) -> {
            StrainData d = stack.get(ModDataComponentTypes.STRAIN_DATA.get());
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
            StrainData d = stack.get(ModDataComponentTypes.STRAIN_DATA.get());
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
