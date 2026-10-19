package net.micaxs.smokeleaf.screen.widget;

import net.minecraft.client.renderer.RenderPipelines;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.resources.Identifier;

import java.util.function.Consumer;

public class UvImageButton extends AbstractButton {
    private final Identifier texture;
    private final int texW, texH;
    private final int u, v, w, h;
    private final Integer hoverU, hoverV;
    private final int hoverVOffset;
    private final Consumer<UvImageButton> onPressCb;

    // Hover by explicit UVs
    public UvImageButton(int x, int y, int width, int height,
                         int u, int v, int hoverU, int hoverV,
                         Identifier texture, int texW, int texH,
                         Consumer<UvImageButton> onPress) {
        super(x, y, width, height, CommonComponents.EMPTY);
        this.u = u; this.v = v; this.w = width; this.h = height;
        this.hoverU = hoverU; this.hoverV = hoverV; this.hoverVOffset = 0;
        this.texture = texture; this.texW = texW; this.texH = texH;
        this.onPressCb = onPress;
    }

    // Legacy hover via V offset (unused here but handy)
    public UvImageButton(int x, int y, int width, int height,
                         int u, int v, int hoverVOffset,
                         Identifier texture, int texW, int texH,
                         Consumer<UvImageButton> onPress) {
        super(x, y, width, height, CommonComponents.EMPTY);
        this.u = u; this.v = v; this.w = width; this.h = height;
        this.hoverU = null; this.hoverV = null; this.hoverVOffset = hoverVOffset;
        this.texture = texture; this.texW = texW; this.texH = texH;
        this.onPressCb = onPress;
    }

    @Override
    public void onPress(InputWithModifiers input) {
        if (onPressCb != null) onPressCb.accept(this);
    }

    @Override
    protected void extractContents(GuiGraphicsExtractor g, int mouseX, int mouseY, float partialTick) {
        int du = u;
        int dv = v;
        if (this.isHoveredOrFocused()) {
            if (hoverU != null && hoverV != null) { du = hoverU; dv = hoverV; }
            else { dv = v + hoverVOffset; }
        }
        g.blit(RenderPipelines.GUI_TEXTURED, texture, getX(), getY(), du, dv, w, h, texW, texH);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narration) {
        this.defaultButtonNarrationText(narration);
    }
}