package net.micaxs.smokeleaf.screen.custom;

import net.micaxs.smokeleaf.component.ModDataComponentTypes;
import net.micaxs.smokeleaf.item.ModItems;
import net.micaxs.smokeleaf.network.StrainDataPadPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * Vertically scrollable list for the Strain Data Pad's "Server" tab — every strain any player has
 * registered/named server-wide, shown as a bud icon, its name, and who discovered it.
 */
public class ServerStrainList extends AbstractSelectionList<ServerStrainList.Row> {

    private static final int ITEM_HEIGHT = 26;

    private final int rowWidth;

    public ServerStrainList(Minecraft minecraft, int x, int y, int width, int height,
                             List<StrainDataPadPayload.Entry> entries) {
        super(minecraft, width, height, y, ITEM_HEIGHT);
        this.setX(x);
        // Symmetric margin on both sides (rows are centered by the base class — see
        // getScrollbarPosition below for why this can't be asymmetric) sized so the right margin
        // has room for the scrollbar plus a small gap, and the scrollbar sits flush against the
        // list box's inner right edge instead of overflowing past it.
        this.rowWidth = width - (SCROLLBAR_WIDTH + 2) * 2;
        for (StrainDataPadPayload.Entry e : entries) {
            addEntry(new Row(e));
        }
    }

    @Override
    public int getRowWidth() {
        return rowWidth;
    }

    @Override
    protected int getScrollbarPosition() {
        return this.getRight() - SCROLLBAR_WIDTH;
    }

    @Override
    protected void renderListBackground(GuiGraphics guiGraphics) {
    }

    @Override
    protected void renderListSeparators(GuiGraphics guiGraphics) {
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
    }

    public class Row extends AbstractSelectionList.Entry<Row> {
        private final StrainDataPadPayload.Entry entry;
        private final ItemStack preview;

        Row(StrainDataPadPayload.Entry entry) {
            this.entry = entry;
            this.preview = new ItemStack(ModItems.GENERIC_BUD.get());
            this.preview.set(ModDataComponentTypes.STRAIN_DATA.get(), entry.data());
        }

        @Override
        public void render(GuiGraphics guiGraphics, int index, int top, int left, int width, int height,
                            int mouseX, int mouseY, boolean hovering, float partialTick) {
            int fillBottom = top + height - 2;
            int bg = hovering ? 0x33FFFFFF : (index % 2 == 0 ? 0x22000000 : 0x11000000);
            guiGraphics.fill(left, top, left + width, fillBottom, bg);

            // Content (16px icon, two 9px text lines) centered within the filled band so it
            // doesn't hug the top and spill past the bottom.
            int visibleH = fillBottom - top;
            int iconY = top + Math.max(1, (visibleH - 16) / 2);
            int textY = top + Math.max(1, (visibleH - (9 + 9)) / 2);

            guiGraphics.renderItem(preview, left + 4, iconY);

            int textX = left + 26;
            guiGraphics.drawString(minecraft.font, entry.data().displayName(), textX, textY, 0xFFE8E8E8, false);

            String discoverer = entry.creatorName().isBlank()
                    ? Component.translatable("gui.smokeleafindustries.strain_book.unknown_discoverer").getString()
                    : Component.translatable("gui.smokeleafindustries.strain_book.by", entry.creatorName()).getString();
            guiGraphics.drawString(minecraft.font, discoverer, textX, textY + 9, 0xFFA0AAA0, false);
        }
    }
}
