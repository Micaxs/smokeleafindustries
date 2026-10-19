package net.micaxs.smokeleaf.network;

import io.netty.buffer.ByteBuf;
import net.micaxs.smokeleaf.SmokeleafIndustries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record GigglyFitPayload(boolean jump) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<GigglyFitPayload> TYPE =
            new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(SmokeleafIndustries.MODID, "giggly_fit"));

    public static final StreamCodec<ByteBuf, GigglyFitPayload> STREAM_CODEC =
            StreamCodec.composite(ByteBufCodecs.BOOL, GigglyFitPayload::jump, GigglyFitPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }


}