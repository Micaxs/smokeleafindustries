package net.micaxs.smokeleaf.network;

import net.micaxs.smokeleaf.SmokeleafIndustries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/**
 * Client → Server: sent when the player adjusts sliders or types a name in the Strain Modifier UI.
 */
public record StrainModifierUpdatePayload(
        int thc,
        int cbd,
        int leafR, int leafG, int leafB,
        int strainR, int strainG, int strainB,
        String name
) implements CustomPacketPayload {

    public static final Type<StrainModifierUpdatePayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(SmokeleafIndustries.MODID, "strain_modifier_update"));

    public static final StreamCodec<FriendlyByteBuf, StrainModifierUpdatePayload> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public StrainModifierUpdatePayload decode(FriendlyByteBuf buf) {
            return new StrainModifierUpdatePayload(
                    buf.readByte() & 0xFF,
                    buf.readByte() & 0xFF,
                    buf.readByte() & 0xFF,
                    buf.readByte() & 0xFF,
                    buf.readByte() & 0xFF,
                    buf.readByte() & 0xFF,
                    buf.readByte() & 0xFF,
                    buf.readByte() & 0xFF,
                    buf.readUtf(64)
            );
        }

        @Override
        public void encode(FriendlyByteBuf buf, StrainModifierUpdatePayload payload) {
            buf.writeByte(payload.thc());
            buf.writeByte(payload.cbd());
            buf.writeByte(payload.leafR());
            buf.writeByte(payload.leafG());
            buf.writeByte(payload.leafB());
            buf.writeByte(payload.strainR());
            buf.writeByte(payload.strainG());
            buf.writeByte(payload.strainB());
            buf.writeUtf(payload.name() == null ? "" : payload.name(), 64);
        }
    };

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
