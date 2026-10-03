package net.micaxs.smokeleaf.screen.custom;

import com.mojang.blaze3d.systems.RenderSystem;
import net.micaxs.smokeleaf.SmokeleafIndustries;
import net.micaxs.smokeleaf.block.entity.StrainModifierBlockEntity;
import net.micaxs.smokeleaf.network.StrainModifierUpdatePayload;
import net.micaxs.smokeleaf.utils.MouseUtil;
import net.micaxs.smokeleaf.utils.StrainModifierCostUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

public class StrainModifierScreen extends AbstractContainerScreen<StrainModifierMenu> {

    public static final ResourceLocation GUI_TEXTURE = ResourceLocation.fromNamespaceAndPath(SmokeleafIndustries.MODID, "textures/gui/strain_modifier/strain_modifier_gui.png");

    // ── Naming Input Box
    private static final int NAME_BOX_X = 31, NAME_BOX_Y = 9, NAME_BOX_W = 117, NAME_BOX_H = 9;
    private EditBox nameInput;

    // ── Button Click Area's
    private static final int BTN_Y = 20, BTN_H = 8;
    private static final int BTN_LEAF_X = 29,  BTN_LEAF_W = 26;
    private static final int BTN_BUDS_X = 55,  BTN_BUDS_W = 26;
    private static final int BTN_SAVE_X = 122, BTN_SAVE_W = 26;
    private static final int TAB_UNDERLINE_Y = 27;

    // ── Leaf Meter Bar
    private static final int METER_X = 12,  METER_Y = 30;
    private static final int METER_W = 8, METER_H = 45;
    private static final int METER_FILL_COLOR = 0xFF2E7D32;
    private static final int METER_LINE_OK    = 0xFF1B5E20;
    private static final int METER_LINE_BAD   = 0xFFEA2A2A;

    // ── Disabled (no seed) slider color
    private static final int SLIDER_DISABLED_COLOR = 0xFF888888;

    // ── RGB sliders
    private static final int SLIDER_FILL_X = 31, SLIDER_FILL_W = 106, SLIDER_H = 4;
    private static final int SLIDER_R_Y = 31, SLIDER_G_Y = 39, SLIDER_B_Y = 47;
    private static final int HANDLE_W = 2, HANDLE_EXTRA = 1;

    // ── Colour dots
    private static final int DOT1_X = 155, DOT1_Y = 28, DOT_W = 12, DOT_H = 12;
    private static final int DOT2_X = 155, DOT2_Y = 42;

    // ── THC / CBD sliders
    private static final int STAT_FILL_X = 49, STAT_FILL_W = 88;
    private static final int THC_Y = 61, CBD_Y = 69;

    // ── Colour bounds
    private static final int COLOR_MIN = StrainModifierBlockEntity.COLOR_MIN;
    private static final int COLOR_MAX = StrainModifierBlockEntity.COLOR_MAX;

    // ── Client state
    private int activeTab = 0;   // 0=Leaf colour, 1=Strain/Buds colour
    private boolean initialised = false;
    private int lastOrigThc, lastOrigCbd;
    private int lastOrigLeafR, lastOrigLeafG, lastOrigLeafB;
    private int lastOrigStrainR, lastOrigStrainG, lastOrigStrainB;
    private String lastStrainName = "";

    private int sliderLeafR, sliderLeafG, sliderLeafB;
    private int sliderStrainR, sliderStrainG, sliderStrainB;
    private int sliderThc, sliderCbd;
    private int draggingSlider = -1;   // 0=R 1=G 2=B 3=THC 4=CBD, -1=none

    public StrainModifierScreen(StrainModifierMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void init() {
        super.init();
        this.inventoryLabelY = 100000;
        this.titleLabelY = 100000;

        int ox = (width - imageWidth) / 2;
        int oy = (height - imageHeight) / 2;

        // Initialize Name Input Field
        this.nameInput = new EditBox(this.font, ox + NAME_BOX_X, oy + NAME_BOX_Y, NAME_BOX_W, NAME_BOX_H, Component.literal("Strain Name"));
        this.nameInput.setMaxLength(32);
        this.nameInput.setBordered(false);
        this.nameInput.setTextColor(0xFFFFFF);
        this.nameInput.setResponder(text -> {
            // Re-evaluate client visual state on input change
        });
        this.addRenderableWidget(this.nameInput);

        initialised = false;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float v, int i, int i1) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, GUI_TEXTURE);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        guiGraphics.blit(GUI_TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        ensureInitialised();

        renderLeafMeter(guiGraphics, x, y);
        renderTabIndicator(guiGraphics, x, y);
        renderColorDots(guiGraphics, x, y);
        renderRgbSliders(guiGraphics, x, y);
        renderStatSliders(guiGraphics, x, y);
    }

