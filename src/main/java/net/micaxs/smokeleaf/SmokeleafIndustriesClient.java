package net.micaxs.smokeleaf;

import net.micaxs.smokeleaf.block.ModBlocks;
import net.micaxs.smokeleaf.block.entity.ModBlockEntities;
import net.micaxs.smokeleaf.block.entity.client.DryingRackRenderer;
import net.micaxs.smokeleaf.block.entity.render.GrowPotRenderer;
import net.micaxs.smokeleaf.client.brainmelt.BrainMeltInputHandler;
import net.micaxs.smokeleaf.client.guide.GuideDataLoader;
import net.micaxs.smokeleaf.effect.ModEffects;
import net.micaxs.smokeleaf.fluid.ModFluidTypes;
import net.micaxs.smokeleaf.fluid.ModFluids;
import net.micaxs.smokeleaf.screen.ModMenuTypes;
import net.micaxs.smokeleaf.screen.custom.*;
import net.minecraft.client.Minecraft;
import net.micaxs.smokeleaf.client.color.DnaFullProperty;
import net.micaxs.smokeleaf.client.color.StrainTintSource;
import net.micaxs.smokeleaf.client.color.UnidentifiedCropTintSource;
import net.micaxs.smokeleaf.client.model.PipeBlockStateModel;
import net.micaxs.smokeleaf.client.render.RedEyesLayer;
import net.micaxs.smokeleaf.event.ClientEvents;
import net.minecraft.client.entity.ClientAvatarEntity;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.world.entity.Avatar;
import net.neoforged.neoforge.client.renderstate.AvatarRenderStateModifier;
import net.neoforged.neoforge.client.renderstate.RegisterRenderStateModifiersEvent;
import net.micaxs.smokeleaf.fluid.TintedFluidType;
import net.minecraft.client.Camera;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.client.renderer.fog.environment.FogEnvironment;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.world.entity.player.PlayerModelType;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.fluid.FluidTintSource;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.joml.Vector4f;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
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
import java.util.List;
import java.util.Map;
import java.util.Set;

@Mod(value = SmokeleafIndustries.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = SmokeleafIndustries.MODID, value = Dist.CLIENT)
public class SmokeleafIndustriesClient {

    private static final Set<Identifier> WIGGLED = new HashSet<>();

    private record WiggleSpec(double sx, double ax, double axLvl, boolean ix,
                              double sy, double ay, double ayLvl, boolean iy) {}

    private static final Map<Identifier, WiggleSpec> WIGGLE_SPECS = new HashMap<>();
    static {
        WIGGLE_SPECS.put(VanillaGuiLayers.HOTBAR,             new WiggleSpec(0.10, 3.0, 1.5, false, 0.18, 1.4, 0.6, false));
        WIGGLE_SPECS.put(VanillaGuiLayers.PLAYER_HEALTH,      new WiggleSpec(0.13, 3.0, 1.5, true,  0.22, 1.8, 0.7, false));
        WIGGLE_SPECS.put(VanillaGuiLayers.FOOD_LEVEL,         new WiggleSpec(0.17, 2.5, 1.2, false, 0.25, 1.2, 0.5, true));
        WIGGLE_SPECS.put(VanillaGuiLayers.CHAT,               new WiggleSpec(0.21, 4.0, 1.0, false, 0.07, 6.0, 1.5, false));
        WIGGLE_SPECS.put(VanillaGuiLayers.TAB_LIST,           new WiggleSpec(0.15, 5.0, 2.0, true,  0.11, 3.5, 1.2, true));
        WIGGLE_SPECS.put(VanillaGuiLayers.CROSSHAIR,          new WiggleSpec(0.11, 2.0, 1.0, false, 0.19, 2.2, 0.8, false));
        WIGGLE_SPECS.put(VanillaGuiLayers.EFFECTS,            new WiggleSpec(0.19, 2.0, 1.0, false, 0.16, 1.6, 0.6, false));
        WIGGLE_SPECS.put(VanillaGuiLayers.CONTEXTUAL_INFO_BAR,     new WiggleSpec(0.19, 2.0, 1.0, false, 0.27, 1.0, 0.4, true));
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
        // Render layers are picked from texture transparency since 1.21.5 (fluids, the reflector),
        // and item model properties are declared in the item model JSONs (see the conditional
        // property registered below), so there's nothing left to set up here.
    }

