package net.micaxs.smokeleaf.network;

import io.netty.buffer.ByteBuf;
import net.micaxs.smokeleaf.SmokeleafIndustries;
import net.micaxs.smokeleaf.strain.StrainData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

/**
 * Server -&gt; client payload for the Strain Data Pad screen. Carries two lists of entries:
 * {@code personal} (strains this player has picked up at least once) and {@code server}
 * (every strain any player has registered/named server-wide).
 */
public record StrainDataPadPayload(
        List<Entry> personal,
        List<Entry> server
) implements CustomPacketPayload {

    /**
     * @param strainId    the registry key
     * @param data        full strain stats/appearance, used to build preview item stacks
     * @param creatorName who originally discovered/named this strain; blank if unknown or preset
     */
    public record Entry(String strainId, StrainData data, String creatorName) {}

    public static final Type<StrainDataPadPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(SmokeleafIndustries.MODID, "strain_data_pad"));

    private static final StreamCodec<ByteBuf, StrainData> STRAIN_DATA_STREAM_CODEC =
            ByteBufCodecs.fromCodec(StrainData.CODEC);

    private static void encodeEntry(FriendlyByteBuf buf, Entry entry) {
        buf.writeUtf(entry.strainId());
        STRAIN_DATA_STREAM_CODEC.encode(buf, entry.data());
        buf.writeUtf(entry.creatorName());
    }

    private static Entry decodeEntry(FriendlyByteBuf buf) {
        String strainId = buf.readUtf();
        StrainData data = STRAIN_DATA_STREAM_CODEC.decode(buf);
        String creatorName = buf.readUtf();
        return new Entry(strainId, data, creatorName);
    }

    public static final StreamCodec<FriendlyByteBuf, StrainDataPadPayload> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public StrainDataPadPayload decode(FriendlyByteBuf buf) {
            int personalSize = buf.readVarInt();
            List<Entry> personal = new ArrayList<>(personalSize);
            for (int i = 0; i < personalSize; i++) personal.add(decodeEntry(buf));

            int serverSize = buf.readVarInt();
            List<Entry> server = new ArrayList<>(serverSize);
            for (int i = 0; i < serverSize; i++) server.add(decodeEntry(buf));

            return new StrainDataPadPayload(personal, server);
        }

        @Override
        public void encode(FriendlyByteBuf buf, StrainDataPadPayload payload) {
            buf.writeVarInt(payload.personal.size());
            for (Entry e : payload.personal) encodeEntry(buf, e);

            buf.writeVarInt(payload.server.size());
            for (Entry e : payload.server) encodeEntry(buf, e);
        }
    };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
