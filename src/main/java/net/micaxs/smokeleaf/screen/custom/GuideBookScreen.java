package net.micaxs.smokeleaf.screen.custom;

import net.micaxs.smokeleaf.SmokeleafIndustries;
import net.micaxs.smokeleaf.client.guide.GuideCategory;
import net.micaxs.smokeleaf.client.guide.GuideDataLoader;
import net.micaxs.smokeleaf.client.guide.GuideEntry;
import net.micaxs.smokeleaf.client.guide.GuideIcons;
import net.micaxs.smokeleaf.client.guide.GuidePage;
import net.micaxs.smokeleaf.client.guide.GuideText;
import net.micaxs.smokeleaf.screen.widget.GuideBookButton;
import net.micaxs.smokeleaf.screen.widget.UvImageButton;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Optional;

/**
 * The mod's Patchouli-free guide book: a vertical left-side category rail (styled after
 * {@code StrainDataPadScreen}'s hand-rolled tabs) plus a right-hand book page reusing the old
 * (previously dead) {@code SmokeleafGuideScreen}'s page-turn rendering, generalized to draw every
 * {@link GuidePage} type instead of just plain text. Purely client-side — the content is static
 * and shared, so unlike the Strain Data Pad this needs no server round-trip to open.
 *
 * <p>Every on-screen dimension below is the hand-authored layout (matched to the
 * {@code smokeleaf_guide.png} atlas — a 200x220 parchment page, 22x13 nav-arrow plaques, and 16x16
 * tab tiles for the rail, all sharing one leather/gold palette) multiplied by {@link #SCALE}, so the
 * whole assembly reads comfortably on a modern display instead of at the art's native pixel size.
 * Body/caption text that overflows its column scrolls (mouse wheel, plus a thumb drawn alongside it)
 * rather than being silently truncated.
 */
public class GuideBookScreen extends Screen {
    private static final ResourceLocation TEX = ResourceLocation.fromNamespaceAndPath(
            SmokeleafIndustries.MODID, "textures/gui/smokeleaf_guide.png");
    private static final int TEX_W = 256, TEX_H = 256;

    private static final float SCALE = 1.5F;
    private static int sc(double base) {
        return Math.round((float) (base * SCALE));
    }

    private static final int SRC_IMAGE_WIDTH = 200, SRC_IMAGE_HEIGHT = 220;
    private static final int IMAGE_WIDTH = sc(SRC_IMAGE_WIDTH), IMAGE_HEIGHT = sc(SRC_IMAGE_HEIGHT);
    private static final int PAGE_U = 0, PAGE_V = 0;

    private static final int PAGE_TEXT_X_OFFSET = sc(18);
    private static final int PAGE_TEXT_Y_OFFSET = sc(34);
    private static final int TEXT_WIDTH = sc(164);
    private static final int PAGE_BOTTOM_RESERVED = sc(38);
    private static final float TEXT_SCALE = 0.75F * SCALE;
    private static final int SCROLLBAR_GAP = sc(4);
    private static final int SCROLLBAR_WIDTH = sc(3);

    // The page-turn button sprites keep their native texture size (avoids sampling outside their
    // sprite in the source atlas, since UvImageButton doesn't support scaling destination
    // separately from source) — only their position on the page scales with everything else.
    private static final int BTN_W = 22, BTN_H = 13;

    private static final float TITLE_SCALE = 1.1F * SCALE;
    private static final int TITLE_EXTRA_Y = sc(10);
    private static final int TITLE_BODY_SPACING = sc(6);

    private static final int RAIL_WIDTH = sc(32);
    private static final int RAIL_GAP = sc(4);
    private static final int TAB_HEIGHT = sc(18);
    private static final int TAB_GAP = sc(1);
    private static final int TAB_TILE_SRC = 16;
    private static final int TAB_NORMAL_U = 204, TAB_NORMAL_V = 0;
    private static final int TAB_HOVER_U = 204, TAB_HOVER_V = 18;
    private static final int TAB_ACTIVE_U = 204, TAB_ACTIVE_V = 36;

