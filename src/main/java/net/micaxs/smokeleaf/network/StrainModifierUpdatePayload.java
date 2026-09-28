package net.micaxs.smokeleaf.network;

import net.minecraft.network.FriendlyByteBuf;

/**
 * Client → Server: sent when the player adjusts sliders or types a name in the Strain Modifier UI.
 */
public record StrainModifierUpdatePayload(
        int thc,
        int cbd,
        int leafR, int leafG, int leafB,
        int strainR, int strainG, int strainB,
        String name
) {

    public static StrainModifierUpdatePayload decode(FriendlyByteBuf buf) {
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

    public static void encode(StrainModifierUpdatePayload payload, FriendlyByteBuf buf) {
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
}
