package net.micaxs.smokeleaf.network;

import net.micaxs.smokeleaf.block.entity.StrainModifierBlockEntity;
import net.micaxs.smokeleaf.screen.custom.StrainModifierMenu;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;
public class StrainModifierUpdateHandler {

    public static void handle(StrainModifierUpdatePayload payload, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
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
        ctx.get().setPacketHandled(true);
    }
}