    @Override
    protected void renderLabels(GuiGraphics g, int mouseX, int mouseY) {
        int ox = (width - imageWidth) / 2, oy = (height - imageHeight) / 2;
        int cost = calculatePreviewCost();
        int meter = menu.getLeafMeter();

        // Render THC & CBD Values to the right of the sliders
        String thcStr = sliderThc + "%";
        String cbdStr = sliderCbd + "%";
        g.drawString(font, thcStr, STAT_FILL_X + STAT_FILL_W + 3, THC_Y - 1, 0xFFC5C5C5, false);
        g.drawString(font, cbdStr, STAT_FILL_X + STAT_FILL_W + 3, CBD_Y - 1, 0xFFC5C5C5, false);

        // Tooltip: Save Button
        if (MouseUtil.isMouseOver(mouseX, mouseY, ox + BTN_SAVE_X, oy + BTN_Y, BTN_SAVE_W, BTN_H)) {
            String msg;
            if (!menu.hasSeed())    msg = "No seed inserted";
            else if (cost > meter)  msg = "Not enough leaves (need " + cost + ", have " + meter + ")";
            else if (cost == 0)     msg = "No changes to save";
            else                    msg = "Save  (cost: " + cost + " leaves)";
            g.renderTooltip(font, Component.literal(msg), mouseX - ox, mouseY - oy);
        }

        // Tooltip: Leaf Bar Meter
        if (MouseUtil.isMouseOver(mouseX, mouseY, ox + METER_X, oy + METER_Y, METER_W, METER_H)) {
            String meterText = "Leaves: " + meter + " / " + StrainModifierBlockEntity.MAX_LEAF_CAPACITY;
            String costText = "Current Cost: " + cost + " leaves";
            g.renderTooltip(font, java.util.List.of(
                    Component.literal(meterText),
                    Component.literal(costText)
            ), java.util.Optional.empty(), mouseX - ox, mouseY - oy);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            int ox = (width - imageWidth) / 2, oy = (height - imageHeight) / 2;
            int mx = (int) mouseX, my = (int) mouseY;

            // Clicking anywhere outside the name box drops its focus
            if (this.nameInput != null && this.nameInput.isFocused()
                    && !MouseUtil.isMouseOver(mx, my, ox + NAME_BOX_X, oy + NAME_BOX_Y, NAME_BOX_W, NAME_BOX_H)) {
                this.nameInput.setFocused(false);
            }

            // Tabs
            if (MouseUtil.isMouseOver(mx, my, ox + BTN_LEAF_X, oy + BTN_Y, BTN_LEAF_W, BTN_H)) {
                activeTab = 0; return true;
            }
            if (MouseUtil.isMouseOver(mx, my, ox + BTN_BUDS_X, oy + BTN_Y, BTN_BUDS_W, BTN_H)) {
                activeTab = 1; return true;
            }

            // Save button
            if (MouseUtil.isMouseOver(mx, my, ox + BTN_SAVE_X, oy + BTN_Y, BTN_SAVE_W, BTN_H)) {
                if (menu.hasSeed() && calculatePreviewCost() <= menu.getLeafMeter() && calculatePreviewCost() > 0) {
                    sendSliderUpdate();
                    if (minecraft != null && minecraft.gameMode != null)
                        minecraft.gameMode.handleInventoryButtonClick(menu.containerId, StrainModifierMenu.BUTTON_SAVE);
                }
                return true;
            }

            // Colour dots
            if (MouseUtil.isMouseOver(mx, my, ox + DOT1_X - 1, oy + DOT1_Y - 1, DOT_W + 2, DOT_H + 2)) { activeTab = 0; return true; }
            if (MouseUtil.isMouseOver(mx, my, ox + DOT2_X - 1, oy + DOT2_Y - 1, DOT_W + 2, DOT_H + 2)) { activeTab = 1; return true; }

            // Sliders — disabled while no seed is inserted
            if (menu.hasSeed()) {
                int slider = hitTestSlider(mx, my, ox, oy);
                if (slider >= 0) {
                    draggingSlider = slider;
                    updateSliderFromMouse(mx, ox);
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (button == 0 && draggingSlider >= 0) {
            if (!menu.hasSeed()) {
                draggingSlider = -1;
                return true;
            }
            updateSliderFromMouse((int) mouseX, (width - imageWidth) / 2);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0 && draggingSlider >= 0) {
            draggingSlider = -1;
            sendSliderUpdate();
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // While the name box is focused, swallow every key except Escape so global keybinds
        // (e.g. "E" for inventory) don't fire mid-typing. Escape still falls through to close
        // the screen normally, which also drops the box's focus.
        if (this.nameInput != null && this.nameInput.isFocused() && keyCode != GLFW.GLFW_KEY_ESCAPE) {
            this.nameInput.keyPressed(keyCode, scanCode, modifiers);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void ensureInitialised() {
        // Re-sync sliders (and the name box) from the block entity's "orig" values whenever the
        // underlying seed identity changes — not just once on GUI open. Without this, swapping the
        // seed while the screen is already open leaves the sliders showing stale/default values
        // instead of the newly-inserted seed's actual THC/CBD/colors/name.
        int oThc = menu.getOrigThc(), oCbd = menu.getOrigCbd();
        int oLR = menu.getOrigLeafR(), oLG = menu.getOrigLeafG(), oLB = menu.getOrigLeafB();
        int oSR = menu.getOrigStrainR(), oSG = menu.getOrigStrainG(), oSB = menu.getOrigStrainB();
        String oName = menu.getStrainName();

        boolean seedChanged = !initialised
                || oThc != lastOrigThc || oCbd != lastOrigCbd
                || oLR != lastOrigLeafR || oLG != lastOrigLeafG || oLB != lastOrigLeafB
                || oSR != lastOrigStrainR || oSG != lastOrigStrainG || oSB != lastOrigStrainB
                || !oName.equals(lastStrainName);
        if (!seedChanged) return;

        if (menu.hasSeed()) {
            sliderLeafR   = menu.getLeafColorR();
            sliderLeafG   = menu.getLeafColorG();
            sliderLeafB   = menu.getLeafColorB();

            sliderStrainR = menu.getStrainColorR();
            sliderStrainG = menu.getStrainColorG();
            sliderStrainB = menu.getStrainColorB();

            sliderThc = menu.getPreviewThc();
            sliderCbd = menu.getPreviewCbd();
        } else {
            sliderLeafR = sliderLeafG = sliderLeafB = 0;
            sliderStrainR = sliderStrainG = sliderStrainB = 0;
            sliderThc = 0;
            sliderCbd = 0;
        }

        if (this.nameInput != null) {
            this.nameInput.setValue(oName);
        }

        lastOrigThc = oThc; lastOrigCbd = oCbd;
        lastOrigLeafR = oLR; lastOrigLeafG = oLG; lastOrigLeafB = oLB;
        lastOrigStrainR = oSR; lastOrigStrainG = oSG; lastOrigStrainB = oSB;
        lastStrainName = oName;
        initialised = true;
    }

    private void renderLeafMeter(GuiGraphics guiGraphics, int x, int y) {
        int leafMeter = menu.getLeafMeter();
        int cost      = calculatePreviewCost();
        boolean canAfford = cost <= leafMeter;

        int fill     = Math.clamp(((long) leafMeter * METER_H) / StrainModifierBlockEntity.MAX_LEAF_CAPACITY, 0, METER_H);
        int costFill = Math.clamp(((long) cost * METER_H) / StrainModifierBlockEntity.MAX_LEAF_CAPACITY, 0, METER_H);

        int mx = x + METER_X, my = y + METER_Y;

        // Tank fill: how many leaves are actually in the machine.
        if (fill > 0) {
            guiGraphics.fill(mx, my + METER_H - fill, mx + METER_W, my + METER_H, METER_FILL_COLOR);
        }

        // Cost threshold line: dark green if affordable, red if not enough leaves.
        if (cost > 0) {
            int lineY = my + METER_H - costFill;
            guiGraphics.fill(mx, lineY, mx + METER_W, lineY + 1, canAfford ? METER_LINE_OK : METER_LINE_BAD);
        }
    }

    private void renderTabIndicator(GuiGraphics guiGraphics, int x, int y) {
        int uly = y + TAB_UNDERLINE_Y;
        if (activeTab == 0) {
            guiGraphics.fill(x+1 + BTN_LEAF_X - 1, uly, x+1 + BTN_LEAF_X + BTN_LEAF_W - 1, uly + 1, 0xFFd2c28f);
        } else {
            guiGraphics.fill(x+1 + BTN_BUDS_X - 1, uly, x+1 + BTN_BUDS_X + BTN_BUDS_W - 1, uly + 1, 0xFFd2c28f);
        }
    }

    private void renderColorDots(GuiGraphics guiGraphics, int x, int y) {
        if (!menu.hasSeed()) return;

        int leafArgb   = argb(sliderLeafR,   sliderLeafG,   sliderLeafB);
        int strainArgb = argb(sliderStrainR, sliderStrainG, sliderStrainB);

        guiGraphics.fill(x + DOT1_X, y + DOT1_Y, x + DOT1_X + DOT_W, y + DOT1_Y + DOT_H, leafArgb);
        guiGraphics.fill(x + DOT2_X, y + DOT2_Y, x + DOT2_X + DOT_W, y + DOT2_Y + DOT_H, strainArgb);

        drawDotBorder(guiGraphics, x + DOT1_X, y + DOT1_Y);
        drawDotBorder(guiGraphics, x + DOT2_X, y + DOT2_Y);
    }

    private void drawDotBorder(GuiGraphics guiGraphics, int dx, int dy) {
        guiGraphics.hLine(dx - 1,     dx + DOT_W, dy - 1,      0xFF232323);
        guiGraphics.hLine(dx - 1,     dx + DOT_W, dy + DOT_H,  0xFF7c7c7c);
        guiGraphics.vLine(dx - 1,     dy - 1,     dy + DOT_H,  0xFF444444);
        guiGraphics.vLine(dx + DOT_W, dy - 1,     dy + DOT_H,  0xFF7c7c7c);
    }

    private void renderRgbSliders(GuiGraphics g, int ox, int oy) {
        boolean enabled = menu.hasSeed();
        int r, gr, b;
        if (activeTab == 0) { r = sliderLeafR;   gr = sliderLeafG;   b = sliderLeafB;   }
        else                 { r = sliderStrainR; gr = sliderStrainG; b = sliderStrainB; }
        drawSlider(g, ox, oy, SLIDER_FILL_X, SLIDER_R_Y, SLIDER_FILL_W, r,  COLOR_MIN, COLOR_MAX, enabled ? 0xFFCC3333 : SLIDER_DISABLED_COLOR);
        drawSlider(g, ox, oy, SLIDER_FILL_X, SLIDER_G_Y, SLIDER_FILL_W, gr, COLOR_MIN, COLOR_MAX, enabled ? 0xFF33CC33 : SLIDER_DISABLED_COLOR);
        drawSlider(g, ox, oy, SLIDER_FILL_X, SLIDER_B_Y, SLIDER_FILL_W, b,  COLOR_MIN, COLOR_MAX, enabled ? 0xFF3333CC : SLIDER_DISABLED_COLOR);
    }

    private void renderStatSliders(GuiGraphics g, int ox, int oy) {
        boolean enabled = menu.hasSeed();
        drawSlider(g, ox, oy, STAT_FILL_X, THC_Y, STAT_FILL_W, sliderThc, 0, 35, enabled ? 0xFF886600 : SLIDER_DISABLED_COLOR);
        drawSlider(g, ox, oy, STAT_FILL_X, CBD_Y, STAT_FILL_W, sliderCbd, 0, 30, enabled ? 0xFF336688 : SLIDER_DISABLED_COLOR);
    }

    private void drawSlider(GuiGraphics g, int ox, int oy,
                            int fillX, int fillY, int fillW,
                            int value, int min, int max, int fillColor) {
        int ax = ox + fillX, ay = oy + fillY;
        int fillPx = Math.max(0, Math.min(fillW, (value - min) * fillW / Math.max(1, max - min)));
        if (fillPx > 0) g.fill(ax, ay, ax + fillPx, ay + SLIDER_H, fillColor);
        int hx = Math.max(ax, Math.min(ax + fillW - HANDLE_W, ax + fillPx - HANDLE_W / 2));
        g.fill(hx, ay - HANDLE_EXTRA, hx + HANDLE_W, ay + SLIDER_H + HANDLE_EXTRA, 0xFFDDDDDD);
    }

    private int hitTestSlider(int mx, int my, int ox, int oy) {
        int pad = HANDLE_EXTRA + 2;
        if (inSlider(mx, my, ox + SLIDER_FILL_X, oy + SLIDER_R_Y, SLIDER_FILL_W, pad)) return 0;
        if (inSlider(mx, my, ox + SLIDER_FILL_X, oy + SLIDER_G_Y, SLIDER_FILL_W, pad)) return 1;
        if (inSlider(mx, my, ox + SLIDER_FILL_X, oy + SLIDER_B_Y, SLIDER_FILL_W, pad)) return 2;
        if (inSlider(mx, my, ox + STAT_FILL_X,   oy + THC_Y,      STAT_FILL_W,   pad)) return 3;
        if (inSlider(mx, my, ox + STAT_FILL_X,   oy + CBD_Y,      STAT_FILL_W,   pad)) return 4;
        return -1;
    }

    private static boolean inSlider(int mx, int my, int tx, int ty, int tw, int pad) {
        return mx >= tx && mx <= tx + tw && my >= ty - pad && my <= ty + SLIDER_H + pad;
    }

    private void updateSliderFromMouse(int mouseX, int ox) {
        int[] fillX = { ox+SLIDER_FILL_X, ox+SLIDER_FILL_X, ox+SLIDER_FILL_X, ox+STAT_FILL_X, ox+STAT_FILL_X };
        int[] fillW = { SLIDER_FILL_W, SLIDER_FILL_W, SLIDER_FILL_W, STAT_FILL_W, STAT_FILL_W };
        int[] minV  = { COLOR_MIN, COLOR_MIN, COLOR_MIN, 0, 0 };
        int[] maxV  = { COLOR_MAX, COLOR_MAX, COLOR_MAX, 35, 30 };

        int idx = draggingSlider;
        if (idx < 0 || idx >= fillX.length) return;
        int clamped = Math.max(0, Math.min(fillW[idx], mouseX - fillX[idx]));
        int value   = minV[idx] + clamped * Math.max(1, maxV[idx] - minV[idx]) / fillW[idx];
        switch (idx) {
            case 0 -> { if (activeTab == 0) sliderLeafR   = value; else sliderStrainR = value; }
            case 1 -> { if (activeTab == 0) sliderLeafG   = value; else sliderStrainG = value; }
            case 2 -> { if (activeTab == 0) sliderLeafB   = value; else sliderStrainB = value; }
            case 3 -> sliderThc = value;
            case 4 -> sliderCbd = value;
        }
    }

    private int calculatePreviewCost() {
        if (!menu.hasSeed()) return 0;
        String inputName = this.nameInput != null ? this.nameInput.getValue() : "";
        return StrainModifierCostUtil.calculateCost(
                menu.getOrigThc(), menu.getOrigCbd(),
                menu.getOrigLeafR(), menu.getOrigLeafG(), menu.getOrigLeafB(),
                menu.getOrigStrainR(), menu.getOrigStrainG(), menu.getOrigStrainB(),
                sliderThc, sliderCbd,
                sliderLeafR, sliderLeafG, sliderLeafB,
                sliderStrainR, sliderStrainG, sliderStrainB,
                menu.getStrainName(), inputName
        );
    }

    private void sendSliderUpdate() {
        String inputName = this.nameInput != null ? this.nameInput.getValue() : "";
        PacketDistributor.sendToServer(new StrainModifierUpdatePayload(
                sliderThc, sliderCbd,
                sliderLeafR, sliderLeafG, sliderLeafB,
                sliderStrainR, sliderStrainG, sliderStrainB,
                inputName
        ));
    }

    private static int argb(int r, int g, int b) {
        return 0xFF000000 | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | (b & 0xFF);
    }
}