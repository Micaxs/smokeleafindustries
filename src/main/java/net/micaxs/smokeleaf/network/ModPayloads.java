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
        // NOTE: registered as a lambda (not a bare method reference) so that resolving
        // ParanoiaHallucinationClientHandler is deferred until the handler actually runs.
        // A direct method reference here forces the class (and its client-only imports,
        // e.g. Minecraft/Screen) to load during registration, which also happens on the
        // dedicated server and crashes with "invalid dist DEDICATED_SERVER".
        registrar.playToClient(
                ParanoiaHallucinationPayload.TYPE,
                ParanoiaHallucinationPayload.STREAM_CODEC,
                (payload, context) -> ParanoiaHallucinationClientHandler.handle(payload, context)
        );

        // Strain Data Pad data -> client
        registrar.playToClient(
                StrainDataPadPayload.TYPE,
                StrainDataPadPayload.STREAM_CODEC,
                (payload, context) -> StrainDataPadClientHandler.handle(payload, context)
        );

        // Strain Modifier slider update -> server
        registrar.playToServer(
                StrainModifierUpdatePayload.TYPE,
                StrainModifierUpdatePayload.STREAM_CODEC,
                StrainModifierUpdateHandler::handle
        );
    }
}
