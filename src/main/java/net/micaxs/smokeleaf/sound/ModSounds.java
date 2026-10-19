package net.micaxs.smokeleaf.sound;

import net.micaxs.smokeleaf.SmokeleafIndustries;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, SmokeleafIndustries.MODID);


    public static final Supplier<SoundEvent> BONG_HIT = registerSoundEvents("bonghit");
    public static final Supplier<SoundEvent> MANUAL_GRINDER = registerSoundEvents("manual_grinder");
    public static final Supplier<SoundEvent> LAMP_BREAK = registerSoundEvents("lamp_break");



    private static Supplier<SoundEvent> registerSoundEvents(String name) {
        Identifier id = Identifier.fromNamespaceAndPath(SmokeleafIndustries.MODID, name);
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(id));
    }

    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }

}
