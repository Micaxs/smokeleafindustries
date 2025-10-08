package net.micaxs.smokeleaf.screen.custom;

import net.micaxs.smokeleaf.SmokeleafIndustries;
import net.micaxs.smokeleaf.block.custom.BaseWeedCropBlock;
import net.micaxs.smokeleaf.block.entity.BaseWeedCropBlockEntity;
import net.micaxs.smokeleaf.utils.MouseUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.Optional;

public class MagnifyingGlassScreen extends Screen {
    private static final ResourceLocation BG_TEXTURE = ResourceLocation.fromNamespaceAndPath(SmokeleafIndustries.MODID, "textures/gui/analyzer/analyzer_gui.png"); // 256x256
    private static final int TEX_W = 256, TEX_H = 256;
    private static final int GUI_W = 176, GUI_H = 142;


    // Bar Textures
    private static final int BAR_W = 141, BAR_H = 5;
    private static final int BAR_U = 0;
    private static final int NITROGEN_BAR_BG_TEXTURE_Y = 236, NITROGEN_BAR_FG_TEXTURE_Y = 241;
    private static final int PHOSPHORUS_BAR_BG_TEXTURE_Y = 216, PHOSPHORUS_BAR_FG_TEXTURE_Y = 221;
    private static final int POTASSIUM_BAR_BG_TEXTURE_Y = 246, POTASSIUM_BAR_FG_TEXTURE_Y = 251;

    // Back Button texture positioning with hover
    private static final int BUTTON_W = 11, BUTTON_H = 10;
    private static final int BUTTON_U = 176, BUTTON_V = 0;
    private static final int BUTTON_HOVER_U = 187, BUTTON_HOVER_V = 0;

    // Nutrient Icon texture positioning
    private static final int ICON_SIZE = 12;
    private static final int NITROGEN_ICON_U = 176, NITROGEN_ICON_V = 10;
    private static final int PHOSPHORUS_ICON_U = 176, PHOSPHORUS_ICON_V = 22;
    private static final int POTASSIUM_ICON_U = 176, POTASSIUM_ICON_V = 34;

    // Values
    private static final int MAX_NUTRIENT = 25;

    private final BlockPos pos;

