package net.micaxs.smokeleaf.client;

/** Color helpers for GUI text, which takes full ARGB since 1.21.6. */
public final class GuiColors {
    private GuiColors() {}

    /**
     * Text colors used to be RGB with the alpha byte ignored when (almost) zero; now that byte is
     * real alpha. Keeps that old behavior so {@code 0x404040}-style colors stay opaque.
     */
    public static int text(int color) {
        return (color & 0xFC000000) == 0 ? color | 0xFF000000 : color;
    }
}
