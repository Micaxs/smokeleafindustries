package net.micaxs.smokeleaf.network;

import net.micaxs.smokeleaf.SmokeleafIndustries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Optional;
import java.util.function.Supplier;

public class ModNetwork {
    private static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(SmokeleafIndustries.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static boolean registered = false;

    public static void register() {
        if (registered) return;
        registered = true;
        int id = 0;

        // Paranoia Hallucination -> client
        // NOTE: the client handler is resolved through DistExecutor (not a bare method reference)
        // so that ParanoiaHallucinationClientHandler and its client-only imports never load on
        // the dedicated server, which would crash with "invalid dist DEDICATED_SERVER".
        CHANNEL.registerMessage(id++, ParanoiaHallucinationPayload.class,
                ParanoiaHallucinationPayload::encode, ParanoiaHallucinationPayload::decode,
                (payload, ctx) -> {
                    ctx.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                            () -> () -> ParanoiaHallucinationClientHandler.handle(payload)));
                    ctx.get().setPacketHandled(true);
                },
                Optional.of(NetworkDirection.PLAY_TO_CLIENT));

        // Strain Data Pad data -> client
        CHANNEL.registerMessage(id++, StrainDataPadPayload.class,
                StrainDataPadPayload::encode, StrainDataPadPayload::decode,
                (payload, ctx) -> {
                    ctx.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                            () -> () -> StrainDataPadClientHandler.handle(payload)));
                    ctx.get().setPacketHandled(true);
                },
                Optional.of(NetworkDirection.PLAY_TO_CLIENT));

        // Strain Modifier slider update -> server
        CHANNEL.registerMessage(id++, StrainModifierUpdatePayload.class,
                StrainModifierUpdatePayload::encode, StrainModifierUpdatePayload::decode,
                StrainModifierUpdateHandler::handle,
                Optional.of(NetworkDirection.PLAY_TO_SERVER));
    }

    public static void sendToPlayer(ServerPlayer player, Object message) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), message);
    }

    public static void sendToServer(Object message) {
        CHANNEL.sendToServer(message);
    }
}
