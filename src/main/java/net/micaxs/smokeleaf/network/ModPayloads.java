package net.micaxs.smokeleaf.network;

import net.micaxs.smokeleaf.SmokeleafIndustries;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class ModPayloads {

    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(SmokeleafIndustries.MODID)
                .versioned("1")
                .optional();

        // Paranoia Hallucination -> client
        registrar.playToClient(
                ParanoiaHallucinationPayload.TYPE,
                ParanoiaHallucinationPayload.STREAM_CODEC,
                ParanoiaHallucinationClientHandler::handle
        );

        // Strain Data Pad data -> client
        registrar.playToClient(
                StrainDataPadPayload.TYPE,
                StrainDataPadPayload.STREAM_CODEC,
                StrainDataPadClientHandler::handle
        );

        // Strain Modifier slider update -> server
        registrar.playToServer(
                StrainModifierUpdatePayload.TYPE,
                StrainModifierUpdatePayload.STREAM_CODEC,
                StrainModifierUpdateHandler::handle
        );
    }
}
