package net.micaxs.smokeleaf.effect;

import net.minecraftforge.registries.RegistryObject;

import net.minecraft.core.registries.Registries;

import net.micaxs.smokeleaf.SmokeleafIndustries;
import net.micaxs.smokeleaf.effect.beneficial.*;
import net.micaxs.smokeleaf.effect.harmful.*;
import net.micaxs.smokeleaf.effect.neutral.*;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
public class ModEffects {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, SmokeleafIndustries.MODID);


    public static final RegistryObject<MobEffect> STONED = MOB_EFFECTS.register("stoned",
            () -> new StonedEffect(MobEffectCategory.NEUTRAL, 31458724));
    public static final RegistryObject<MobEffect> SCRAMBLED_MOUTH = MOB_EFFECTS.register("scrambled_mouth",
            () -> new ScrambledMouthEffect(MobEffectCategory.HARMFUL, 0x9EDB4C));


    public static final RegistryObject<MobEffect> SLEEPY = MOB_EFFECTS.register("sleepy",
            () -> new SleepyEffect(MobEffectCategory.BENEFICIAL, 31458724));
    public static final RegistryObject<MobEffect> RELAXED = MOB_EFFECTS.register("relaxed",
            () -> new RelaxedEffect(MobEffectCategory.BENEFICIAL, 31458724));
    public static final RegistryObject<MobEffect> EUPHORIC = MOB_EFFECTS.register("euphoric",
            () -> new EuphoricEffect(MobEffectCategory.BENEFICIAL, 31458724));
    public static final RegistryObject<MobEffect> UPLIFTED = MOB_EFFECTS.register("uplifted",
            () -> new UpliftedEffect(MobEffectCategory.BENEFICIAL, 31458724));
    public static final RegistryObject<MobEffect> RAINBOW = MOB_EFFECTS.register("rainbow",
            () -> new CreativeEffect(MobEffectCategory.BENEFICIAL, 31458724));
    public static final RegistryObject<MobEffect> BREATHING = MOB_EFFECTS.register("breathing",
            () -> new BreathingEffect(MobEffectCategory.BENEFICIAL, 31458724));
    public static final RegistryObject<MobEffect> BUBBLED = MOB_EFFECTS.register("bubbled",
            () -> new BubbledEffect(MobEffectCategory.BENEFICIAL, 31458724));
    public static final RegistryObject<MobEffect> MELTED = MOB_EFFECTS.register("melted",
            () -> new MeltedEffect(MobEffectCategory.BENEFICIAL, 31458724));
    public static final RegistryObject<MobEffect> AROUSED = MOB_EFFECTS.register("aroused",
            () -> new ArousedEffect(MobEffectCategory.BENEFICIAL, 31458724));
    public static final RegistryObject<MobEffect> SHY = MOB_EFFECTS.register("shy",
            () -> new ShyEffect(MobEffectCategory.BENEFICIAL, 31458724)); // Invisibility
    public static final RegistryObject<MobEffect> HIGH_FLYER = MOB_EFFECTS.register("high_flyer",
            () -> new HighFlyerEffect(MobEffectCategory.BENEFICIAL, 31458724)); // Invisibility


    public static final RegistryObject<MobEffect> DRY_MOUTH = MOB_EFFECTS.register("dry_mouth",
            () -> new HeadSpinEffect(MobEffectCategory.HARMFUL, 31458724));
    public static final RegistryObject<MobEffect> DIZZY = MOB_EFFECTS.register("dizzy",
            () -> new HeadSpinEffect(MobEffectCategory.HARMFUL, 31458724));
    public static final RegistryObject<MobEffect> PARANOIA = MOB_EFFECTS.register("paranoia",
            () -> new ParanoiaEffect(MobEffectCategory.HARMFUL, 31458724));
    public static final RegistryObject<MobEffect> DRY_EYES = MOB_EFFECTS.register("dry_eyes",
            () -> new RedEyesEffect(MobEffectCategory.HARMFUL, 31458724));
    public static final RegistryObject<MobEffect> BRAIN_MELT = MOB_EFFECTS.register("brain_melt",
            () -> new BrainMeltEffect(MobEffectCategory.HARMFUL, 31458724));


    public static final RegistryObject<MobEffect> CHILLOUT = MOB_EFFECTS.register("chillout",
            () -> new ChilloutEffect(MobEffectCategory.BENEFICIAL, 31458724));
    public static final RegistryObject<MobEffect> ZOMBIFIED = MOB_EFFECTS.register("zombified",
            () -> new ZombifiedEffect(MobEffectCategory.HARMFUL, 31458724));
    public static final RegistryObject<MobEffect> ECHO_LOCATION = MOB_EFFECTS.register("echo_location",
            () -> new EchoLocationEffect(MobEffectCategory.BENEFICIAL, 31458724));
    public static final RegistryObject<MobEffect> FRIEND_OR_FOE = MOB_EFFECTS.register("friend_or_foe",
            () -> new FriendOrFoeEffect(MobEffectCategory.NEUTRAL, 31458724));
    public static final RegistryObject<MobEffect> LINGUISTS_HIGH = MOB_EFFECTS.register("linguists_high",
            () -> new LinguistsHighEffect(MobEffectCategory.BENEFICIAL, 31458724));
    public static final RegistryObject<MobEffect> STICKY_ICKY = MOB_EFFECTS.register("sticky_icky",
            () -> new StickyIckyEffect(MobEffectCategory.BENEFICIAL, 31458724));
    public static final RegistryObject<MobEffect> R_TREES = MOB_EFFECTS.register("r_trees",
            () -> new RTreesEffect(MobEffectCategory.BENEFICIAL, 31458724));
    public static final RegistryObject<MobEffect> VEIN_HIGH = MOB_EFFECTS.register("vein_high",
            () -> new VeinHighEffect(MobEffectCategory.BENEFICIAL, 31458724));


    public static void register(IEventBus eventBus) {
        MOB_EFFECTS.register(eventBus);
    }
}