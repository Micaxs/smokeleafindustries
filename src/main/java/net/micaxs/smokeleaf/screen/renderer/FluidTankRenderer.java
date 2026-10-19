package net.micaxs.smokeleaf.screen.renderer;

import com.google.common.base.Preconditions;
import net.micaxs.smokeleaf.SmokeleafIndustries;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.block.FluidModel;
import net.neoforged.neoforge.client.fluid.FluidTintSource;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class FluidTankRenderer {
    private static final NumberFormat nf = NumberFormat.getIntegerInstance();
    private static final int TEXTURE_SIZE = 16;
    private static final int MIN_FLUID_HEIGHT = 1; // ensure tiny amounts of fluid are still visible

    private final long capacity;
    private final TooltipMode tooltipMode;
    private final int width;
    private final int height;

    enum TooltipMode {
        SHOW_AMOUNT,
        SHOW_AMOUNT_AND_CAPACITY,
        ITEM_LIST
    }

    public FluidTankRenderer(long capacity, boolean showCapacity, int width, int height) {
        this(capacity, showCapacity ? TooltipMode.SHOW_AMOUNT_AND_CAPACITY : TooltipMode.SHOW_AMOUNT, width, height);
    }

    private FluidTankRenderer(long capacity, TooltipMode tooltipMode, int width, int height) {
        Preconditions.checkArgument(capacity > 0, "capacity must be > 0");
        Preconditions.checkArgument(width > 0, "width must be > 0");
        Preconditions.checkArgument(height > 0, "height must be > 0");

        this.capacity = capacity;
        this.tooltipMode = tooltipMode;
        this.width = width;
        this.height = height;
    }

    public void render(GuiGraphicsExtractor guiGraphics, int x, int y, FluidStack fluidStack) {
        guiGraphics.pose().pushMatrix();
        {
            guiGraphics.pose().translate(x, y);
            drawFluid(guiGraphics, width, height, fluidStack);
        }
        guiGraphics.pose().popMatrix();
    }

    private void drawFluid(GuiGraphicsExtractor guiGraphics, final int width, final int height, FluidStack fluidStack) {
        Fluid fluid = fluidStack.getFluid();
        if (fluid.isSame(Fluids.EMPTY)) {
            return;
        }

        TextureAtlasSprite fluidStillSprite = getStillFluidSprite(fluidStack);
        int fluidColor = getColorTint(fluidStack);

        long amount = fluidStack.getAmount();
        long scaledAmount = (amount * height) / capacity;

        if (amount > 0 && scaledAmount < MIN_FLUID_HEIGHT) {
            scaledAmount = MIN_FLUID_HEIGHT;
        }
        if (scaledAmount > height) {
            scaledAmount = height;
        }

        drawTiledSprite(guiGraphics, width, height, fluidColor, scaledAmount, fluidStillSprite);
    }

    private static FluidModel fluidModel(FluidStack fluidStack) {
        return Minecraft.getInstance().getModelManager().getFluidStateModelSet().get(fluidStack.getFluid().defaultFluidState());
    }

    private TextureAtlasSprite getStillFluidSprite(FluidStack fluidStack) {
        return fluidModel(fluidStack).stillMaterial().sprite();
    }

    private int getColorTint(FluidStack ingredient) {
        // The fluid model's tint source can compute the tint from the FluidStack (e.g. strain color).
        FluidTintSource tint = fluidModel(ingredient).fluidTintSource();
        return tint != null ? tint.colorAsStack(ingredient) : 0xFFFFFFFF;
    }

    /** Fills the bottom {@code scaledAmount} px of the tank with 16x16 tiles of the fluid sprite. */
    private static void drawTiledSprite(GuiGraphicsExtractor guiGraphics, final int tiledWidth, final int tiledHeight, int color, long scaledAmount, TextureAtlasSprite sprite) {
        int top = tiledHeight - (int) scaledAmount;
        guiGraphics.enableScissor(0, top, tiledWidth, tiledHeight);
        for (int x = 0; x < tiledWidth; x += TEXTURE_SIZE) {
            for (int y = tiledHeight - TEXTURE_SIZE; y > top - TEXTURE_SIZE; y -= TEXTURE_SIZE) {
                guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, x, y, TEXTURE_SIZE, TEXTURE_SIZE, color);
            }
        }
        guiGraphics.disableScissor();
    }

    public List<Component> getTooltip(FluidStack fluidStack, TooltipFlag tooltipFlag) {
        List<Component> tooltip = new ArrayList<>();

        Fluid fluidType = fluidStack.getFluid();
        try {
            if (fluidType.isSame(Fluids.EMPTY)) {
                tooltip.add(Component.literal("Empty"));
                tooltip.add(Component.translatable("smokeleafindusties.tooltip.liquid.amount.with.capacity", 0, nf.format(capacity)).withStyle(ChatFormatting.GRAY));
                return tooltip;
            }

            Component displayName = fluidStack.getHoverName();
            tooltip.add(displayName);

            long amount = fluidStack.getAmount();
            long milliBuckets = (amount * 1000) / FluidType.BUCKET_VOLUME;

            if (tooltipMode == TooltipMode.SHOW_AMOUNT_AND_CAPACITY) {
                MutableComponent amountString = Component.translatable("smokeleafindusties.tooltip.liquid.amount.with.capacity", nf.format(milliBuckets), nf.format(capacity));
                tooltip.add(amountString.withStyle(ChatFormatting.GRAY));
            } else if (tooltipMode == TooltipMode.SHOW_AMOUNT) {
                MutableComponent amountString = Component.translatable("smokeleafindusties.tooltip.liquid.amount", nf.format(milliBuckets));
                tooltip.add(amountString.withStyle(ChatFormatting.GRAY));
            }
        } catch (RuntimeException e) {
            SmokeleafIndustries.LOGGER.error("Failed to get tooltip for fluid: " + e);
        }

        return tooltip;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}