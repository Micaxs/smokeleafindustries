package net.micaxs.smokeleaf.compat.jei;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.neoforge.NeoForgeTypes;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.micaxs.smokeleaf.SmokeleafIndustries;
import net.micaxs.smokeleaf.block.ModBlocks;
import net.micaxs.smokeleaf.component.ModDataComponentTypes;
import net.micaxs.smokeleaf.fluid.ModFluids;
import net.micaxs.smokeleaf.item.ModItems;
import net.micaxs.smokeleaf.recipe.LiquifierRecipe;
import net.micaxs.smokeleaf.strain.StrainRegistry;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class LiquifierRecipeCategory implements IRecipeCategory<LiquifierRecipeCategory.Display> {
    public static final ResourceLocation UID =
            ResourceLocation.fromNamespaceAndPath(SmokeleafIndustries.MODID, "liquifier");
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(SmokeleafIndustries.MODID, "textures/gui/liquifier/liquifier_gui.png");
    public static final RecipeType<Display> LIQUIFIER_RECIPE_TYPE =
            new RecipeType<>(UID, Display.class);

    private final IDrawable background;
    private final IDrawable icon;

    private static final int TANK_X = 129;
    private static final int TANK_Y = 6;
    private static final int TANK_WIDTH = 16;
    private static final int TANK_HEIGHT = 64;
    private static final int TANK_CAPACITY = 8000;

    private static final int BUCKET_X = TANK_X - 20;
    private static final int BUCKET_Y = TANK_Y + TANK_HEIGHT - 16;

    public LiquifierRecipeCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 5, 5, 168, 75);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK,
                new ItemStack(ModBlocks.LIQUIFIER.get()));
    }

    @Override
    public RecipeType<Display> getRecipeType() {
        return LIQUIFIER_RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("block.smokeleafindustries.liquifier");
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, Display display, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 25, 30)
                .addItemStack(display.inputExtract());

        builder.addSlot(RecipeIngredientRole.OUTPUT, TANK_X, TANK_Y)
                .setFluidRenderer(TANK_CAPACITY, false, TANK_WIDTH, TANK_HEIGHT)
                .addIngredient(NeoForgeTypes.FLUID_STACK, display.outputFluid());

        ItemStack filledBucket = display.outputBucket();
        if (!filledBucket.isEmpty()) {
            IRecipeSlotBuilder bucket = builder.addSlot(RecipeIngredientRole.OUTPUT, BUCKET_X, BUCKET_Y)
                    .addItemStack(filledBucket);
            bucket.addRichTooltipCallback((slotView, tooltip) ->
                    tooltip.add(Component.translatable("jei.smokeleafindustries.bucket_use")));
        }
    }

    @Override
    public void draw(Display display, IRecipeSlotsView recipeSlotsView,
                     GuiGraphics guiGraphics, double mouseX, double mouseY) {
        background.draw(guiGraphics);
    }

    @Override
    public int getWidth() {
        return 168;
    }

    @Override
    public int getHeight() {
        return 75;
    }

    /**
     * Generates per-strain display entries for a liquifier recipe.
     * Each strain gets a colored extract input and colored fluid output.
     */
    public static List<Display> buildStrainDisplays(LiquifierRecipe recipe) {
        List<Display> displays = new ArrayList<>();
        for (String strainId : StrainRegistry.ids()) {
            StrainRegistry.get(strainId).ifPresent(strainData -> {
                // Colored input extract
                ItemStack inputStack = new ItemStack(ModItems.GENERIC_EXTRACT.get());
                inputStack.set(ModDataComponentTypes.STRAIN_DATA.get(), strainData);
                inputStack.set(ModDataComponentTypes.STRAIN_ID.get(), strainId);

                // Output fluid
                FluidStack fluidOut = recipe.outputCopy();

                // Colored output bucket
                ItemStack bucketStack = new ItemStack(ModFluids.UNIDENTIFIED_MIXTURE_BUCKET.get());
                bucketStack.set(ModDataComponentTypes.STRAIN_DATA.get(), strainData);
                bucketStack.set(ModDataComponentTypes.STRAIN_ID.get(), strainId);

                displays.add(new Display(inputStack, fluidOut, bucketStack));
            });
        }
        return displays;
    }

    /**
     * Display record for per-strain liquifier recipes.
     */
    public record Display(ItemStack inputExtract, FluidStack outputFluid, ItemStack outputBucket) { }
}
