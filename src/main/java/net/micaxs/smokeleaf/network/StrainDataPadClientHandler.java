package net.micaxs.smokeleaf.network;

import net.micaxs.smokeleaf.screen.custom.StrainDataPadScreen;

public class StrainDataPadClientHandler {
    public static void handle(StrainDataPadPayload payload) {
        net.minecraft.client.Minecraft.getInstance().setScreen(
                new StrainDataPadScreen(payload.personal(), payload.server()));
    }
}
