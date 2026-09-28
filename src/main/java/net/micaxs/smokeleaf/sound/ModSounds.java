package net.micaxs.smokeleaf.sound;

import net.minecraft.core.registries.Registries;

import net.micaxs.smokeleaf.SmokeleafIndustries;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import java.util.function.Supplier;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(Registries.SOUND_EVENT, SmokeleafIndustries.MODID);


    public static final Supplier<SoundEvent> BONG_HIT = registerSoundEvents("bonghit");
    public static final Supplier<SoundEvent> MANUAL_GRINDER = registerSoundEvents("manual_grinder");
    public static final Supplier<SoundEvent> LAMP_BREAK = registerSoundEvents("lamp_break");



    private static Supplier<SoundEvent> registerSoundEvents(String name) {
        ResourceLocation id = new ResourceLocation(SmokeleafIndustries.MODID, name);
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(id));
    }

    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }

}
