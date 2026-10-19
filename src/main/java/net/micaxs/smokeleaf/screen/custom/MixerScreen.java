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

public class MixerScreen extends AbstractContainerScreen<MixerMenu> {

    public static final Identifier GUI_TEXTURE = Identifier.fromNamespaceAndPath(SmokeleafIndustries.MODID, "textures/gui/mixer/mixer_gui.png");
    private static final Identifier INFO_ICON = Identifier.fromNamespaceAndPath(SmokeleafIndustries.MODID, "textures/gui/icons/info.png");
    private static final int ICON_SIZE = 8;

    private EnergyDisplayTooltipArea energyInfoArea;
    private FluidTankRenderer aRenderer;
    private FluidTankRenderer bRenderer;
    private FluidTankRenderer outRenderer;

    public MixerScreen(MixerMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
    }

    @Override
    protected void init() {
        super.init();
        this.inventoryLabelY = 100000;
        this.titleLabelY = 100000;

        energyInfoArea = new EnergyDisplayTooltipArea(((width - imageWidth) / 2) + 156, ((height - imageHeight) / 2) + 11,
                menu.blockEntity.getEnergyStorage(null), 8, 64);

        aRenderer = new FluidTankRenderer(4000, true, 16, 64);
        bRenderer = new FluidTankRenderer(4000, true, 16, 64);
        outRenderer = new FluidTankRenderer(8000, true, 16, 64);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor g, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(g, mouseX, mouseY, partialTick);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        g.blit(RenderPipelines.GUI_TEXTURED, GUI_TEXTURE, x, y, 0, 0, imageWidth, imageHeight, 256, 256);

        energyInfoArea.render(g);

        aRenderer.render(g, x + 8, y + 11, menu.blockEntity.getFluidA());
        bRenderer.render(g, x + 29, y + 11, menu.blockEntity.getFluidB());
        outRenderer.render(g, x + 134, y + 11, menu.blockEntity.getFluidOut());

        if (menu.isCrafting()) {
            g.blit(RenderPipelines.GUI_TEXTURED, GUI_TEXTURE, x + 76, y + 39, 0, 166, menu.getScaledProgress(), 7, 256, 256);
        }

        renderInfoIcon(g, x, y);
    }

    private void renderInfoIcon(GuiGraphicsExtractor g, int baseX, int baseY) {
        int ix = baseX + ICON_SIZE;
        int iy = baseY + ICON_SIZE;
        g.blit(RenderPipelines.GUI_TEXTURED, INFO_ICON, ix, iy, 0, 0, ICON_SIZE, ICON_SIZE, ICON_SIZE, ICON_SIZE);
    }

    private void renderInfoIconTooltip(GuiGraphicsExtractor g, int mouseX, int mouseY, int baseX, int baseY) {
        if (MouseUtil.isMouseOver(mouseX, mouseY, baseX + ICON_SIZE, baseY + ICON_SIZE, ICON_SIZE, ICON_SIZE)) {
            Component info = Component.translatable("gui.tooltip.mixer.info");
            List<FormattedCharSequence> wrapped = this.font.split(info, 300);
            g.setTooltipForNextFrame(this.font, wrapped, mouseX, mouseY);
        }
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor g, int mouseX, int mouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        if (MouseUtil.isMouseOver(mouseX, mouseY, x + 156, y + 11, 8, 64)) {
            g.setTooltipForNextFrame(this.font, energyInfoArea.getTooltips(), Optional.empty(), mouseX, mouseY);
        }

        renderFluidTooltip(g, mouseX, mouseY, x, y, 8, 11, aRenderer, menu.blockEntity.getFluidA());
        renderFluidTooltip(g, mouseX, mouseY, x, y, 29, 11, bRenderer, menu.blockEntity.getFluidB());
        renderFluidTooltip(g, mouseX, mouseY, x, y, 134, 11, outRenderer, menu.blockEntity.getFluidOut());
    }

    private void renderFluidTooltip(GuiGraphicsExtractor g, int mouseX, int mouseY, int baseX, int baseY, int offX, int offY, FluidTankRenderer r, FluidStack stack) {
        if (MouseUtil.isMouseOver(mouseX, mouseY, baseX + offX, baseY + offY, r.getWidth(), r.getHeight())) {
            g.setTooltipForNextFrame(this.font, r.getTooltip(stack, TooltipFlag.Default.NORMAL), Optional.empty(), mouseX, mouseY);
        }
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor g, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(g, mouseX, mouseY, partialTick);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        renderInfoIconTooltip(g, mouseX, mouseY, x, y);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        double mouseX = event.x();
        double mouseY = event.y();
        int button = event.button();
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        // Allow clicking any of the tank areas.
        boolean overA = aRenderer != null && MouseUtil.isMouseOver((int) mouseX, (int) mouseY, x + 8, y + 11, aRenderer.getWidth(), aRenderer.getHeight());
        boolean overB = bRenderer != null && MouseUtil.isMouseOver((int) mouseX, (int) mouseY, x + 29, y + 11, bRenderer.getWidth(), bRenderer.getHeight());
        boolean overOut = outRenderer != null && MouseUtil.isMouseOver((int) mouseX, (int) mouseY, x + 134, y + 11, outRenderer.getWidth(), outRenderer.getHeight());

        if (overA || overB || overOut) {
            if (this.minecraft != null && this.minecraft.gameMode != null) {
                if (button == 0) {
                    if (overA) {
                        this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, MixerMenu.BUTTON_FILL_INPUT_A_FROM_BUCKET);
                        return true;
                    }
                    if (overB) {
                        this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, MixerMenu.BUTTON_FILL_INPUT_B_FROM_BUCKET);
                        return true;
                    }
                }
                if (button == 1) {
                    if (overOut) {
                        this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, MixerMenu.BUTTON_DRAIN_OUTPUT_TO_BUCKET);
                        return true;
                    }
                    // right-clicking inputs does nothing (inputs are not drained via GUI)
                }
            }
        }

        return super.mouseClicked(event, doubleClick);
    }
}
