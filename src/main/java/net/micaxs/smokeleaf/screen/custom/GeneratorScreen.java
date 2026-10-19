package net.micaxs.smokeleaf.screen.custom;

import net.minecraft.client.renderer.RenderPipelines;
import com.mojang.blaze3d.systems.RenderSystem;
import net.micaxs.smokeleaf.SmokeleafIndustries;
import net.micaxs.smokeleaf.screen.renderer.EnergyDisplayTooltipArea;
import net.micaxs.smokeleaf.utils.MouseUtil;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;

import java.util.List;
import java.util.Optional;

public class GeneratorScreen extends AbstractContainerScreen<GeneratorMenu> {
    public static final Identifier GUI_TEXTURE = Identifier.fromNamespaceAndPath(SmokeleafIndustries.MODID, "textures/gui/generator/generator_gui.png");
    public static final Identifier LIT_PROGRESS_TEXTURE = Identifier.fromNamespaceAndPath("minecraft", "container/furnace/lit_progress");
    private EnergyDisplayTooltipArea energyInfoArea;

    private static final Identifier INFO_ICON = Identifier.fromNamespaceAndPath(SmokeleafIndustries.MODID, "textures/gui/icons/info.png");
    private static final int ICON_SIZE = 8;
    private static final float TOOLTIP_SCALE = 0.5f;

    public GeneratorScreen(GeneratorMenu menu, Inventory playerInvetory, Component title) {
        super(menu, playerInvetory, title);
    }


    @Override
    protected void init() {
        super.init();
        this.inventoryLabelY = 100000;
        this.titleLabelY = 100000;

        assignEnergyInfoArea();
    }


    private void renderEnergyInfoArea(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, int x, int y) {
        if (isMouseAboveArea(mouseX, mouseY, x, y, 125, 21, 13, 38)) {
            guiGraphics.setTooltipForNextFrame(this.font, energyInfoArea.getTooltips(), Optional.empty(), mouseX, mouseY);
        }
    }

    private void assignEnergyInfoArea() {
        energyInfoArea = new EnergyDisplayTooltipArea(((width - imageWidth) / 2) + 125, ((height - imageHeight) / 2) + 21, menu.blockEntity.getEnergyStorage(null), 13, 38);
    }


    @Override
    public void extractBackground(GuiGraphicsExtractor guiGraphics, int i, int i1, float v) {
        super.extractBackground(guiGraphics, i, i1, v);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, GUI_TEXTURE, x, y, 0, 0, imageWidth, imageHeight, 256, 256);

        energyInfoArea.render(guiGraphics);
        renderFuelProgress(guiGraphics, x, y);
        renderInfoIcon(guiGraphics, x, y);
    }


    private void renderInfoIcon(GuiGraphicsExtractor g, int baseX, int baseY) {
        int ix = baseX + ICON_SIZE;
        int iy = baseY + ICON_SIZE;
        // full 16x16 texture
        g.blit(RenderPipelines.GUI_TEXTURED, INFO_ICON, ix, iy, 0, 0, ICON_SIZE, ICON_SIZE, ICON_SIZE, ICON_SIZE);
    }

    private void renderInfoIconTooltip(GuiGraphicsExtractor g, int mouseX, int mouseY, int baseX, int baseY) {
        if (isMouseAboveArea(mouseX, mouseY, baseX, baseY, ICON_SIZE, ICON_SIZE, ICON_SIZE, ICON_SIZE)) {
            Component info = Component.translatable("gui.tooltip.generator.info");
            List<FormattedCharSequence> wrapped = this.font.split(info, 300);
            g.setTooltipForNextFrame(this.font, wrapped, mouseX, mouseY);
        }
    }

    private void renderFuelProgress(GuiGraphicsExtractor guiGraphics, int x, int y) {
        int scaledHeight = menu.getFuelStoredScaled();
        if (scaledHeight > 0) {
            int adjustedY = y + 38 + (11 - scaledHeight);
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, GUI_TEXTURE, x + 81, adjustedY, 176, 14 - scaledHeight, 14, scaledHeight, 256, 256);
        }
    }


//    private void renderFuelBurning(GuiGraphicsExtractor guiGraphics, int x, int y) {
//        if (this.menu.isBurning()) {
//            int l = Mth.ceil(this.menu.getFuelProgress() * 13.0F) + 1;
//            guiGraphics.blitSprite(LIT_PROGRESS_TEXTURE, 14, 14, 0, 14 - l, x + 80, y + 18 + 14 -l, 14, l);
//        }
//    }

    private boolean isMouseAboveArea(int mouseX, int mouseY, int x, int y, int offsetX, int offsetY, int width, int height) {
        return MouseUtil.isMouseOver(mouseX, mouseY, x + offsetX, y + offsetY, width, height);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        renderEnergyInfoArea(guiGraphics, mouseX, mouseY, x, y);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(guiGraphics, mouseX, mouseY, partialTick);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        renderInfoIconTooltip(guiGraphics, mouseX, mouseY, x, y);
    }
}
