package net.micaxs.smokeleaf.network;

import net.micaxs.smokeleaf.block.entity.StrainModifierBlockEntity;
import net.micaxs.smokeleaf.screen.custom.StrainModifierMenu;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class StrainModifierUpdateHandler {

    public static void handle(StrainModifierUpdatePayload payload, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (ctx.player() instanceof ServerPlayer player) {
                if (player.containerMenu instanceof StrainModifierMenu menu) {
                    menu.blockEntity.applySliderUpdate(
                            payload.thc(),
                            payload.cbd(),
                            payload.leafR(), payload.leafG(), payload.leafB(),
                            payload.strainR(), payload.strainG(), payload.strainB(),
                            payload.name()
                    );
                }
            }
        });
    }
}
