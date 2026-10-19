package net.micaxs.smokeleaf.client;

import net.micaxs.smokeleaf.screen.custom.MagnifyingGlassScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;

/**
 * Client-only screen helpers. Common code may call these only from client-side branches
 * (e.g. {@code level.isClientSide()}), so this class is never loaded on a dedicated server.
 */
public final class ClientScreens {
    private ClientScreens() {}

    public static void openAnalyzerScreen(BlockPos pos) {
        Minecraft.getInstance().setScreen(new MagnifyingGlassScreen(pos));
    }
}
