package net.micaxs.smokeleaf.screen;

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
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModMenuTypes {

    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(BuiltInRegistries.MENU, SmokeleafIndustries.MODID);


    public static final DeferredHolder<MenuType<?>, MenuType<GeneratorMenu>> GENERATOR_MENU =
            registerMenuType("generator_menu", GeneratorMenu::new);

    public static final DeferredHolder<MenuType<?>, MenuType<GrinderMenu>> GRINDER_MENU =
            registerMenuType("grinder_menu", GrinderMenu::new);

    public static final DeferredHolder<MenuType<?>, MenuType<ExtractorMenu>> EXTRACTOR_MENU =
            registerMenuType("extractor_menu", ExtractorMenu::new);

    public static final DeferredHolder<MenuType<?>, MenuType<LiquifierMenu>> LIQUIFIER_MENU =
            registerMenuType("liquifier_menu", LiquifierMenu::new);

    public static final DeferredHolder<MenuType<?>, MenuType<MutatorMenu>> MUTATOR_MENU =
            registerMenuType("mutator_menu", MutatorMenu::new);

    public static final DeferredHolder<MenuType<?>, MenuType<SynthesizerMenu>> SYNTHESIZER_MENU =
            registerMenuType("synthesizer_menu", SynthesizerMenu::new);

    public static final DeferredHolder<MenuType<?>, MenuType<SequencerMenu>> SEQUENCER_MENU =
            registerMenuType("sequencer_menu", SequencerMenu::new);

    public static final DeferredHolder<MenuType<?>, MenuType<DryerMenu>> DRYER_MENU =
            registerMenuType("dryer_menu", DryerMenu::new);

    public static final DeferredHolder<MenuType<?>, MenuType<MixerMenu>> MIXER_MENU =
            registerMenuType("mixer_menu", MixerMenu::new);


    private static <T extends AbstractContainerMenu> DeferredHolder<MenuType<?>, MenuType<T>> registerMenuType(String name, IContainerFactory<T> factory) {
        return MENUS.register(name, () -> IMenuTypeExtension.create(factory));
    }

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}
