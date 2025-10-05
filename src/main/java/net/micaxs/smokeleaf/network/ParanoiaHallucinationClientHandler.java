package net.micaxs.smokeleaf.network;

import net.micaxs.smokeleaf.client.paranoia.HallucinationManager;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ParanoiaHallucinationClientHandler {
    public static void handle(ParanoiaHallucinationPayload payload, IPayloadContext context) {
        Minecraft mc = Minecraft.getInstance();
        mc.execute(() -> {
            if (mc.level == null) return;

            EntityType<?> type = BuiltInRegistries.ENTITY_TYPE
                    .getOptional(payload.entityTypeId())
                    .orElse(null);
            if (type == null) return;

            SoundEvent sound = payload.soundIdOrNull() == null ? null
                    : BuiltInRegistries.SOUND_EVENT.getOptional(payload.soundIdOrNull()).orElse(null);

            HallucinationManager.spawn(type, payload.x(), payload.y(), payload.z(),
                    payload.yaw(), payload.lifeTicks(), sound);
        });
    }
}