    private static final int CONTROLS_GAP = sc(4);
    private static final int DONE_WIDTH = sc(84), DONE_HEIGHT = sc(20);
    private static final int BACK_WIDTH = sc(84), BACK_HEIGHT = sc(20);

    /** Item icons on a spotlight page are drawn at 16px times this zoom (independent of {@link #SCALE} until multiplied in here, so the icon grows with the rest of the page). */
    private static final float SPOTLIGHT_ICON_ZOOM = 3.0F * SCALE;

    private enum Mode { LIST, PAGE }

    private final List<GuideCategory> categories;
    private int categoryIndex = 0;
    private Mode mode = Mode.LIST;

    private GuideEntry selectedEntry;
    private int currentPage = 0;
    private int textScrollOffset = 0;

    private GuideEntryList entryList;
    private UvImageButton forwardButton;
    private UvImageButton backButton;
    private Button backToListButton;

    private Object cachedForPage;
    private List<FormattedCharSequence> cachedLines = List.of();

    public GuideBookScreen() {
        super(GameNarrator.NO_TITLE);
        this.categories = GuideDataLoader.categories();
    }

    public static void open() {
        Minecraft.getInstance().setScreen(new GuideBookScreen());
    }

    private int left() {
        return (this.width - (RAIL_WIDTH + RAIL_GAP + IMAGE_WIDTH)) / 2;
    }

    private int pageLeft() {
        return left() + RAIL_WIDTH + RAIL_GAP;
    }

    /** Vertically centers the whole book + its control row, instead of pinning it to the top of the screen. */
    private int top() {
        int totalHeight = IMAGE_HEIGHT + CONTROLS_GAP + DONE_HEIGHT;
        return Math.max(sc(2), (this.height - totalHeight) / 2);
    }

