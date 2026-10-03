package net.micaxs.smokeleaf.screen.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;

/**
 * A leather-and-gold styled button matching the guide book's palette, used in place of vanilla's
 * flat gray {@link Button} sprite so the Done/Back controls read as part of the book instead of a
 * generic menu widget pasted on top of it.
 */
public class GuideBookButton extends Button {
    private static final int BORDER = 0xFF2E1B10;
    private static final int BORDER_HOVER = 0xFFC89E4C;
    private static final int FILL = 0xFF4A2F1D;
    private static final int FILL_HOVER = 0xFF6B4423;
    private static final int FILL_DISABLED = 0xFF33241A;
    private static final int TEXT = 0xFFE8D0A0;
    private static final int TEXT_DISABLED = 0xFF806030;

    protected GuideBookButton(Builder builder) {
        super(builder);
    }

    /** {@code Button.builder(message, onPress).bounds(...).build(GuideBookButton::of)} */
    public static Button of(Builder builder) {
        return new GuideBookButton(builder);
    }

    @Override
    public void renderWidget(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        boolean hovered = this.active && this.isHoveredOrFocused();
        int border = hovered ? BORDER_HOVER : BORDER;
        int fill = !this.active ? FILL_DISABLED : (hovered ? FILL_HOVER : FILL);
        g.fill(getX(), getY(), getX() + getWidth(), getY() + getHeight(), border);
        g.fill(getX() + 1, getY() + 1, getX() + getWidth() - 1, getY() + getHeight() - 1, fill);

        int color = this.active ? TEXT : TEXT_DISABLED;
        Minecraft mc = Minecraft.getInstance();
        g.drawCenteredString(mc.font, getMessage(), getX() + getWidth() / 2, getY() + (getHeight() - 8) / 2, color);
    }
}