    @SubscribeEvent
    public static void registerBER(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.DRYING_RACK_BE.get(), DryingRackRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.GROW_POT.get(), GrowPotRenderer::new);

    }

    @SubscribeEvent
    public static void onRegisterBlockStateModels(RegisterBlockStateModels event) {
        event.registerModel(Identifier.fromNamespaceAndPath(SmokeleafIndustries.MODID, "pipe"), PipeBlockStateModel.Unbaked.MAP_CODEC);
    }

    @SubscribeEvent
    public static void onRegisterReloadListeners(AddClientReloadListenersEvent event) {
        event.addListener(Identifier.fromNamespaceAndPath(SmokeleafIndustries.MODID, "guide"), new GuideDataLoader());
    }

    @SubscribeEvent
    public static void onAddLayers(EntityRenderersEvent.AddLayers event) {
        for (PlayerModelType skin : event.getSkins()) {
            AvatarRenderer<AbstractClientPlayer> renderer = event.getPlayerRenderer(skin);
            if (renderer != null) {
                renderer.addLayer(new RedEyesLayer(renderer));
            }
        }
    }

    @SubscribeEvent
    public static void onRenderStateModifiers(RegisterRenderStateModifiersEvent event) {
        // RedEyesLayer only sees the render state, so flag stoned players on it.
        event.registerAvatarEntityModifier(new AvatarRenderStateModifier() {
            @Override
            public <T extends Avatar & ClientAvatarEntity> void accept(T avatar, AvatarRenderState renderState) {
                renderState.setRenderData(RedEyesLayer.STONED, avatar.hasEffect(ModEffects.STONED));
                renderState.setRenderData(ClientEvents.ZOMBIFIED_PLAYER,
                        avatar instanceof Player player && player.hasEffect(ModEffects.ZOMBIFIED) ? player : null);
            }
        });
    }

    @SubscribeEvent
    public static void onRegisterFluidModels(RegisterFluidModelsEvent event) {
        registerFluidModel(event, ModFluidTypes.HASH_OIL_FLUID_TYPE.get(), ModFluids.SOURCE_HASH_OIL_FLUID.get(), ModFluids.FLOWING_HASH_OIL_FLUID.get());
        registerFluidModel(event, ModFluidTypes.HASH_OIL_SLUDGE_FLUID_TYPE.get(), ModFluids.SOURCE_HASH_OIL_SLUDGE_FLUID.get(), ModFluids.FLOWING_HASH_OIL_SLUDGE_FLUID.get());
        registerFluidModel(event, ModFluidTypes.UNIDENTIFIED_MIXTURE_FLUID_TYPE.get(), ModFluids.SOURCE_UNIDENTIFIED_MIXTURE_FLUID.get(), ModFluids.FLOWING_UNIDENTIFIED_MIXTURE_FLUID.get());
    }

    private static void registerFluidModel(RegisterFluidModelsEvent event, FluidType type, Fluid still, Fluid flowing) {
        TintedFluidType tinted = (TintedFluidType) type;
        FluidTintSource tint = new FluidTintSource() {
            @Override
            public int color(FluidState state) {
                return tinted.getTintColor();
            }

            @Override
            public int colorAsStack(FluidStack stack) {
                return tinted.getTintColor(stack);
            }
        };
        Identifier overlay = tinted.getOverlayTexture();
        event.register(new FluidModel.Unbaked(
                new Material(tinted.getStillTexture()),
                new Material(tinted.getFlowingTexture()),
                overlay != null ? new Material(overlay) : null,
                tint), still, flowing);
    }

    @SubscribeEvent
    public static void onClientExtensions(RegisterClientExtensionsEvent event) {
        registerFluidFog(event, ModFluidTypes.HASH_OIL_FLUID_TYPE.get());
        registerFluidFog(event, ModFluidTypes.HASH_OIL_SLUDGE_FLUID_TYPE.get());
        registerFluidFog(event, ModFluidTypes.UNIDENTIFIED_MIXTURE_FLUID_TYPE.get());
    }

    /** Thick, colored fog when the camera is inside one of our oils. */
    private static void registerFluidFog(RegisterClientExtensionsEvent event, FluidType type) {
        TintedFluidType tinted = (TintedFluidType) type;
        event.registerFluidType(new IClientFluidTypeExtensions() {
            @Override
            public void modifyFogColor(Camera camera, float partialTick, ClientLevel level, int renderDistance, float darkenWorldAmount, Vector4f fluidFogColor) {
                Vector3f fog = tinted.getFogColor();
                fluidFogColor.set(fog.x(), fog.y(), fog.z(), fluidFogColor.w());
            }

            @Override
            public void modifyFogRender(Camera camera, @Nullable FogEnvironment environment, float renderDistance, float partialTick, FogData fogData) {
                fogData.environmentalStart = 1f;
                fogData.environmentalEnd = 6f; // distance when the fog starts
            }
        }, type);
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

        Identifier id = event.getName();
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

        GuiGraphicsExtractor g = event.getGuiGraphics();
        g.pose().pushMatrix();
        g.pose().translate((float) xOffset, (float) yOffset);
        WIGGLED.add(id);
    }

    @SubscribeEvent
    public static void onRenderGuiLayerPost(RenderGuiLayerEvent.Post event) {
        if (!WIGGLED.remove(event.getName())) return;
        event.getGuiGraphics().pose().popMatrix();
    }

    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null) return;
        if (!player.hasEffect(ModEffects.RAINBOW)) return;

        GuiGraphicsExtractor gg = event.getGuiGraphics();
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
            event.setFOV((float) (event.getFOV() * (1.0 + wave)));
        }

        if (bubbled != null) {
            int amp = bubbled.getAmplifier();
            double boost = -0.50 + amp * 0.12;
            double modified = event.getFOV() * (1.0 + boost);
            event.setFOV((float) Math.min(170.0, modified));
        }

        // Stoned: very slow, deep FOV pulse — like a long relaxed inhale (~4.8s cycle).
        // Gated the same way the trip shader is: only once the streak requirement is met.
        MobEffectInstance stoned = player.getEffect(ModEffects.STONED);
        if (stoned != null && stoned.isVisible()) {
            float time = (float)(player.tickCount + p);
            int amp = stoned.getAmplifier();
            float amplitude = 0.009f + amp * 0.004f;
            float wave = (float)Math.sin(time * 0.065f) * amplitude;
            event.setFOV((float) (event.getFOV() * (1.0 + wave)));
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
    public static void onBlockTints(RegisterColorHandlersEvent.BlockTintSources event) {
        // tint index 0 => base leaf layer, 1 => strain color mask (see UnidentifiedCropTintSource)
        event.register(List.of(new UnidentifiedCropTintSource(false), new UnidentifiedCropTintSource(true)),
                ModBlocks.UNIDENTIFIED_WEED_CROP.get());
    }

    @SubscribeEvent
    public static void onItemTints(RegisterColorHandlersEvent.ItemTintSources event) {
        // Per-layer strain colors are declared in each item model JSON (see ModModelProvider).
        event.register(Identifier.fromNamespaceAndPath(SmokeleafIndustries.MODID, "strain"), StrainTintSource.MAP_CODEC);
    }

    @SubscribeEvent
    public static void onConditionalItemProperties(RegisterConditionalItemModelPropertyEvent event) {
        event.register(Identifier.fromNamespaceAndPath(SmokeleafIndustries.MODID, "dna_full"), DnaFullProperty.MAP_CODEC);
    }
}
