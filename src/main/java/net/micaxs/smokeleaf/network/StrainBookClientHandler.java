package net.micaxs.smokeleaf.network;

import net.micaxs.smokeleaf.screen.custom.StrainBookScreen;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class StrainBookClientHandler {
    public static void handle(StrainBookDataPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            net.minecraft.client.Minecraft.getInstance().setScreen(
                    new StrainBookScreen(payload.myStrains(), payload.globalDiscoveries()));
        });
    }
}
