package net.micaxs.smokeleaf.screen.custom;

import com.mojang.blaze3d.systems.RenderSystem;
import net.micaxs.smokeleaf.SmokeleafIndustries;
import net.micaxs.smokeleaf.screen.renderer.EnergyDisplayTooltipArea;
import net.micaxs.smokeleaf.screen.renderer.FluidTankRenderer;
import net.micaxs.smokeleaf.utils.MouseUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.List;
import java.util.Optional;

public class MixerScreen extends AbstractContainerScreen<MixerMenu> {

    public static final ResourceLocation GUI_TEXTURE = ResourceLocation.fromNamespaceAndPath(SmokeleafIndustries.MODID, "textures/gui/mixer/mixer_gui.png");
    private static final ResourceLocation INFO_ICON = ResourceLocation.fromNamespaceAndPath(SmokeleafIndustries.MODID, "textures/gui/icons/info.png");
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
    protected void renderBg(GuiGraphics g, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.setShaderTexture(0, GUI_TEXTURE);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        g.blit(GUI_TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        energyInfoArea.render(g);

        aRenderer.render(g, x + 8, y + 11, menu.blockEntity.getFluidA());
        bRenderer.render(g, x + 29, y + 11, menu.blockEntity.getFluidB());
        outRenderer.render(g, x + 134, y + 11, menu.blockEntity.getFluidOut());

        if (menu.isCrafting()) {
            g.blit(GUI_TEXTURE, x + 76, y + 39, 0, 166, menu.getScaledProgress(), 7);
        }

        renderInfoIcon(g, x, y);
    }

    private void renderInfoIcon(GuiGraphics g, int baseX, int baseY) {
        int ix = baseX + ICON_SIZE;
        int iy = baseY + ICON_SIZE;
        g.blit(INFO_ICON, ix, iy, 0, 0, ICON_SIZE, ICON_SIZE, ICON_SIZE, ICON_SIZE);
    }

    private void renderInfoIconTooltip(GuiGraphics g, int mouseX, int mouseY, int baseX, int baseY) {
        if (MouseUtil.isMouseOver(mouseX, mouseY, baseX + ICON_SIZE, baseY + ICON_SIZE, ICON_SIZE, ICON_SIZE)) {
            Component info = Component.translatable("gui.tooltip.mixer.info");
            List<FormattedCharSequence> wrapped = this.font.split(info, 300);
            g.renderTooltip(this.font, wrapped, mouseX, mouseY);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics g, int mouseX, int mouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        if (MouseUtil.isMouseOver(mouseX, mouseY, x + 156, y + 11, 8, 64)) {
            g.renderTooltip(this.font, energyInfoArea.getTooltips(), Optional.empty(), mouseX - x, mouseY - y);
        }

        renderFluidTooltip(g, mouseX, mouseY, x, y, 8, 11, aRenderer, menu.blockEntity.getFluidA());
        renderFluidTooltip(g, mouseX, mouseY, x, y, 29, 11, bRenderer, menu.blockEntity.getFluidB());
        renderFluidTooltip(g, mouseX, mouseY, x, y, 134, 11, outRenderer, menu.blockEntity.getFluidOut());
    }

    private void renderFluidTooltip(GuiGraphics g, int mouseX, int mouseY, int baseX, int baseY, int offX, int offY, FluidTankRenderer r, FluidStack stack) {
        if (MouseUtil.isMouseOver(mouseX, mouseY, baseX + offX, baseY + offY, r.getWidth(), r.getHeight())) {
            g.renderTooltip(this.font, r.getTooltip(stack, TooltipFlag.Default.NORMAL), Optional.empty(), mouseX - baseX, mouseY - baseY);
        }
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        super.render(g, mouseX, mouseY, partialTick);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        renderInfoIconTooltip(g, mouseX, mouseY, x, y);

        renderTooltip(g, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
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

        return super.mouseClicked(mouseX, mouseY, button);
    }
}
