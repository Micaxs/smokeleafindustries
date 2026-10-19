// Java
package net.micaxs.smokeleaf.network;
import net.micaxs.smokeleaf.SmokeleafIndustries;
import net.micaxs.smokeleaf.client.paranoia.HallucinationManager;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Optional;

public record ParanoiaHallucinationPayload(
        Identifier entityTypeId,
        double x, double y, double z,
        float yaw,
        int lifeTicks,
        Identifier soundIdOrNull
) implements CustomPacketPayload {

    public static final Type<ParanoiaHallucinationPayload> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath(SmokeleafIndustries.MODID, "paranoia_hallucination"));

    // Codec using FriendlyByteBuf everywhere; write/read fields manually to avoid composite overload limits
    public static final StreamCodec<FriendlyByteBuf, ParanoiaHallucinationPayload> STREAM_CODEC =
            StreamCodec.of(
                    (buf, msg) -> {
                        Identifier.STREAM_CODEC.encode(buf, msg.entityTypeId());
                        buf.writeDouble(msg.x());
                        buf.writeDouble(msg.y());
                        buf.writeDouble(msg.z());
                        buf.writeFloat(msg.yaw());
                        buf.writeVarInt(msg.lifeTicks());
                        ByteBufCodecs.optional(Identifier.STREAM_CODEC)
                                .encode(buf, Optional.ofNullable(msg.soundIdOrNull()));
                    },
                    buf -> {
                        Identifier typeId = Identifier.STREAM_CODEC.decode(buf);
                        double x = buf.readDouble();
                        double y = buf.readDouble();
                        double z = buf.readDouble();
                        float yaw = buf.readFloat();
                        int life = buf.readVarInt();
                        Identifier sound = ByteBufCodecs
                                .optional(Identifier.STREAM_CODEC)
                                .decode(buf).orElse(null);
                        return new ParanoiaHallucinationPayload(typeId, x, y, z, yaw, life, sound);
                    }
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handleClient(ParanoiaHallucinationPayload msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            var level = net.minecraft.client.Minecraft.getInstance().level;
            if (level == null) return;

            var type = BuiltInRegistries.ENTITY_TYPE.getOptional(msg.entityTypeId()).orElse(null);
            if (type == null) return;

            var sound = msg.soundIdOrNull() == null ? null
                    : BuiltInRegistries.SOUND_EVENT.getOptional(msg.soundIdOrNull()).orElse(null);

            HallucinationManager.spawn(type, msg.x(), msg.y(), msg.z(), msg.yaw(), msg.lifeTicks(), sound);
        });
    }
}