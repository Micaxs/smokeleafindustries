package net.micaxs.smokeleaf.screen.renderer;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraftforge.energy.IEnergyStorage;
import java.util.List;

public class EnergyDisplayTooltipArea {

    private final int xPos;
    private final int yPos;
    private final int width;
    private final int height;
    private final IEnergyStorage energy;

    public EnergyDisplayTooltipArea(int xMin, int yMin, IEnergyStorage energy) {
        this(xMin, yMin, energy, 8, 64);
    }

    public EnergyDisplayTooltipArea(int xMin, int yMin, IEnergyStorage energy, int width, int height) {
        xPos = xMin;
        yPos = yMin;
        this.energy = energy;
        this.width = width;
        this.height = height;
    }

    public List<Component> getTooltips() {
        return List.of(Component.literal(energy.getEnergyStored() + " / " + energy.getMaxEnergyStored() + " FE"));
    }

    public void render(GuiGraphics guiGraphics) {
        int stored = (int)(height * (energy.getEnergyStored() / (float)energy.getMaxEnergyStored()));
        guiGraphics.fillGradient(xPos, yPos + (height - stored), xPos + width, yPos + height, 0xffb51500, 0xff600b00);
    }


}
