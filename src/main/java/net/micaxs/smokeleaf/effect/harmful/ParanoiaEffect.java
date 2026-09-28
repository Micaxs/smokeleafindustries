// Java
package net.micaxs.smokeleaf.effect.harmful;

import net.micaxs.smokeleaf.network.ParanoiaHallucinationPayload;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.micaxs.smokeleaf.network.ModNetwork;
import java.util.List;

public class ParanoiaEffect extends MobEffect {
    private static final RandomSource RNG = RandomSource.createThreadSafe();
    private static final List<EntityType<?>> SPOOKS = List.of(
            EntityType.CREEPER, EntityType.ZOMBIE, EntityType.BLAZE, EntityType.CAT,
            EntityType.SKELETON, EntityType.SPIDER, EntityType.ENDERMAN
    );
    private static final List<ResourceLocation> SOUNDS = List.of(
            new ResourceLocation("entity.creeper.primed"),
            new ResourceLocation("entity.phantom.ambient"),
            new ResourceLocation("entity.spider.ambient"),
            new ResourceLocation("entity.enderman.stare"),
            new ResourceLocation("entity.cat.ambient"),
            new ResourceLocation("entity.blaze.ambient"),
            new ResourceLocation("entity.cow.ambient")
    );

    public ParanoiaEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public void applyEffectTick(LivingEntity living, int amplifier) {
        if (!living.level().isClientSide() && living instanceof ServerPlayer sp) {
            // Try roughly every 3s reduced by amplifier, with variability
            int base = Math.max(40, 100 - amplifier * 20); // ticks between tries
            if (RNG.nextInt(base) == 0) {
                // Pick a mob type and a location around the player
                EntityType<?> type = SPOOKS.get(RNG.nextInt(SPOOKS.size()));
                double radius = 3.0 + RNG.nextDouble() * 6.0;
                double angle = RNG.nextDouble() * Math.PI * 2.0;
                Vec3 eye = sp.getEyePosition();
                double x = sp.getX() + Math.cos(angle) * radius;
                double z = sp.getZ() + Math.sin(angle) * radius;
                double y = eye.y - 1.0 + RNG.nextDouble() * 2.5;

                // Make the illusion look at the player
                float yawToPlayer = (float)(Math.toDegrees(Math.atan2(sp.getZ() - z, sp.getX() - x)) - 90.0);
                int life = 40 + RNG.nextInt(60); // 2-5s

                // 2/3 chance to include a sound cue
                ResourceLocation sound = RNG.nextInt(3) != 0 ? SOUNDS.get(RNG.nextInt(SOUNDS.size())) : null;

                ParanoiaHallucinationPayload payload = new ParanoiaHallucinationPayload(
                        BuiltInRegistries.ENTITY_TYPE.getKey(type),
                        x, y, z,
                        yawToPlayer,
                        life,
                        sound
                );
                ModNetwork.sendToPlayer(sp, payload);
            }
        }
        super.applyEffectTick(living, amplifier);
        return;
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }
}