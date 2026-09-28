// Java
package net.micaxs.smokeleaf.client;

import net.micaxs.smokeleaf.SmokeleafIndustries;
import net.micaxs.smokeleaf.client.particle.EchoLocationParticle;
import net.micaxs.smokeleaf.effect.ModParticles;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
@Mod.EventBusSubscriber(modid = SmokeleafIndustries.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModParticleFactories {

    @SubscribeEvent
    public static void registerFactories(RegisterParticleProvidersEvent evt) {
        evt.registerSpriteSet(ModParticles.ECHO_LOCATION_PARTICLE.get(), EchoLocationParticle.Provider::new);
    }
}
