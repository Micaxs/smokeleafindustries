package net.micaxs.smokeleaf.network;

import net.micaxs.smokeleaf.SmokeleafIndustries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record StrainBookDataPayload(
        Map<String, String> myStrains,
        Map<String, ServerStrain> globalDiscoveries
) implements CustomPacketPayload {

    public record ServerStrain(String displayName, List<String> discoverers) {}

    public static final Type<StrainBookDataPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(SmokeleafIndustries.MODID, "strain_book_data"));

    public static final StreamCodec<FriendlyByteBuf, StrainBookDataPayload> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public StrainBookDataPayload decode(FriendlyByteBuf buf) {
            int mySize = buf.readVarInt();
            Map<String, String> myStrains = new HashMap<>();
            for (int i = 0; i < mySize; i++) {
                myStrains.put(buf.readUtf(), buf.readUtf());
            }
            int globalSize = buf.readVarInt();
            Map<String, ServerStrain> global = new HashMap<>();
            for (int i = 0; i < globalSize; i++) {
                String strainId = buf.readUtf();
                String displayName = buf.readUtf();
                List<String> discoverers = buf.readList(FriendlyByteBuf::readUtf);
                global.put(strainId, new ServerStrain(displayName, discoverers));
            }
            return new StrainBookDataPayload(myStrains, global);
        }

        @Override
        public void encode(FriendlyByteBuf buf, StrainBookDataPayload payload) {
            buf.writeVarInt(payload.myStrains.size());
            for (var entry : payload.myStrains.entrySet()) {
                buf.writeUtf(entry.getKey());
                buf.writeUtf(entry.getValue());
            }
            buf.writeVarInt(payload.globalDiscoveries.size());
            for (var entry : payload.globalDiscoveries.entrySet()) {
                buf.writeUtf(entry.getKey());
                buf.writeUtf(entry.getValue().displayName());
                buf.writeCollection(entry.getValue().discoverers(), FriendlyByteBuf::writeUtf);
            }
        }
    };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
