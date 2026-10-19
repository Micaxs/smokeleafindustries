package net.micaxs.smokeleaf.screen.custom;

import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import com.mojang.blaze3d.systems.RenderSystem;
import net.micaxs.smokeleaf.SmokeleafIndustries;
import net.micaxs.smokeleaf.screen.renderer.EnergyDisplayTooltipArea;
import net.micaxs.smokeleaf.screen.renderer.FluidTankRenderer;
import net.micaxs.smokeleaf.utils.MouseUtil;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.List;
import java.util.Optional;

public class GummyMachineScreen extends AbstractContainerScreen<GummyMachineMenu> {

    public static final Identifier GUI_TEXTURE = Identifier.fromNamespaceAndPath(SmokeleafIndustries.MODID, "textures/gui/gummy_machine/gummy_machine_gui.png");
    private EnergyDisplayTooltipArea energyInfoArea;
    private FluidTankRenderer fluidRenderer;

    private static final Identifier INFO_ICON = Identifier.fromNamespaceAndPath(SmokeleafIndustries.MODID, "textures/gui/icons/info.png");
    private static final int ICON_SIZE = 8;

    public GummyMachineScreen(GummyMachineMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void init() {
        super.init();
        this.inventoryLabelY = 100000;
        this.titleLabelY = 100000;

        assignEnergyInfoArea();
        assignFluidRenderer();
    }

    private void assignFluidRenderer() {
        fluidRenderer = new FluidTankRenderer(8000, true, 16, 64);
    }

    private void renderFluidTooltipArea(GuiGraphicsExtractor guiGraphics, int pMouseX, int pMouseY, int x, int y, FluidStack stack, int offsetX, int offsetY, FluidTankRenderer renderer) {
        if (isMouseAboveArea(pMouseX, pMouseY, x, y, offsetX, offsetY, renderer)) {
            guiGraphics.setTooltipForNextFrame(this.font, renderer.getTooltip(stack, TooltipFlag.Default.NORMAL), Optional.empty(), pMouseX, pMouseY);
        }
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor guiGraphics, int i, int i1, float v) {
        super.extractBackground(guiGraphics, i, i1, v);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, GUI_TEXTURE, x, y, 0, 0, imageWidth, imageHeight, 256, 256);

        energyInfoArea.render(guiGraphics);
        fluidRenderer.render(guiGraphics, x + 8, y + 11, menu.blockEntity.getFluid());
        renderProgressBar(guiGraphics, x, y);
        renderInfoIcon(guiGraphics, x, y);
    }

    private static final int INFO_ICON_X = 44;
    private static final int INFO_ICON_Y = 6;

    private void renderInfoIcon(GuiGraphicsExtractor g, int baseX, int baseY) {
        int ix = baseX + INFO_ICON_X;
        int iy = baseY + INFO_ICON_Y;
        g.blit(RenderPipelines.GUI_TEXTURED, INFO_ICON, ix, iy, 0, 0, ICON_SIZE, ICON_SIZE, ICON_SIZE, ICON_SIZE);
    }

    private void renderInfoIconTooltip(GuiGraphicsExtractor g, int mouseX, int mouseY, int baseX, int baseY) {
        if (isMouseAboveAreaEnergy(mouseX, mouseY, baseX, baseY, INFO_ICON_X, INFO_ICON_Y, ICON_SIZE, ICON_SIZE)) {
            Component info = Component.translatable("gui.tooltip.gummy_machine.info");
            List<FormattedCharSequence> wrapped = this.font.split(info, 300);
            g.setTooltipForNextFrame(this.font, wrapped, mouseX, mouseY);
        }
    }

    private void renderProgressBar(GuiGraphicsExtractor guiGraphics, int x, int y) {
        if (menu.isCrafting()) {
            // Horizontal fill between the mold/catalyst slots and the output slot, matching
            // the other machines' horizontal progress-arrow convention.
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, GUI_TEXTURE, x + 75, y + 39, 0, 166, menu.getScaledProgress(), 7, 256, 256);
        }
    }

    private void assignEnergyInfoArea() {
        energyInfoArea = new EnergyDisplayTooltipArea(((width - imageWidth) / 2) + 156, ((height - imageHeight) / 2) + 11, menu.blockEntity.getEnergyStorage(null), 8, 64);
    }

    private void renderEnergyInfoArea(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, int x, int y) {
        if (isMouseAboveAreaEnergy(mouseX, mouseY, x, y, 152, 11, 8, 64)) {
            guiGraphics.setTooltipForNextFrame(this.font, energyInfoArea.getTooltips(), Optional.empty(), mouseX, mouseY);
        }
    }

    private boolean isMouseAboveAreaEnergy(int mouseX, int mouseY, int x, int y, int offsetX, int offsetY, int width, int height) {
        return MouseUtil.isMouseOver(mouseX, mouseY, x + offsetX, y + offsetY, width, height);
    }

    private boolean isMouseAboveArea(int mouseX, int mouseY, int x, int y, int offsetX, int offsetY, FluidTankRenderer renderer) {
        return MouseUtil.isMouseOver(mouseX, mouseY, x + offsetX, y + offsetY, renderer.getWidth(), renderer.getHeight());
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        double mouseX = event.x();
        double mouseY = event.y();
        int button = event.button();
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        if (fluidRenderer != null && isMouseAboveArea((int) mouseX, (int) mouseY, x, y, 8, 11, fluidRenderer)) {
            if (this.minecraft != null && this.minecraft.gameMode != null) {
                if (button == 0) {
                    this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId,
                            GummyMachineMenu.BUTTON_FILL_FROM_BUCKET);
                    return true;
                }
                if (button == 1) {
                    this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId,
                            GummyMachineMenu.BUTTON_DRAIN_TO_BUCKET);
                    return true;
                }
            }
        }
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        renderEnergyInfoArea(guiGraphics, mouseX, mouseY, x, y);
        renderFluidTooltipArea(guiGraphics, mouseX, mouseY, x, y, menu.blockEntity.getFluid(), 8, 11, fluidRenderer);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(guiGraphics, mouseX, mouseY, partialTick);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        renderInfoIconTooltip(guiGraphics, mouseX, mouseY, x, y);
    }
}
