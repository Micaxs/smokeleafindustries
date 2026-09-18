package net.micaxs.smokeleaf.network;

import net.micaxs.smokeleaf.screen.custom.StrainDataPadScreen;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class StrainDataPadClientHandler {
    public static void handle(StrainDataPadPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            net.minecraft.client.Minecraft.getInstance().setScreen(
                    new StrainDataPadScreen(payload.personal(), payload.server()));
        });
    }
}
