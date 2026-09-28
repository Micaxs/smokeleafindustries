package net.micaxs.smokeleaf.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record ParanoiaHallucinationPayload(
        ResourceLocation entityTypeId,
        double x, double y, double z,
        float yaw,
        int lifeTicks,
        ResourceLocation soundIdOrNull
) {

    public static void encode(ParanoiaHallucinationPayload msg, FriendlyByteBuf buf) {
        buf.writeResourceLocation(msg.entityTypeId());
        buf.writeDouble(msg.x());
        buf.writeDouble(msg.y());
        buf.writeDouble(msg.z());
        buf.writeFloat(msg.yaw());
        buf.writeVarInt(msg.lifeTicks());
        buf.writeNullable(msg.soundIdOrNull(), FriendlyByteBuf::writeResourceLocation);
    }

    public static ParanoiaHallucinationPayload decode(FriendlyByteBuf buf) {
        ResourceLocation typeId = buf.readResourceLocation();
        double x = buf.readDouble();
        double y = buf.readDouble();
        double z = buf.readDouble();
        float yaw = buf.readFloat();
        int life = buf.readVarInt();
        ResourceLocation sound = buf.readNullable(FriendlyByteBuf::readResourceLocation);
        return new ParanoiaHallucinationPayload(typeId, x, y, z, yaw, life, sound);
    }
}