    public MagnifyingGlassScreen(BlockPos pos) {
        super(Component.literal("Plant Analyzer"));
        this.pos = pos;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        super.init();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        // Centered GUI placement
        int left = (this.width - GUI_W) / 2;
        int top = (this.height - GUI_H) / 2;

        // Draw the 176x142 GUI region from the top-left of the 256x256 texture
        guiGraphics.blit(BG_TEXTURE, left, top, 0, 0, GUI_W, GUI_H, TEX_W, TEX_H);


        // Draw "Nutrients" Title Text
        drawScaledCentered(guiGraphics, Component.literal("Nutrients"), this.width / 2, top + 10, 0xFFFFFF, 1.0f, false);

        // Draw Nutrient Icons
        int nitrogen_x = left + 8, nitrogen_y = top + 26;
        int phosphorus_x = left + 8, phosphorus_y = top + 51;
        int potassium_x = left + 8, potassium_y = top + 76;
        guiGraphics.blit(BG_TEXTURE, nitrogen_x, nitrogen_y, NITROGEN_ICON_U, NITROGEN_ICON_V, ICON_SIZE, ICON_SIZE, TEX_W, TEX_H);
        guiGraphics.blit(BG_TEXTURE, phosphorus_x, phosphorus_y, PHOSPHORUS_ICON_U, PHOSPHORUS_ICON_V, ICON_SIZE, ICON_SIZE, TEX_W, TEX_H);
        guiGraphics.blit(BG_TEXTURE, potassium_x, potassium_y, POTASSIUM_ICON_U, POTASSIUM_ICON_V, ICON_SIZE, ICON_SIZE, TEX_W, TEX_H);

        // Draw Bars Background
        int nitrogen_bar_x = left + 26, nitrogen_bar_y = top + 36;
        int phosphorus_bar_x = left + 26, phosphorus_bar_y = top + 61;
        int potassium_bar_x = left + 26, potassium_bar_y = top + 86;
        guiGraphics.blit(BG_TEXTURE, nitrogen_bar_x, nitrogen_bar_y, BAR_U, NITROGEN_BAR_BG_TEXTURE_Y, BAR_W, BAR_H, TEX_W, TEX_H);
        guiGraphics.blit(BG_TEXTURE, phosphorus_bar_x, phosphorus_bar_y, BAR_U, PHOSPHORUS_BAR_BG_TEXTURE_Y, BAR_W, BAR_H, TEX_W, TEX_H);
        guiGraphics.blit(BG_TEXTURE, potassium_bar_x, potassium_bar_y, BAR_U, POTASSIUM_BAR_BG_TEXTURE_Y, BAR_W, BAR_H, TEX_W, TEX_H);

        // Draw Text Labels
        guiGraphics.drawString(this.font, Component.literal("Nitrogen"), nitrogen_bar_x + 1, nitrogen_bar_y - 10, 0xBBBBBB, true);
        guiGraphics.drawString(this.font, Component.literal("Phosphorus"), phosphorus_bar_x + 1, phosphorus_bar_y - 10, 0xBBBBBB, true);
        guiGraphics.drawString(this.font, Component.literal("Potassium"), potassium_bar_x + 1, potassium_bar_y - 9, 0xBBBBBB, true);


        Level level = Minecraft.getInstance().level;
        BlockPos bePos = this.pos;
        if (level != null) {
            BlockState stateAtPos = level.getBlockState(this.pos);
            if (stateAtPos.getBlock() instanceof BaseWeedCropBlock && stateAtPos.getValue(BaseWeedCropBlock.TOP)) {
                bePos = this.pos.below();
            }
        }


        BlockEntity be = level.getBlockEntity(bePos);
        if (be instanceof BaseWeedCropBlockEntity crop) {
            int nitrogen_val = Mth.clamp(crop.getNitrogen(), 0, MAX_NUTRIENT);
            int phosphorus_val = Mth.clamp(crop.getPhosphorus(), 0, MAX_NUTRIENT);
            int potassium_val = Mth.clamp(crop.getPotassium(), 0, MAX_NUTRIENT);

            var optimalNutrients = crop.getOptimalNutrientsLevels();

            int nitrogen_perfect_val = optimalNutrients.n;
            int phosphorus_perfect_val = optimalNutrients.p;
            int potassium_perfect_val = optimalNutrients.k;

            int nitrogen_fill_w = (int) Math.round(BAR_W * (Mth.clamp(nitrogen_val, 0, MAX_NUTRIENT) / (double) MAX_NUTRIENT));
            int phosphorus_fill_w = (int) Math.round(BAR_W * (Mth.clamp(phosphorus_val, 0, MAX_NUTRIENT) / (double) MAX_NUTRIENT));
            int potassium_fill_w = (int) Math.round(BAR_W * (Mth.clamp(potassium_val, 0, MAX_NUTRIENT) / (double) MAX_NUTRIENT));

            // Draw Bars Foreground (example values, replace with actual data)
            guiGraphics.blit(BG_TEXTURE, nitrogen_bar_x, nitrogen_bar_y, BAR_U, NITROGEN_BAR_FG_TEXTURE_Y, nitrogen_fill_w, BAR_H, TEX_W, TEX_H);
            guiGraphics.blit(BG_TEXTURE, phosphorus_bar_x, phosphorus_bar_y, BAR_U, PHOSPHORUS_BAR_FG_TEXTURE_Y, phosphorus_fill_w, BAR_H, TEX_W, TEX_H);
            guiGraphics.blit(BG_TEXTURE, potassium_bar_x, potassium_bar_y, BAR_U, POTASSIUM_BAR_FG_TEXTURE_Y, potassium_fill_w, BAR_H, TEX_W, TEX_H);

            int nDiff = Math.abs(nitrogen_val - nitrogen_perfect_val);
            int pDiff = Math.abs(phosphorus_val - phosphorus_perfect_val);
            int kDiff = Math.abs(potassium_val - potassium_perfect_val);

            ChatFormatting nColor = (nDiff == 0) ? ChatFormatting.GREEN
                    : (nDiff == 1) ? ChatFormatting.GOLD
                    : (nDiff == 2) ? ChatFormatting.RED
                    : ChatFormatting.DARK_RED;

            ChatFormatting pColor = (pDiff == 0) ? ChatFormatting.GREEN
                    : (pDiff == 1) ? ChatFormatting.GOLD
                    : (pDiff == 2) ? ChatFormatting.RED
                    : ChatFormatting.DARK_RED;

            ChatFormatting kColor = (kDiff == 0) ? ChatFormatting.GREEN
                    : (kDiff == 1) ? ChatFormatting.GOLD
                    : (kDiff == 2) ? ChatFormatting.RED
                    : ChatFormatting.DARK_RED;

            // Build value components with color coding
            Component nitrogen_value = Component.literal("" + nitrogen_val).withStyle(nColor)
                    .append(Component.literal("/").withStyle(ChatFormatting.GRAY))
                    .append(Component.literal("" + nitrogen_perfect_val).withStyle(ChatFormatting.GREEN));

            Component phosphorus_value = Component.literal("" + phosphorus_val).withStyle(pColor)
                    .append(Component.literal("/").withStyle(ChatFormatting.GRAY))
                    .append(Component.literal("" + phosphorus_perfect_val).withStyle(ChatFormatting.GREEN));

            Component potassium_value = Component.literal("" + potassium_val).withStyle(kColor)
                    .append(Component.literal("/").withStyle(ChatFormatting.GRAY))
                    .append(Component.literal("" + potassium_perfect_val).withStyle(ChatFormatting.GREEN));

            int nitrogen_text_right = nitrogen_bar_x + BAR_W - 2;
            int phosphorus_text_right = phosphorus_bar_x + BAR_W - 2;
            int potassium_text_right = potassium_bar_x + BAR_W - 2;
            int nitrogen_text_top = nitrogen_bar_y - 10;
            int phosphorus_text_top = phosphorus_bar_y - 10;
            int potassium_text_top = potassium_bar_y - 9;
            int nTextLeft = nitrogen_text_right - this.font.width(nitrogen_value);
            int pTextLeft = phosphorus_text_right - this.font.width(phosphorus_value);
            int kTextLeft = potassium_text_right - this.font.width(potassium_value);

            guiGraphics.drawString(this.font, nitrogen_value, nTextLeft, nitrogen_text_top, 0xFFFFFF, true);
            guiGraphics.drawString(this.font, phosphorus_value, pTextLeft, phosphorus_text_top, 0xFFFFFF, true);
            guiGraphics.drawString(this.font, potassium_value, kTextLeft, potassium_text_top, 0xFFFFFF, true);


            // Draw "Produces" Text
            drawScaledCentered(guiGraphics, Component.literal("Produces"), this.width / 2, top + 101, 0xFFFFFF, 1.0f, false);

            // Draw Produces Info "THC: ?%   CBD: ?%   SEED: ?   BUD: ?"
            int thcVal = crop.getThc();
            int cbdVal =  crop.getCbd();
            int budVal =  crop.getBudCount();


            Component producesLine = Component.literal("")
                    .append(Component.literal("THC: ").withStyle(ChatFormatting.GRAY))
                    .append(Component.literal(String.valueOf(thcVal)).withStyle(ChatFormatting.GREEN))
                    .append(Component.literal("%").withStyle(ChatFormatting.GRAY))
                    .append(Component.literal("  ").withStyle(ChatFormatting.GRAY))
                    .append(Component.literal("CBD: ").withStyle(ChatFormatting.GRAY))
                    .append(Component.literal(String.valueOf(cbdVal)).withStyle(ChatFormatting.AQUA))
                    .append(Component.literal("%").withStyle(ChatFormatting.GRAY))
                    .append(Component.literal("  ").withStyle(ChatFormatting.GRAY))
                    .append(Component.literal("BUDS: ").withStyle(ChatFormatting.GRAY))
                    .append(Component.literal(String.valueOf(budVal)).withStyle(ChatFormatting.LIGHT_PURPLE));

            // Draw centered under the "Produces" title
            drawScaledCentered(guiGraphics, producesLine, this.width / 2, top + 117, 0xFFFFFF, 1.0f, true);
        }
    }





//        // Title area
//        int xCenter = this.width / 2;
//        int yTopText = top + 17;
//
//        // Resolve world and base-half pos before drawing the title
//        Level level = Minecraft.getInstance().level;
//        BlockPos bePos = this.pos;
//        if (level != null) {
//            BlockState stateAtPos = level.getBlockState(this.pos);
//            if (stateAtPos.getBlock() instanceof BaseWeedCropBlock && stateAtPos.getValue(BaseWeedCropBlock.TOP)) {
//                bePos = this.pos.below();
//            }
//        }
//
//        // Dynamic title: plant block name (fallback to default title)
//        Component titleToDraw = (level != null)
//                ? level.getBlockState(bePos).getBlock().getName()
//                : this.title;
//        drawScaledCentered(guiGraphics, titleToDraw, xCenter, yTopText, 0xFFFFFF, 0.5f);
//


//        if (level == null) {
//            guiGraphics.drawCenteredString(this.font, Component.literal("World not available."), xCenter, yTopText + 34, 0xFF5555);
//            drawBar(guiGraphics, left + BAR_OFFSET_X, top + BAR_OFFSET_Y_N, BAR_U, BAR_V_N, 0);
//            drawBar(guiGraphics, left + BAR_OFFSET_X, top + BAR_OFFSET_Y_P, BAR_U, BAR_V_P, 0);
//            drawBar(guiGraphics, left + BAR_OFFSET_X, top + BAR_OFFSET_Y_K, BAR_U, BAR_V_K, 0);
//            return;
//        }

//        BlockEntity be = level.getBlockEntity(bePos);
//        if (be instanceof BaseWeedCropBlockEntity crop) {
//            int n = Mth.clamp(crop.getNitrogen(), 0, MAX_NUTRIENT);
//            int p = Mth.clamp(crop.getPhosphorus(), 0, MAX_NUTRIENT);
//            int k = Mth.clamp(crop.getPotassium(), 0, MAX_NUTRIENT);
//
//
//            guiGraphics.drawString(this.font, Component.literal("Nitrogen (" + n + ")"), left + 25, top + 40, 0xFFFFFF);
//            guiGraphics.drawString(this.font, Component.literal("Phosphorus (" + p + ")"), left + 25, top + 61, 0xFFFFFF);
//            guiGraphics.drawString(this.font, Component.literal("Potassium (" + k + ")"), left + 25, top + 82, 0xFFFFFF);
//
////            float s = 0.5f;
////            drawScaled(guiGraphics, Component.literal("Nitrogen:"),    xCenter - 55, yTopText + 27, 0xFFFFFF, s);
////            drawScaled(guiGraphics, Component.literal("Phosphorus:"),  xCenter - 55, yTopText + 47, 0xFFFFFF, s);
////            drawScaled(guiGraphics, Component.literal("Potassium:"),   xCenter - 55, yTopText + 67, 0xFFFFFF, s);
//
////            guiGraphics.drawCenteredString(this.font, Component.literal("pH: " + crop.getPh()), xCenter, yTopText + 34 + line++ * 12, 0xFFFFFF);
////            guiGraphics.drawCenteredString(this.font, Component.literal("N: " + n + "  P: " + p + "  K: " + k), xCenter, yTopText + 34 + line++ * 12, 0xFFFFFF);
////            guiGraphics.drawCenteredString(this.font, Component.literal("THC: " + crop.getThc() + "%  CBD: " + crop.getCbd() + "%"), xCenter, yTopText + 34 + line * 12, 0xFFFFFF);
//
//
//            if (crop.isValidNutrientsLevels()) {
//                drawScaledCentered(guiGraphics, Component.literal("Levels Valid!"), xCenter, top + 110, 0x55FF55, 1f);
//                drawStatsLine(guiGraphics, xCenter, top + 125, crop.getThc(), crop.getCbd(), 1f);
//            } else {
//                drawScaledCentered(guiGraphics, Component.literal("Levels Invalid!"), xCenter, top + 110, 0xFF5555, 1f);
//                drawScaledCentered(guiGraphics, Component.literal("Plant produces no drops!"), xCenter, top + 125, 0x3d3d3d, 0.8f);
//            }
//
//
//            drawScaledCentered(guiGraphics, Component.literal("Press ESC to exit!"), xCenter, top + 150, 0x292929, 1f);
//
//
//            var optimalNutrients = crop.getOptimalNutrientsLevels();
//
//            int barLeft  = left + BAR_OFFSET_X;
//            int barRight = barLeft + BAR_W - 1; // inclusive
//
//
//
//            final int COLOR_RED    = 0xFFFF5555;
//            final int COLOR_ORANGE = 0xFFFFAA00;
//            final int COLOR_GREEN  = 0xFF55FF55;
//
//            int barRightExclusive = barRight + 1;
//
//// N
//            if (optimalNutrients.n > 0 && optimalNutrients.n <= MAX_NUTRIENT) {
//                int y = top + BAR_OFFSET_Y_N;
//                int opt = optimalNutrients.n;
//
//                int pxOpt = Mth.clamp(barLeft + scaledWidth(opt), barLeft, barRight);
//                int pxM1  = Mth.clamp(barLeft + scaledWidth(opt - 1), barLeft, barRight);
//                int pxP1  = Mth.clamp(barLeft + scaledWidth(opt + 1), barLeft, barRight);
//                int pxM2  = Mth.clamp(barLeft + scaledWidth(opt - 2), barLeft, barRight);
//                int pxP2  = Mth.clamp(barLeft + scaledWidth(opt + 2), barLeft, barRight);
//
//                int xOrangeStart = Math.min(pxM1, pxP1);
//                int xOrangeEndEx = Math.min(Math.max(pxM1, pxP1) + 1, barRightExclusive);
//
//                int xRedLStart   = pxM2;
//                int xRedLEndEx   = Math.min(xOrangeStart, barRightExclusive);
//
//                int xRedRStart   = Math.min(pxP1 + 1, barRightExclusive);
//                int xRedREndEx   = Math.min(pxP2 + 1, barRightExclusive);
//
//                if (xRedLStart < xRedLEndEx) guiGraphics.fill(xRedLStart, y, xRedLEndEx, y + BAR_H, COLOR_RED);
//                if (xOrangeStart < xOrangeEndEx) guiGraphics.fill(xOrangeStart, y, xOrangeEndEx, y + BAR_H, COLOR_ORANGE);
//                if (xRedRStart < xRedREndEx) guiGraphics.fill(xRedRStart, y, xRedREndEx, y + BAR_H, COLOR_RED);
//
//                guiGraphics.fill(pxOpt, y, pxOpt + 1, y + BAR_H, COLOR_GREEN);
//            }
//
//// P
//            if (optimalNutrients.p > 0 && optimalNutrients.p <= MAX_NUTRIENT) {
//                int y = top + BAR_OFFSET_Y_P;
//                int opt = optimalNutrients.p;
//
//                int pxOpt = Mth.clamp(barLeft + scaledWidth(opt), barLeft, barRight);
//                int pxM1  = Mth.clamp(barLeft + scaledWidth(opt - 1), barLeft, barRight);
//                int pxP1  = Mth.clamp(barLeft + scaledWidth(opt + 1), barLeft, barRight);
//                int pxM2  = Mth.clamp(barLeft + scaledWidth(opt - 2), barLeft, barRight);
//                int pxP2  = Mth.clamp(barLeft + scaledWidth(opt + 2), barLeft, barRight);
//
//                int xOrangeStart = Math.min(pxM1, pxP1);
//                int xOrangeEndEx = Math.min(Math.max(pxM1, pxP1) + 1, barRightExclusive);
//
//                int xRedLStart   = pxM2;
//                int xRedLEndEx   = Math.min(xOrangeStart, barRightExclusive);
//
//                int xRedRStart   = Math.min(pxP1 + 1, barRightExclusive);
//                int xRedREndEx   = Math.min(pxP2 + 1, barRightExclusive);
//
//                if (xRedLStart < xRedLEndEx) guiGraphics.fill(xRedLStart, y, xRedLEndEx, y + BAR_H, COLOR_RED);
//                if (xOrangeStart < xOrangeEndEx) guiGraphics.fill(xOrangeStart, y, xOrangeEndEx, y + BAR_H, COLOR_ORANGE);
//                if (xRedRStart < xRedREndEx) guiGraphics.fill(xRedRStart, y, xRedREndEx, y + BAR_H, COLOR_RED);
//
//                guiGraphics.fill(pxOpt, y, pxOpt + 1, y + BAR_H, COLOR_GREEN);
//            }
//
//// K
//            if (optimalNutrients.k > 0 && optimalNutrients.k <= MAX_NUTRIENT) {
//                int y = top + BAR_OFFSET_Y_K;
//                int opt = optimalNutrients.k;
//
//                int pxOpt = Mth.clamp(barLeft + scaledWidth(opt), barLeft, barRight);
//                int pxM1  = Mth.clamp(barLeft + scaledWidth(opt - 1), barLeft, barRight);
//                int pxP1  = Mth.clamp(barLeft + scaledWidth(opt + 1), barLeft, barRight);
//                int pxM2  = Mth.clamp(barLeft + scaledWidth(opt - 2), barLeft, barRight);
//                int pxP2  = Mth.clamp(barLeft + scaledWidth(opt + 2), barLeft, barRight);
//
//                int xOrangeStart = Math.min(pxM1, pxP1);
//                int xOrangeEndEx = Math.min(Math.max(pxM1, pxP1) + 1, barRightExclusive);
//
//                int xRedLStart   = pxM2;
//                int xRedLEndEx   = Math.min(xOrangeStart, barRightExclusive);
//
//                int xRedRStart   = Math.min(pxP1 + 1, barRightExclusive);
//                int xRedREndEx   = Math.min(pxP2 + 1, barRightExclusive);
//
//                if (xRedLStart < xRedLEndEx) guiGraphics.fill(xRedLStart, y, xRedLEndEx, y + BAR_H, COLOR_RED);
//                if (xOrangeStart < xOrangeEndEx) guiGraphics.fill(xOrangeStart, y, xOrangeEndEx, y + BAR_H, COLOR_ORANGE);
//                if (xRedRStart < xRedREndEx) guiGraphics.fill(xRedRStart, y, xRedREndEx, y + BAR_H, COLOR_RED);
//
//                guiGraphics.fill(pxOpt, y, pxOpt + 1, y + BAR_H, COLOR_GREEN);
//            }
//
//
//            drawBar(guiGraphics, left + BAR_OFFSET_X, top + BAR_OFFSET_Y_N, BAR_U, BAR_V_N, scaledWidth(n));
//            drawBar(guiGraphics, left + BAR_OFFSET_X, top + BAR_OFFSET_Y_P, BAR_U, BAR_V_P, scaledWidth(p));
//            drawBar(guiGraphics, left + BAR_OFFSET_X, top + BAR_OFFSET_Y_K, BAR_U, BAR_V_K, scaledWidth(k));
//
//
//
//            renderOptimalNutrientsTooltip(
//                    guiGraphics, mouseX, mouseY, left, top,
//                    n, p, k,
//                    optimalNutrients.n, optimalNutrients.p, optimalNutrients.k
//            );
//
//        } else {
//            guiGraphics.drawCenteredString(this.font, Component.literal("No plant data available."), xCenter, yTopText + 34, 0xFF5555);
//            drawBar(guiGraphics, left + BAR_OFFSET_X, top + BAR_OFFSET_Y_N, BAR_U, BAR_V_N, 0);
//            drawBar(guiGraphics, left + BAR_OFFSET_X, top + BAR_OFFSET_Y_P, BAR_U, BAR_V_P, 0);
//            drawBar(guiGraphics, left + BAR_OFFSET_X, top + BAR_OFFSET_Y_K, BAR_U, BAR_V_K, 0);
//        }
//    }

//    private void renderOptimalNutrientsTooltip(GuiGraphics g,
//                                               int mouseX, int mouseY,
//                                               int baseX, int baseY,
//                                               int n, int p, int k,
//                                               int optN, int optP, int optK) {
//        // N bar
//        if (isMouseAboveArea(mouseX, mouseY, baseX, baseY, BAR_OFFSET_X, BAR_OFFSET_Y_N, BAR_W, BAR_H)) {
//            g.renderTooltip(
//                    this.font,
//                    List.of(
//                            Component.literal("Nitrogen"),
//                            Component.literal("Current: " + n),
//                            Component.literal("Optimal: " + optN)
//                    ),
//                    Optional.empty(),
//                    mouseX, mouseY
//            );
//            return;
//        }
//        // P bar
//        if (isMouseAboveArea(mouseX, mouseY, baseX, baseY, BAR_OFFSET_X, BAR_OFFSET_Y_P, BAR_W, BAR_H)) {
//            g.renderTooltip(
//                    this.font,
//                    List.of(
//                            Component.literal("Phosphorus"),
//                            Component.literal("Current: " + p),
//                            Component.literal("Optimal: " + optP)
//                    ),
//                    Optional.empty(),
//                    mouseX, mouseY
//            );
//            return;
//        }
//        // K bar
//        if (isMouseAboveArea(mouseX, mouseY, baseX, baseY, BAR_OFFSET_X, BAR_OFFSET_Y_K, BAR_W, BAR_H)) {
//            g.renderTooltip(
//                    this.font,
//                    List.of(
//                            Component.literal("Potassium"),
//                            Component.literal("Current: " + k),
//                            Component.literal("Optimal: " + optK)
//                    ),
//                    Optional.empty(),
//                    mouseX, mouseY
//            );
//        }
//    }
//
//
//    private boolean isMouseAboveArea(int mouseX, int mouseY, int x, int y, int offsetX, int offsetY, int width, int height) {
//        return MouseUtil.isMouseOver(mouseX, mouseY, x + offsetX, y + offsetY, width, height);
//    }
//
//
//    private void drawStatsLine(GuiGraphics g, int centerX, int y, int thc, int cbd, float scale) {
//        String sThcLabel = "THC: ";
//        String sThcVal   = String.valueOf(thc);
//        String sPct      = "%";
//        String sSpacer   = "  ";
//        String sCbdLabel = "CBD: ";
//        String sCbdVal   = String.valueOf(cbd);
//
//        int wThcLabel = this.font.width(sThcLabel);
//        int wThcVal   = this.font.width(sThcVal);
//        int wPct      = this.font.width(sPct);
//        int wSpacer   = this.font.width(sSpacer);
//        int wCbdLabel = this.font.width(sCbdLabel);
//        int wCbdVal   = this.font.width(sCbdVal);
//
//        float totalUnscaled = wThcLabel + wThcVal + wPct + wSpacer + wCbdLabel + wCbdVal + wPct;
//        int left = (int) (centerX - (totalUnscaled * scale) / 2f);
//
//        g.pose().pushPose();
//        g.pose().translate(left, y, 0);
//        g.pose().scale(scale, scale, 1f);
//
//        int x = 0;
//        g.drawString(this.font, sThcLabel, x, 0, THC_LABEL_COLOR, false); x += wThcLabel;
//        g.drawString(this.font, sThcVal,   x, 0, VALUE_COLOR,     false); x += wThcVal;
//        g.drawString(this.font, sPct,      x, 0, PERCENT_COLOR,   false); x += wPct;
//        x += wSpacer; // skip drawing the spacer, just advance
//
//        g.drawString(this.font, sCbdLabel, x, 0, CBD_LABEL_COLOR, false); x += wCbdLabel;
//        g.drawString(this.font, sCbdVal,   x, 0, VALUE_COLOR,     false); x += wCbdVal;
//        g.drawString(this.font, sPct,      x, 0, PERCENT_COLOR,   false);
//
//        g.pose().popPose();
//    }
//
//
//    private int scaledWidth(int value) {
//        int v = Mth.clamp(value, 0, MAX_NUTRIENT);
//        return (int) Math.round((v / (double) MAX_NUTRIENT) * BAR_W);
//    }
//
//    // Blits a BAR_W x BAR_H slice but only 'width' pixels wide (progress)
//    private void drawBar(GuiGraphics g, int x, int y, int u, int v, int width) {
//        if (width <= 0) return;
//        g.blit(BG_TEXTURE, x, y, u, v, width, BAR_H, TEX_W, TEX_H);
//    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }



    private void drawScaled(GuiGraphics g, Component text, int x, int y, int color, float scale) {
        g.pose().pushPose();
        g.pose().translate(x, y, 0);
        g.pose().scale(scale, scale, 1.0f);
        g.drawString(this.font, text, 0, 0, color, false);
        g.pose().popPose();
    }

    private void drawScaledCentered(GuiGraphics g, Component text, int centerX, int y, int color, float scale, boolean shadow) {
        int w = this.font.width(text);
        int left = (int) (centerX - (w * scale) / 2.0f);
        g.pose().pushPose();
        g.pose().translate(left, y, 0);
        g.pose().scale(scale, scale, 1.0f);
        g.drawString(this.font, text, 0, 0, color, shadow);
        g.pose().popPose();
    }
}
