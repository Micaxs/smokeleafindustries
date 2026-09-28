package net.micaxs.smokeleaf.command;

import net.micaxs.smokeleaf.SmokeleafIndustries;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.event.RegisterCommandsEvent;
@Mod.EventBusSubscriber(modid = SmokeleafIndustries.MODID)
public final class ModCommands {

    private ModCommands() {}

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        DNADebugCommand.register(event.getDispatcher());
    }
}