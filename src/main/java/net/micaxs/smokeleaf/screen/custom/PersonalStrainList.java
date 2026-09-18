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
 * Vertically scrollable list for the Strain Data Pad's "Personal" tab. Each row previews the
 * strain as a bud item stack (tinted via STRAIN_DATA, matching the Server tab's style), plus its
 * name and THC/CBD — and, when applicable, the two parent strains it was crossed from (so it can
 * be recreated) and a note when this player wasn't the original discoverer.
 */
public class PersonalStrainList extends AbstractSelectionList<PersonalStrainList.Row> {

    private static final int LINE_HEIGHT = 10;
    private static final int ITEM_HEIGHT = 40;

    private final int rowWidth;
    private final String selfName;

    public PersonalStrainList(Minecraft minecraft, int x, int y, int width, int height,
                               List<StrainDataPadPayload.Entry> entries, String selfName) {
        super(minecraft, width, height, y, ITEM_HEIGHT);
        this.setX(x);
        // Symmetric margin on both sides (rows are centered by the base class — see
        // getScrollbarPosition below for why this can't be asymmetric) sized so the right margin
        // has room for the scrollbar plus a small gap, and the scrollbar sits flush against the
        // list box's inner right edge instead of overflowing past it.
        this.rowWidth = width - (SCROLLBAR_WIDTH + 2) * 2;
        this.selfName = selfName;
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
        // Panel texture already draws the recessed list frame; nothing to do here.
    }

    @Override
    protected void renderListSeparators(GuiGraphics guiGraphics) {
        // No vanilla header/footer separator sprites — they don't match this UI's theme.
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
            int bg = hovering ? 0x33FFFFFF : (index % 2 == 0 ? 0x22000000 : 0x11000000);
            guiGraphics.fill(left, top, left + width, top + height - 2, bg);

            int rowContentHeight = LINE_HEIGHT * 2
                    + (hasCross() ? LINE_HEIGHT : 0)
                    + (hasDiscoveredNote() ? LINE_HEIGHT : 0);
            int iconY = top + Math.max(2, (height - 2 - 16) / 2);
            int textY = top + Math.max(3, (height - 2 - rowContentHeight) / 2);

            guiGraphics.renderItem(preview, left + 4, iconY);

            int textX = left + 26;

            guiGraphics.drawString(minecraft.font, entry.data().displayName(), textX, textY, 0xFFE8E8E8, false);
            textY += LINE_HEIGHT;

            String stats = "THC " + entry.data().thc() + "%   CBD " + entry.data().cbd() + "%";
            guiGraphics.drawString(minecraft.font, stats, textX, textY, 0xFFA0AAA0, false);
            textY += LINE_HEIGHT;

            if (hasCross()) {
                Component cross = Component.translatable("gui.smokeleafindustries.strain_book.cross",
                        entry.data().baseStrain1(), entry.data().baseStrain2());
                guiGraphics.drawString(minecraft.font, cross, textX, textY, 0xFF9FB086, false);
                textY += LINE_HEIGHT;
            }

            if (hasDiscoveredNote()) {
                Component note = Component.translatable("gui.smokeleafindustries.strain_book.originally_by", entry.creatorName());
                guiGraphics.drawString(minecraft.font, note, textX, textY, 0xFF8A8A70, false);
            }
        }

        private boolean hasCross() {
            return !entry.data().baseStrain1().isBlank() && !entry.data().baseStrain2().isBlank();
        }

        private boolean hasDiscoveredNote() {
            return !entry.creatorName().isBlank() && !entry.creatorName().equalsIgnoreCase(selfName);
        }
    }
}
