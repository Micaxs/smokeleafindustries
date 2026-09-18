package net.micaxs.smokeleaf.screen.custom;

import net.micaxs.smokeleaf.client.guide.GuideEntry;
import net.micaxs.smokeleaf.client.guide.GuideIcons;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.function.Consumer;

/**
 * The guide book's "table of contents" pane: every {@link GuideEntry} in the currently selected
 * category, one icon+name row each — modeled directly on {@code PersonalStrainList}'s row style.
 * Clicking a row opens that entry.
 */
public class GuideEntryList extends AbstractSelectionList<GuideEntryList.Row> {
    private static final int ROW_HEIGHT = 20;

    private final int rowWidth;

    public GuideEntryList(Minecraft minecraft, int x, int y, int width, int height, List<GuideEntry> entries, Consumer<GuideEntry> onSelect) {
        super(minecraft, width, height, y, ROW_HEIGHT);
        this.setX(x);
        this.rowWidth = width - (SCROLLBAR_WIDTH + 2) * 2;
        for (GuideEntry entry : entries) {
            addEntry(new Row(entry, onSelect));
        }
    }

    @Override
    public int getRowWidth() {
        return rowWidth;
    }

    @Override
    public int getRowLeft() {
        // AbstractSelectionList's own getRowLeft() adds a +2 pixel pad that
        // getEntryAtPosition() (used for hover hit-testing) does not, so the row we render
        // 2px right of where the mouse actually needs to be for `hovering` to be true. Drop
        // the pad so the drawn hover background lines up with the real hit box.
        return this.getX() + this.width / 2 - this.getRowWidth() / 2;
    }

    @Override
    protected int getScrollbarPosition() {
        return this.getRight() - SCROLLBAR_WIDTH;
    }

    @Override
    protected void renderListBackground(GuiGraphics guiGraphics) {
        // The book page texture already provides the background; nothing extra to draw.
    }

    @Override
    protected void renderListSeparators(GuiGraphics guiGraphics) {
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
    }

    public class Row extends AbstractSelectionList.Entry<Row> {
        private final GuideEntry entry;
        private final ItemStack icon;
        private final Consumer<GuideEntry> onSelect;

        Row(GuideEntry entry, Consumer<GuideEntry> onSelect) {
            this.entry = entry;
            this.onSelect = onSelect;
            this.icon = GuideIcons.stack(entry.icon(), entry.strainId());
        }

        @Override
        public void render(GuiGraphics guiGraphics, int index, int top, int left, int width, int height,
                            int mouseX, int mouseY, boolean hovering, float partialTick) {
            // AbstractSelectionList hands us `height = itemHeight - 4` (16, not the real 20px
            // row band), so sizing the highlight to it clips off the bottom of the 16px icon
            // drawn at a fixed `top + 2`. Use the real row height instead so the highlight
            // fully contains the icon.
            int bg = hovering ? 0x33000000 : 0x00000000;
            guiGraphics.fill(left, top, left + width, top + ROW_HEIGHT - 1, bg);
            guiGraphics.renderItem(icon, left + 2, top + 2);
            int textY = top + (ROW_HEIGHT - 1 - 8) / 2;
            guiGraphics.drawString(minecraft.font, entry.name(), left + 22, textY, 0xFF3F2E1E, false);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (button == 0) {
                onSelect.accept(entry);
                return true;
            }
            return false;
        }
    }
}
