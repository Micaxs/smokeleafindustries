package net.micaxs.smokeleaf.effect;

import net.micaxs.smokeleaf.SmokeleafIndustries;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
public class ModParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLES =
            DeferredRegister.create(Registries.PARTICLE_TYPE, SmokeleafIndustries.MODID);

    public static final RegistryObject<SimpleParticleType> DRY_BUD_SPARK =
            PARTICLES.register("dry_bud_spark", () -> new SimpleParticleType(true));

    public static final RegistryObject<SimpleParticleType> ECHO_LOCATION_PARTICLE =
            PARTICLES.register("echo_location", () -> new SimpleParticleType(true));

    // Use SimpleParticleType for the first approach, or keep as is for DustParticle approach
    public static final RegistryObject<SimpleParticleType> XRAY_PARTICLE =
            PARTICLES.register("xray_particle", () -> new SimpleParticleType(true));

    public static void register(IEventBus bus) {
        PARTICLES.register(bus);
    }
}