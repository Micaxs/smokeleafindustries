package net.micaxs.smokeleaf;

import net.micaxs.smokeleaf.block.ModBlocks;
import net.micaxs.smokeleaf.block.entity.ModBlockEntities;
import net.micaxs.smokeleaf.block.entity.UnidentifiedWeedCropBlockEntity;
import net.micaxs.smokeleaf.block.entity.client.DryingRackRenderer;
import net.micaxs.smokeleaf.block.entity.render.GrowPotRenderer;
import net.micaxs.smokeleaf.client.brainmelt.BrainMeltInputHandler;
import net.micaxs.smokeleaf.component.ModDataComponentTypes;
import net.micaxs.smokeleaf.effect.ModEffects;
import net.micaxs.smokeleaf.fluid.BaseFluidType;
import net.micaxs.smokeleaf.fluid.ModFluidTypes;
import net.micaxs.smokeleaf.fluid.ModFluids;
import net.micaxs.smokeleaf.item.ModItems;
import net.micaxs.smokeleaf.item.custom.BaseBudItem;
import net.micaxs.smokeleaf.item.custom.DNAStrandItem;
import net.micaxs.smokeleaf.item.custom.UnidentifiedSeedsItem;
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

            ItemBlockRenderTypes.setRenderLayer(ModFluids.SOURCE_HEMP_OIL_FLUID.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(ModFluids.FLOWING_HEMP_OIL_FLUID.get(), RenderType.translucent());

            ItemBlockRenderTypes.setRenderLayer(ModFluids.SOURCE_HASH_OIL_SLUDGE_FLUID.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(ModFluids.FLOWING_HASH_OIL_SLUDGE_FLUID.get(), RenderType.translucent());

            ItemBlockRenderTypes.setRenderLayer(ModFluids.SOURCE_WHITE_WIDOW_EXTRACT_FLUID.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(ModFluids.FLOWING_WHITE_WIDOW_EXTRACT_FLUID.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(ModFluids.SOURCE_BUBBLE_KUSH_EXTRACT_FLUID.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(ModFluids.FLOWING_BUBBLE_KUSH_EXTRACT_FLUID.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(ModFluids.SOURCE_LEMON_HAZE_EXTRACT_FLUID.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(ModFluids.FLOWING_LEMON_HAZE_EXTRACT_FLUID.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(ModFluids.SOURCE_SOUR_DIESEL_EXTRACT_FLUID.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(ModFluids.FLOWING_SOUR_DIESEL_EXTRACT_FLUID.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(ModFluids.SOURCE_BLUE_ICE_EXTRACT_FLUID.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(ModFluids.FLOWING_BLUE_ICE_EXTRACT_FLUID.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(ModFluids.SOURCE_BUBBLEGUM_EXTRACT_FLUID.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(ModFluids.FLOWING_BUBBLEGUM_EXTRACT_FLUID.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(ModFluids.SOURCE_PURPLE_HAZE_EXTRACT_FLUID.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(ModFluids.FLOWING_PURPLE_HAZE_EXTRACT_FLUID.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(ModFluids.SOURCE_OG_KUSH_EXTRACT_FLUID.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(ModFluids.FLOWING_OG_KUSH_EXTRACT_FLUID.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(ModFluids.SOURCE_JACK_HERER_EXTRACT_FLUID.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(ModFluids.FLOWING_JACK_HERER_EXTRACT_FLUID.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(ModFluids.SOURCE_GARY_PEYTON_EXTRACT_FLUID.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(ModFluids.FLOWING_GARY_PEYTON_EXTRACT_FLUID.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(ModFluids.SOURCE_AMNESIA_HAZE_EXTRACT_FLUID.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(ModFluids.FLOWING_AMNESIA_HAZE_EXTRACT_FLUID.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(ModFluids.SOURCE_AK47_EXTRACT_FLUID.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(ModFluids.FLOWING_AK47_EXTRACT_FLUID.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(ModFluids.SOURCE_GHOST_TRAIN_EXTRACT_FLUID.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(ModFluids.FLOWING_GHOST_TRAIN_EXTRACT_FLUID.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(ModFluids.SOURCE_GRAPE_APE_EXTRACT_FLUID.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(ModFluids.FLOWING_GRAPE_APE_EXTRACT_FLUID.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(ModFluids.SOURCE_COTTON_CANDY_EXTRACT_FLUID.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(ModFluids.FLOWING_COTTON_CANDY_EXTRACT_FLUID.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(ModFluids.SOURCE_BANANA_KUSH_EXTRACT_FLUID.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(ModFluids.FLOWING_BANANA_KUSH_EXTRACT_FLUID.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(ModFluids.SOURCE_CARBON_FIBER_EXTRACT_FLUID.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(ModFluids.FLOWING_CARBON_FIBER_EXTRACT_FLUID.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(ModFluids.SOURCE_BIRTHDAY_CAKE_EXTRACT_FLUID.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(ModFluids.FLOWING_BIRTHDAY_CAKE_EXTRACT_FLUID.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(ModFluids.SOURCE_BLUE_COOKIES_EXTRACT_FLUID.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(ModFluids.FLOWING_BLUE_COOKIES_EXTRACT_FLUID.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(ModFluids.SOURCE_AFGHANI_EXTRACT_FLUID.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(ModFluids.FLOWING_AFGHANI_EXTRACT_FLUID.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(ModFluids.SOURCE_MOONBOW_EXTRACT_FLUID.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(ModFluids.FLOWING_MOONBOW_EXTRACT_FLUID.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(ModFluids.SOURCE_LAVA_CAKE_EXTRACT_FLUID.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(ModFluids.FLOWING_LAVA_CAKE_EXTRACT_FLUID.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(ModFluids.SOURCE_JELLY_RANCHER_EXTRACT_FLUID.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(ModFluids.FLOWING_JELLY_RANCHER_EXTRACT_FLUID.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(ModFluids.SOURCE_STRAWBERRY_SHORTCAKE_EXTRACT_FLUID.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(ModFluids.FLOWING_STRAWBERRY_SHORTCAKE_EXTRACT_FLUID.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(ModFluids.SOURCE_PINK_KUSH_EXTRACT_FLUID.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(ModFluids.FLOWING_PINK_KUSH_EXTRACT_FLUID.get(), RenderType.translucent());
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
    public static void onClientExtensions(RegisterClientExtensionsEvent event) {
        event.registerFluidType(((BaseFluidType) ModFluidTypes.HEMP_OIL_FLUID_TYPE.get()).getClientFluidTypeExtensions(), ModFluidTypes.HEMP_OIL_FLUID_TYPE.get());
        event.registerFluidType(((BaseFluidType) ModFluidTypes.HASH_OIL_FLUID_TYPE.get()).getClientFluidTypeExtensions(), ModFluidTypes.HASH_OIL_FLUID_TYPE.get());
        event.registerFluidType(((BaseFluidType) ModFluidTypes.HASH_OIL_SLUDGE_FLUID_TYPE.get()).getClientFluidTypeExtensions(), ModFluidTypes.HASH_OIL_SLUDGE_FLUID_TYPE.get());

        event.registerFluidType((ModFluidTypes.WHITE_WIDOW_EXTRACT_FLUID_TYPE.get()).getClientFluidTypeExtensions(), ModFluidTypes.WHITE_WIDOW_EXTRACT_FLUID_TYPE.get());

        event.registerFluidType((ModFluidTypes.BUBBLE_KUSH_EXTRACT_FLUID_TYPE.get()).getClientFluidTypeExtensions(), ModFluidTypes.BUBBLE_KUSH_EXTRACT_FLUID_TYPE.get());
        event.registerFluidType((ModFluidTypes.LEMON_HAZE_EXTRACT_FLUID_TYPE.get()).getClientFluidTypeExtensions(), ModFluidTypes.LEMON_HAZE_EXTRACT_FLUID_TYPE.get());
        event.registerFluidType((ModFluidTypes.SOUR_DIESEL_EXTRACT_FLUID_TYPE.get()).getClientFluidTypeExtensions(), ModFluidTypes.SOUR_DIESEL_EXTRACT_FLUID_TYPE.get());
        event.registerFluidType((ModFluidTypes.BLUE_ICE_EXTRACT_FLUID_TYPE.get()).getClientFluidTypeExtensions(), ModFluidTypes.BLUE_ICE_EXTRACT_FLUID_TYPE.get());
        event.registerFluidType((ModFluidTypes.BUBBLEGUM_EXTRACT_FLUID_TYPE.get()).getClientFluidTypeExtensions(), ModFluidTypes.BUBBLEGUM_EXTRACT_FLUID_TYPE.get());
        event.registerFluidType((ModFluidTypes.PURPLE_HAZE_EXTRACT_FLUID_TYPE.get()).getClientFluidTypeExtensions(), ModFluidTypes.PURPLE_HAZE_EXTRACT_FLUID_TYPE.get());
        event.registerFluidType((ModFluidTypes.OG_KUSH_EXTRACT_FLUID_TYPE.get()).getClientFluidTypeExtensions(), ModFluidTypes.OG_KUSH_EXTRACT_FLUID_TYPE.get());
        event.registerFluidType((ModFluidTypes.JACK_HERER_EXTRACT_FLUID_TYPE.get()).getClientFluidTypeExtensions(), ModFluidTypes.JACK_HERER_EXTRACT_FLUID_TYPE.get());
        event.registerFluidType((ModFluidTypes.GARY_PEYTON_EXTRACT_FLUID_TYPE.get()).getClientFluidTypeExtensions(), ModFluidTypes.GARY_PEYTON_EXTRACT_FLUID_TYPE.get());
        event.registerFluidType((ModFluidTypes.AMNESIA_HAZE_EXTRACT_FLUID_TYPE.get()).getClientFluidTypeExtensions(), ModFluidTypes.AMNESIA_HAZE_EXTRACT_FLUID_TYPE.get());
        event.registerFluidType((ModFluidTypes.AK47_EXTRACT_FLUID_TYPE.get()).getClientFluidTypeExtensions(), ModFluidTypes.AK47_EXTRACT_FLUID_TYPE.get());
        event.registerFluidType((ModFluidTypes.GHOST_TRAIN_EXTRACT_FLUID_TYPE.get()).getClientFluidTypeExtensions(), ModFluidTypes.GHOST_TRAIN_EXTRACT_FLUID_TYPE.get());
        event.registerFluidType((ModFluidTypes.GRAPE_APE_EXTRACT_FLUID_TYPE.get()).getClientFluidTypeExtensions(), ModFluidTypes.GRAPE_APE_EXTRACT_FLUID_TYPE.get());
        event.registerFluidType((ModFluidTypes.COTTON_CANDY_EXTRACT_FLUID_TYPE.get()).getClientFluidTypeExtensions(), ModFluidTypes.COTTON_CANDY_EXTRACT_FLUID_TYPE.get());
        event.registerFluidType((ModFluidTypes.BANANA_KUSH_EXTRACT_FLUID_TYPE.get()).getClientFluidTypeExtensions(), ModFluidTypes.BANANA_KUSH_EXTRACT_FLUID_TYPE.get());
        event.registerFluidType((ModFluidTypes.CARBON_FIBER_EXTRACT_FLUID_TYPE.get()).getClientFluidTypeExtensions(), ModFluidTypes.CARBON_FIBER_EXTRACT_FLUID_TYPE.get());
        event.registerFluidType((ModFluidTypes.BIRTHDAY_CAKE_EXTRACT_FLUID_TYPE.get()).getClientFluidTypeExtensions(), ModFluidTypes.BIRTHDAY_CAKE_EXTRACT_FLUID_TYPE.get());
        event.registerFluidType((ModFluidTypes.BLUE_COOKIES_EXTRACT_FLUID_TYPE.get()).getClientFluidTypeExtensions(), ModFluidTypes.BLUE_COOKIES_EXTRACT_FLUID_TYPE.get());
        event.registerFluidType((ModFluidTypes.AFGHANI_EXTRACT_FLUID_TYPE.get()).getClientFluidTypeExtensions(), ModFluidTypes.AFGHANI_EXTRACT_FLUID_TYPE.get());
        event.registerFluidType((ModFluidTypes.MOONBOW_EXTRACT_FLUID_TYPE.get()).getClientFluidTypeExtensions(), ModFluidTypes.MOONBOW_EXTRACT_FLUID_TYPE.get());
        event.registerFluidType((ModFluidTypes.LAVA_CAKE_EXTRACT_FLUID_TYPE.get()).getClientFluidTypeExtensions(), ModFluidTypes.LAVA_CAKE_EXTRACT_FLUID_TYPE.get());
        event.registerFluidType((ModFluidTypes.JELLY_RANCHER_EXTRACT_FLUID_TYPE.get()).getClientFluidTypeExtensions(), ModFluidTypes.JELLY_RANCHER_EXTRACT_FLUID_TYPE.get());
        event.registerFluidType((ModFluidTypes.STRAWBERRY_SHORTCAKE_EXTRACT_FLUID_TYPE.get()).getClientFluidTypeExtensions(), ModFluidTypes.STRAWBERRY_SHORTCAKE_EXTRACT_FLUID_TYPE.get());
        event.registerFluidType((ModFluidTypes.PINK_KUSH_EXTRACT_FLUID_TYPE.get()).getClientFluidTypeExtensions(), ModFluidTypes.PINK_KUSH_EXTRACT_FLUID_TYPE.get());
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
    }

    @SubscribeEvent
    public static void onBlockColor(RegisterColorHandlersEvent.Block event) {
        BlockColor blockColor = (state, level, pos, tintIndex) -> {
            if (state == null || state.getBlock() != ModBlocks.UNIDENTIFIED_WEED_CROP.get()) {
                return 0xFFFFFFFF;
            }

            // tintIndex 0 => base layer, fixed green.
            if (tintIndex == 0) {
                return 0xFF99D335;
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

    @SubscribeEvent
    public static void onItemColor(RegisterColorHandlersEvent.Item event) {
        ItemColor itemColor = (stack, tintIndex) -> {
            // tintIndex 0 → base plant/leaf color from STRAIN_DATA.leafColor
            if (tintIndex == 0 && stack.has(ModDataComponentTypes.STRAIN_DATA.get())) {
                StrainData d = stack.get(ModDataComponentTypes.STRAIN_DATA.get());
                if (d != null) return d.leafColor();
            }
            // tintIndex 1 → strain color from STRAIN_DATA.colorArgb
            if (tintIndex == 1 && stack.has(ModDataComponentTypes.STRAIN_DATA.get())) {
                StrainData d = stack.get(ModDataComponentTypes.STRAIN_DATA.get());
                if (d != null) return d.colorArgb();
            }

            // Custom StrainData Color onto the Unidentified Seeds
            if (stack.getItem() instanceof UnidentifiedSeedsItem) {
                if (tintIndex == 0) {
                    // Base of seeds (pick from array)
                    String[] baseColors = new String[]{"99d335"};
                    int color = Integer.parseInt(baseColors[stack.getDamageValue() % baseColors.length], 16) | 0xFF000000;
                    return color;
                } else if (tintIndex == 1) {
                    // Tint by the StrainData color if present, to match the bud/weed it grows into.
                    StrainData d = stack.get(ModDataComponentTypes.STRAIN_DATA.get());
                    if (d != null) return d.colorArgb();
                }
            }

            if (stack.getItem() instanceof BaseBudItem) {
                Boolean isDry = stack.get(ModDataComponentTypes.DRY);
                if (Boolean.TRUE.equals(isDry)) {
                    return 0xFFD6CEC3;
                }
            }
            return 0xFFFFFFFF; // no tint
        };

        // Use the non-deprecated event.register(...)
        event.register(
                itemColor,
                ModItems.WHITE_WIDOW_BUD.get(),
                ModItems.BUBBLE_KUSH_BUD.get(),
                ModItems.LEMON_HAZE_BUD.get(),
                ModItems.SOUR_DIESEL_BUD.get(),
                ModItems.BLUE_ICE_BUD.get(),
                ModItems.BUBBLEGUM_BUD.get(),
                ModItems.PURPLE_HAZE_BUD.get(),
                ModItems.OG_KUSH_BUD.get(),
                ModItems.JACK_HERER_BUD.get(),
                ModItems.GARY_PEYTON_BUD.get(),
                ModItems.AMNESIA_HAZE_BUD.get(),
                ModItems.AK47_BUD.get(),
                ModItems.GHOST_TRAIN_BUD.get(),
                ModItems.GRAPE_APE_BUD.get(),
                ModItems.COTTON_CANDY_BUD.get(),
                ModItems.BANANA_KUSH_BUD.get(),
                ModItems.CARBON_FIBER_BUD.get(),
                ModItems.BIRTHDAY_CAKE_BUD.get(),
                ModItems.BLUE_COOKIES_BUD.get(),
                ModItems.AFGHANI_BUD.get(),
                ModItems.MOONBOW_BUD.get(),
                ModItems.LAVA_CAKE_BUD.get(),
                ModItems.JELLY_RANCHER_BUD.get(),
                ModItems.STRAWBERRY_SHORTCAKE_BUD.get(),
                ModItems.PINK_KUSH_BUD.get(),

                // Generic + custom strain carriers
                ModItems.GENERIC_SEEDS.get(),
                ModItems.GENERIC_BUD.get(),
                ModItems.GENERIC_WEED.get(),
                ModItems.GENERIC_EXTRACT.get(),
                ModItems.GENERIC_BAG.get(),
                ModItems.UNIDENTIFIED_SEEDS.get(),
                ModItems.UNIDENTIFIED_BUD.get(),
                ModItems.UNIDENTIFIED_WEED.get(),

                // Mixture bucket (mask tinted by strain)
                ModFluids.UNIDENTIFIED_MIXTURE_BUCKET.get()
        );
    }

}
