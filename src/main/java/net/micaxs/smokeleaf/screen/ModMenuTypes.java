package net.micaxs.smokeleaf.screen;

import net.minecraft.core.registries.Registries;

import net.micaxs.smokeleaf.SmokeleafIndustries;
import net.micaxs.smokeleaf.screen.custom.GeneratorMenu;
import net.micaxs.smokeleaf.screen.custom.GrinderMenu;
import net.micaxs.smokeleaf.screen.custom.ExtractorMenu;
import net.micaxs.smokeleaf.screen.custom.LiquifierMenu;
import net.micaxs.smokeleaf.screen.custom.MutatorMenu;
import net.micaxs.smokeleaf.screen.custom.SynthesizerMenu;
import net.micaxs.smokeleaf.screen.custom.SequencerMenu;
import net.micaxs.smokeleaf.screen.custom.DryerMenu;
import net.micaxs.smokeleaf.screen.custom.MixerMenu;
import net.micaxs.smokeleaf.screen.custom.StrainModifierMenu;
import net.micaxs.smokeleaf.screen.custom.GummyMachineMenu;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
public class ModMenuTypes {

    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, SmokeleafIndustries.MODID);


    public static final RegistryObject<MenuType<GeneratorMenu>> GENERATOR_MENU =
            registerMenuType("generator_menu", GeneratorMenu::new);

    public static final RegistryObject<MenuType<GrinderMenu>> GRINDER_MENU =
            registerMenuType("grinder_menu", GrinderMenu::new);

    public static final RegistryObject<MenuType<ExtractorMenu>> EXTRACTOR_MENU =
            registerMenuType("extractor_menu", ExtractorMenu::new);

    public static final RegistryObject<MenuType<LiquifierMenu>> LIQUIFIER_MENU =
            registerMenuType("liquifier_menu", LiquifierMenu::new);

    public static final RegistryObject<MenuType<MutatorMenu>> MUTATOR_MENU =
            registerMenuType("mutator_menu", MutatorMenu::new);

    public static final RegistryObject<MenuType<SynthesizerMenu>> SYNTHESIZER_MENU =
            registerMenuType("synthesizer_menu", SynthesizerMenu::new);

    public static final RegistryObject<MenuType<SequencerMenu>> SEQUENCER_MENU =
            registerMenuType("sequencer_menu", SequencerMenu::new);

    public static final RegistryObject<MenuType<DryerMenu>> DRYER_MENU =
            registerMenuType("dryer_menu", DryerMenu::new);

    public static final RegistryObject<MenuType<MixerMenu>> MIXER_MENU =
            registerMenuType("mixer_menu", MixerMenu::new);

    public static final RegistryObject<MenuType<StrainModifierMenu>> STRAIN_MODIFIER_MENU =
            registerMenuType("strain_modifier_menu", StrainModifierMenu::new);

    public static final RegistryObject<MenuType<GummyMachineMenu>> GUMMY_MACHINE_MENU =
            registerMenuType("gummy_machine_menu", GummyMachineMenu::new);


    private static <T extends AbstractContainerMenu> RegistryObject<MenuType<T>> registerMenuType(String name, IContainerFactory<T> factory) {
        return MENUS.register(name, () -> IForgeMenuType.create(factory));
    }

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}