    @Override
    protected void init() {
        int pl = pageLeft();
        int top = top();
        // Back and Done share one control row below the book (instead of Back being pasted
        // over the page's own bottom-corner decoration): Back on the left, Done on the right,
        // both styled to match the book instead of vanilla's flat gray.
        int y = top + IMAGE_HEIGHT + CONTROLS_GAP;
        this.backToListButton = this.addRenderableWidget(
                Button.builder(Component.translatable("gui.smokeleafindustries.guide.back_to_list"), b -> showList())
                        .bounds(pl, y, BACK_WIDTH, BACK_HEIGHT).build(GuideBookButton::of));
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, b -> this.onClose())
                .bounds(pl + IMAGE_WIDTH - DONE_WIDTH, y, DONE_WIDTH, DONE_HEIGHT).build(GuideBookButton::of));

        int btnY = top + IMAGE_HEIGHT - BTN_H - sc(30);
        this.forwardButton = this.addRenderableWidget(new UvImageButton(pl + sc(106), btnY, BTN_W, BTN_H, 0, 222, 24, 222, TEX, TEX_W, TEX_H, b -> pageForward()));
        this.backButton = this.addRenderableWidget(new UvImageButton(pl + sc(72), btnY, BTN_W, BTN_H, 0, 236, 24, 236, TEX, TEX_W, TEX_H, b -> pageBack()));

        openCategory(categoryIndex);
    }

    private void openCategory(int index) {
        if (categories.isEmpty()) return;
        this.categoryIndex = Mth.clamp(index, 0, categories.size() - 1);
        showList();
    }

    private void showList() {
        this.mode = Mode.LIST;
        this.selectedEntry = null;
        rebuildEntryList();
        updateWidgetVisibility();
    }

    private void rebuildEntryList() {
        if (this.entryList != null) {
            removeWidget(this.entryList);
            this.entryList = null;
        }
        if (categories.isEmpty()) return;

        GuideCategory category = categories.get(categoryIndex);
        List<GuideEntry> entries = GuideDataLoader.entriesFor(category.id());
        int listX = pageLeft() + PAGE_TEXT_X_OFFSET - sc(4);
        int listY = top() + PAGE_TEXT_Y_OFFSET;
        int listW = TEXT_WIDTH + sc(8);
        int listH = IMAGE_HEIGHT - PAGE_TEXT_Y_OFFSET - sc(34);
        this.entryList = new GuideEntryList(minecraft, listX, listY, listW, listH, entries, this::openEntry);
        this.addRenderableWidget(this.entryList);
    }

    private void openEntry(GuideEntry entry) {
        this.selectedEntry = entry;
        this.currentPage = 0;
        this.mode = Mode.PAGE;
        this.cachedForPage = null;
        this.textScrollOffset = 0;
        updateWidgetVisibility();
    }

    private void updateWidgetVisibility() {
        boolean listMode = mode == Mode.LIST;
        if (entryList != null) entryList.visible = listMode;
        backToListButton.visible = !listMode;
        int pageCount = selectedEntry != null ? selectedEntry.pages().size() : 0;
        forwardButton.visible = !listMode && currentPage < pageCount - 1;
        backButton.visible = !listMode && currentPage > 0;
    }

    private void pageForward() {
        if (selectedEntry != null && currentPage < selectedEntry.pages().size() - 1) {
            currentPage++;
            cachedForPage = null;
            textScrollOffset = 0;
            updateWidgetVisibility();
        }
    }

    private void pageBack() {
        if (selectedEntry != null && currentPage > 0) {
            currentPage--;
            cachedForPage = null;
            textScrollOffset = 0;
            updateWidgetVisibility();
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (super.keyPressed(keyCode, scanCode, modifiers)) return true;
        if (mode == Mode.PAGE) {
            if (keyCode == 266) { pageBack(); return true; } // Page Up
            if (keyCode == 267) { pageForward(); return true; } // Page Down
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (mode == Mode.PAGE) {
            int pl = pageLeft();
            int top = top();
            if (mouseX >= pl && mouseX <= pl + IMAGE_WIDTH && mouseY >= top && mouseY <= top + IMAGE_HEIGHT) {
                textScrollOffset = Math.max(0, textScrollOffset - (int) Math.round(scrollY * 3));
                return true;
            }
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && !categories.isEmpty()) {
            int railX = left();
            int top = top();
            for (int i = 0; i < categories.size(); i++) {
                int y = top + i * (TAB_HEIGHT + TAB_GAP);
                if (mouseX >= railX && mouseX <= railX + RAIL_WIDTH && mouseY >= y && mouseY <= y + TAB_HEIGHT) {
                    if (i != categoryIndex || mode != Mode.LIST) openCategory(i);
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void renderBackground(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        this.renderTransparentBackground(g);
        g.blit(TEX, pageLeft(), top(), IMAGE_WIDTH, IMAGE_HEIGHT, (float) PAGE_U, (float) PAGE_V, SRC_IMAGE_WIDTH, SRC_IMAGE_HEIGHT, TEX_W, TEX_H);
        renderTabRail(g, mouseX, mouseY);
    }

    private void renderTabRail(GuiGraphics g, int mouseX, int mouseY) {
        if (categories.isEmpty()) return;
        int railX = left();
        int top = top();
        int railHeight = categories.size() * TAB_HEIGHT + Math.max(0, categories.size() - 1) * TAB_GAP;
        g.fill(railX - sc(2), top - sc(2), railX + RAIL_WIDTH + sc(2), top + railHeight + sc(2), 0xFF2E1B10);

        for (int i = 0; i < categories.size(); i++) {
            GuideCategory category = categories.get(i);
            int y = top + i * (TAB_HEIGHT + TAB_GAP);
            boolean active = i == categoryIndex;
            boolean hovered = !active && mouseX >= railX && mouseX <= railX + RAIL_WIDTH && mouseY >= y && mouseY <= y + TAB_HEIGHT;
            int u = active ? TAB_ACTIVE_U : (hovered ? TAB_HOVER_U : TAB_NORMAL_U);
            int v = active ? TAB_ACTIVE_V : (hovered ? TAB_HOVER_V : TAB_NORMAL_V);
            g.blit(TEX, railX, y, RAIL_WIDTH, TAB_HEIGHT, (float) u, (float) v, TAB_TILE_SRC, TAB_TILE_SRC, TEX_W, TEX_H);
            ItemStack tabIcon = GuideIcons.stack(category.icon(), null);
            g.renderItem(tabIcon, railX + (RAIL_WIDTH - 16) / 2, y + (TAB_HEIGHT - 16) / 2);
            if (hovered) {
                g.renderTooltip(font, category.name(), mouseX, mouseY);
            }
        }
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        super.render(g, mouseX, mouseY, partialTick);
        if (categories.isEmpty()) {
            g.drawCenteredString(font, "No guide content loaded", pageLeft() + IMAGE_WIDTH / 2, top() + IMAGE_HEIGHT / 2, 0xFF553311);
            return;
        }
        if (mode == Mode.PAGE && selectedEntry != null) {
            renderPage(g);
        } else {
            GuideCategory category = categories.get(categoryIndex);
            int titleWidth = font.width(category.name());
            g.drawString(font, category.name(), pageLeft() + PAGE_TEXT_X_OFFSET + TEXT_WIDTH / 2 - titleWidth / 2, top() + sc(14) + sc(4), 0x3F2E1E, false);
        }
    }

    private void renderPage(GuiGraphics g) {
        int pl = pageLeft();
        int top = top();

        Component header = selectedEntry.name();
        int headerWidth = font.width(header);
        g.drawString(font, header, pl + PAGE_TEXT_X_OFFSET + TEXT_WIDTH / 2 - headerWidth / 2, top + sc(14) + sc(4), 0x3F2E1E, false);

        List<GuidePage> pages = selectedEntry.pages();
        if (pages.isEmpty()) return;
        GuidePage page = pages.get(Mth.clamp(currentPage, 0, pages.size() - 1));

        if (page instanceof GuidePage.Text text) {
            renderTextPage(g, pl, top, text);
        } else if (page instanceof GuidePage.Spotlight spotlight) {
            renderSpotlightPage(g, pl, top, spotlight);
        } else if (page instanceof GuidePage.Crafting crafting) {
            renderRecipePage(g, pl, top, crafting.recipeId(), crafting.textKey());
        } else if (page instanceof GuidePage.Smelting smelting) {
            renderRecipePage(g, pl, top, smelting.recipeId(), smelting.textKey());
        } else if (page instanceof GuidePage.Image image) {
            renderImagePage(g, pl, top, image);
        } else if (page instanceof GuidePage.Combine combine) {
            renderCombinePage(g, pl, top, combine);
        }
    }

    private void renderTextPage(GuiGraphics g, int pl, int top, GuidePage.Text text) {
        boolean hasTitle = GuideText.has(text.titleKey());
        int bodyStartY;
        if (hasTitle) {
            Component title = Component.translatable(text.titleKey());
            int titleBaseY = top + PAGE_TEXT_Y_OFFSET + sc(2) + TITLE_EXTRA_Y;
            int centerX = pl + PAGE_TEXT_X_OFFSET + TEXT_WIDTH / 2;
            int titleWidth = font.width(title);
            g.pose().pushPose();
            g.pose().translate(centerX, titleBaseY, 0);
            g.pose().scale(TITLE_SCALE, TITLE_SCALE, 1.0F);
            g.drawString(font, title, -titleWidth / 2, 0, 0x000000, false);
            g.pose().popPose();
            bodyStartY = titleBaseY + Mth.floor(9 * TITLE_SCALE) + TITLE_BODY_SPACING;
        } else {
            bodyStartY = top + PAGE_TEXT_Y_OFFSET + sc(2);
        }

        if (!GuideText.has(text.textKey())) return;
        if (!text.equals(cachedForPage)) {
            Component body = GuideText.parseFormatted(text.textKey());
            cachedLines = font.split(body, Mth.floor(TEXT_WIDTH / TEXT_SCALE));
            cachedForPage = text;
        }
        int available = (top + IMAGE_HEIGHT - PAGE_BOTTOM_RESERVED) - bodyStartY;
        renderScrollableText(g, pl + PAGE_TEXT_X_OFFSET, bodyStartY, available, cachedLines);
    }

    private void renderSpotlightPage(GuiGraphics g, int pl, int top, GuidePage.Spotlight spotlight) {
        ItemStack stack = GuideIcons.stack(spotlight.item(), spotlight.strainId());
        int centerX = pl + PAGE_TEXT_X_OFFSET + TEXT_WIDTH / 2;
        int iconTop = top + PAGE_TEXT_Y_OFFSET + sc(6);
        int iconHalf = Math.round(8 * SPOTLIGHT_ICON_ZOOM);

        g.pose().pushPose();
        g.pose().translate(centerX - iconHalf, iconTop, 0);
        g.pose().scale(SPOTLIGHT_ICON_ZOOM, SPOTLIGHT_ICON_ZOOM, 1.0F);
        g.renderItem(stack, 0, 0);
        g.pose().popPose();

        if (GuideText.has(spotlight.textKey())) {
            renderCaption(g, pl, top, spotlight.textKey(), iconTop + Math.round(2 * iconHalf * 1.2F));
        }
    }

    private void renderRecipePage(GuiGraphics g, int pl, int top, ResourceLocation recipeId, String textKey) {
        Level level = this.minecraft.level;
        int gridX = pl + PAGE_TEXT_X_OFFSET + sc(4);
        int gridY = top + PAGE_TEXT_Y_OFFSET + sc(10);
        int slot = sc(18);

        Optional<RecipeHolder<?>> holder = level != null ? level.getRecipeManager().byKey(recipeId) : Optional.empty();
        if (holder.isEmpty()) {
            g.drawString(font, "(missing recipe: " + recipeId + ")", pl + PAGE_TEXT_X_OFFSET, gridY, 0xAA0000, false);
            return;
        }

        Recipe<?> recipe = holder.get().value();
        NonNullList<Ingredient> ingredients = recipe.getIngredients();
        int gw, gh;
        if (recipe instanceof ShapedRecipe shaped) {
            gw = shaped.getWidth();
            gh = shaped.getHeight();
        } else {
            gw = Math.max(1, Math.min(3, ingredients.size()));
            gh = Math.max(1, (int) Math.ceil(ingredients.size() / (double) gw));
        }

        long tick = System.currentTimeMillis() / 1000L;
        for (int i = 0; i < ingredients.size(); i++) {
            ItemStack[] options = ingredients.get(i).getItems();
            if (options.length == 0) continue;
            ItemStack shown = options[(int) (tick % options.length)];
            int col = i % gw, row = i / gw;
            g.renderItem(shown, gridX + col * slot, gridY + row * slot);
        }

        int arrowX = gridX + gw * slot + sc(4);
        int arrowY = gridY + (gh * slot) / 2 - sc(4);
        g.drawString(font, "->", arrowX, arrowY, 0x000000, false);

        ItemStack result = recipe.getResultItem(level.registryAccess());
        g.renderItem(result, arrowX + sc(16), gridY + (gh * slot) / 2 - sc(8));

        if (GuideText.has(textKey)) {
            renderCaption(g, pl, top, textKey, gridY + gh * slot + sc(8));
        }
    }

    private void renderCombinePage(GuiGraphics g, int pl, int top, GuidePage.Combine combine) {
        int gridX = pl + PAGE_TEXT_X_OFFSET + sc(4);
        int gridY = top + PAGE_TEXT_Y_OFFSET + sc(10);
        int slot = sc(18);

        List<ResourceLocation> inputs = combine.inputs();
        int gw = Math.max(1, Math.min(3, inputs.size()));
        int gh = Math.max(1, Mth.ceil(inputs.size() / (double) gw));

        for (int i = 0; i < inputs.size(); i++) {
            ItemStack shown = GuideIcons.stack(inputs.get(i), null);
            int col = i % gw, row = i / gw;
            g.renderItem(shown, gridX + col * slot, gridY + row * slot);
        }

        int arrowX = gridX + gw * slot + sc(4);
        int arrowY = gridY + (gh * slot) / 2 - sc(4);
        g.drawString(font, "->", arrowX, arrowY, 0x000000, false);

        ItemStack result = GuideIcons.stack(combine.output(), null);
        g.renderItem(result, arrowX + sc(16), gridY + (gh * slot) / 2 - sc(8));

        if (GuideText.has(combine.textKey())) {
            renderCaption(g, pl, top, combine.textKey(), gridY + gh * slot + sc(8));
        }
    }

    private void renderImagePage(GuiGraphics g, int pl, int top, GuidePage.Image image) {
        int size = sc(100);
        int x = pl + PAGE_TEXT_X_OFFSET + (TEXT_WIDTH - size) / 2;
        int y = top + PAGE_TEXT_Y_OFFSET + sc(4);
        float scale = size / 256F;

        g.pose().pushPose();
        g.pose().translate(x, y, 0);
        g.pose().scale(scale, scale, 1.0F);
        g.blit(image.image(), 0, 0, 0, 0, 256, 256, 256, 256);
        g.pose().popPose();

        if (GuideText.has(image.textKey())) {
            renderCaption(g, pl, top, image.textKey(), y + size + sc(8));
        }
    }

    private void renderCaption(GuiGraphics g, int pl, int top, String textKey, int startY) {
        Component caption = GuideText.parseFormatted(textKey);
        List<FormattedCharSequence> lines = font.split(caption, Mth.floor(TEXT_WIDTH / TEXT_SCALE));
        int available = (top + IMAGE_HEIGHT - PAGE_BOTTOM_RESERVED) - startY;
        renderScrollableText(g, pl + PAGE_TEXT_X_OFFSET, startY, available, lines);
    }

    /**
     * Draws a (possibly long) block of pre-wrapped lines clipped to {@code availableHeight}, scrolled
     * by {@link #textScrollOffset}, with a thumb-and-track scrollbar drawn alongside it whenever the
     * text doesn't fit — this is what replaces the old silent-truncation-at-a-fixed-line-count behavior.
     */
    private void renderScrollableText(GuiGraphics g, int x, int y, int availableHeight, List<FormattedCharSequence> lines) {
        if (availableHeight <= 0 || lines.isEmpty()) return;
        int lineHeight = Math.max(1, Mth.ceil(9 * TEXT_SCALE));
        int visibleLines = Math.max(1, availableHeight / lineHeight);
        int maxScroll = Math.max(0, lines.size() - visibleLines);
        textScrollOffset = Mth.clamp(textScrollOffset, 0, maxScroll);

        g.enableScissor(x, y, x + TEXT_WIDTH, y + availableHeight);
        g.pose().pushPose();
        g.pose().translate(x, y, 0);
        g.pose().scale(TEXT_SCALE, TEXT_SCALE, 1.0F);
        int end = Math.min(lines.size(), textScrollOffset + visibleLines + 1);
        for (int l = textScrollOffset; l < end; l++) {
            g.drawString(font, lines.get(l), 0, (l - textScrollOffset) * 9, 0x000000, false);
        }
        g.pose().popPose();
        g.disableScissor();

        if (maxScroll > 0) {
            int barX = x + TEXT_WIDTH + SCROLLBAR_GAP;
            g.fill(barX, y, barX + SCROLLBAR_WIDTH, y + availableHeight, 0x40000000);
            int thumbH = Math.max(sc(10), availableHeight * visibleLines / lines.size());
            int thumbY = y + (availableHeight - thumbH) * textScrollOffset / maxScroll;
            g.fill(barX, thumbY, barX + SCROLLBAR_WIDTH, thumbY + thumbH, 0xFFC89E4C);
        }
    }
}
