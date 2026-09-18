package net.micaxs.smokeleaf.screen.custom;

import net.micaxs.smokeleaf.SmokeleafIndustries;
import net.micaxs.smokeleaf.network.StrainDataPadPayload;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

/**
 * The Strain Data Pad's UI: two tabs ("Personal" and "Server" discoveries), each backed by its
 * own vertically-scrollable list widget ({@link PersonalStrainList} / {@link ServerStrainList}).
 * Standalone screen — no container/menu, nothing to charge, purely a read-only viewer.
 */
public class StrainDataPadScreen extends Screen {

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(SmokeleafIndustries.MODID, "textures/gui/strain_data_pad/strain_data_pad_gui.png");

    private static final int IMAGE_WIDTH = 240;
    private static final int IMAGE_HEIGHT = 222;

    private static final int TAB_Y = 6;
    private static final int TAB_HEIGHT = 18;
    private static final int TAB_WIDTH = 113;
    private static final int TAB_GAP = 2;

    private final List<StrainDataPadPayload.Entry> personalEntries;
    private final List<StrainDataPadPayload.Entry> serverEntries;

    private int leftPos;
    private int topPos;
    private boolean serverTab = false;

    private PersonalStrainList personalList;
    private ServerStrainList serverList;

    public StrainDataPadScreen(List<StrainDataPadPayload.Entry> personalEntries, List<StrainDataPadPayload.Entry> serverEntries) {
        super(Component.translatable("gui.smokeleafindustries.strain_book"));
        this.personalEntries = personalEntries;
        this.serverEntries = serverEntries;
    }

    @Override
    protected void init() {
        leftPos = (width - IMAGE_WIDTH) / 2;
        topPos = (height - IMAGE_HEIGHT) / 2;

        String selfName = minecraft.player != null ? minecraft.player.getName().getString() : "";

        int listX = leftPos + 8;
        int listY = topPos + 30;
        int listW = IMAGE_WIDTH - 16;
        int listH = IMAGE_HEIGHT - 38;

        personalList = new PersonalStrainList(minecraft, listX, listY, listW, listH, personalEntries, selfName);
        serverList = new ServerStrainList(minecraft, listX, listY, listW, listH, serverEntries);

        updateVisibleList();
    }

    private void updateVisibleList() {
        removeWidget(personalList);
        removeWidget(serverList);
        addRenderableWidget(serverTab ? serverList : personalList);
    }

    private int tab1X() { return leftPos + 6; }
    private int tab2X() { return leftPos + 6 + TAB_WIDTH + TAB_GAP; }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            int ty = topPos + TAB_Y;
            if (mouseY >= ty && mouseY <= ty + TAB_HEIGHT) {
                if (mouseX >= tab1X() && mouseX <= tab1X() + TAB_WIDTH && serverTab) {
                    serverTab = false;
                    updateVisibleList();
                    return true;
                }
                if (mouseX >= tab2X() && mouseX <= tab2X() + TAB_WIDTH && !serverTab) {
                    serverTab = true;
                    updateVisibleList();
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    /**
     * Overridden (not just avoided) — {@link Screen#render} unconditionally calls
     * {@code this.renderBackground(...)} again internally regardless of what this class's own
     * {@link #render} does beforehand, so simply not calling it ourselves wasn't enough: the
     * vanilla implementation (which triggers the pause-menu-style gaussian blur post-effect,
     * {@code GameRenderer.processBlurEffect}) was still reached through that second, indirect
     * call. Overriding the method itself is the only way to guarantee the blur shader never runs.
     */
    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderTransparentBackground(guiGraphics);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.blit(TEXTURE, leftPos, topPos, 0, 0, IMAGE_WIDTH, IMAGE_HEIGHT, IMAGE_WIDTH, IMAGE_HEIGHT);

        // Active-tab highlight.
        int activeX = serverTab ? tab2X() : tab1X();
        guiGraphics.fill(activeX, topPos + TAB_Y, activeX + TAB_WIDTH, topPos + TAB_Y + TAB_HEIGHT, 0x552E8B57);
        guiGraphics.renderOutline(activeX, topPos + TAB_Y, TAB_WIDTH, TAB_HEIGHT, 0xFF6FCF6F);

        // super.render() calls renderBackground() again internally — harmless now that it's
        // overridden above to never invoke the blur shader.
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        drawCenteredTabLabel(guiGraphics, Component.translatable("gui.smokeleafindustries.strain_book.my_strains"), tab1X(), !serverTab);
        drawCenteredTabLabel(guiGraphics, Component.translatable("gui.smokeleafindustries.strain_book.server_discoveries"), tab2X(), serverTab);

        boolean empty = serverTab ? serverEntries.isEmpty() : personalEntries.isEmpty();
        if (empty) {
            Component msg = Component.translatable(serverTab
                    ? "gui.smokeleafindustries.strain_book.no_global"
                    : "gui.smokeleafindustries.strain_book.no_strains");
            guiGraphics.drawCenteredString(this.font, msg, leftPos + IMAGE_WIDTH / 2, topPos + IMAGE_HEIGHT / 2, 0xFFAAAAAA);
        }
    }

    private void drawCenteredTabLabel(GuiGraphics guiGraphics, Component label, int tabX, boolean active) {
        int color = active ? 0xFFFFFFFF : 0xFFB0B0B0;
        int textWidth = this.font.width(label);
        int x = tabX + (TAB_WIDTH - textWidth) / 2;
        int y = topPos + TAB_Y + (TAB_HEIGHT - 8) / 2;
        guiGraphics.drawString(this.font, label, x, y, color, false);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
