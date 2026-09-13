package net.micaxs.smokeleaf.compat.jei;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.micaxs.smokeleaf.SmokeleafIndustries;
import net.micaxs.smokeleaf.fluid.ModFluids;
import net.micaxs.smokeleaf.item.ModItems;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class StrainCraftingRecipeCategory implements IRecipeCategory<StrainCraftingRecipeCategory.Display> {

    public static final ResourceLocation UID =
            ResourceLocation.fromNamespaceAndPath(SmokeleafIndustries.MODID, "strain_crafting");
    private static final ResourceLocation VANILLA_BG =
            ResourceLocation.withDefaultNamespace("textures/gui/container/crafting_table.png");

    public static final RecipeType<Display> RECIPE_TYPE =
            new RecipeType<>(UID, Display.class);

    private final IDrawable background;
    private final IDrawable icon;

    public StrainCraftingRecipeCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createDrawable(VANILLA_BG, 29, 16, 116, 54);
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK,
                new ItemStack(Items.CRAFTING_TABLE));
    }

    @Override
    public RecipeType<Display> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("jei.smokeleafindustries.category.strain_crafting");
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return icon;
    }

    @SuppressWarnings("removal")
    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public int getWidth() {
        return 116;
    }

    @Override
    public int getHeight() {
        return 54;
    }

    @Override
    public void draw(Display display, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, Display display, IFocusGroup focuses) {
        for (int i = 0; i < display.slotX.size(); i++) {
            int x = display.slotX.get(i);
            int y = display.slotY.get(i);
            Item strainItem = display.strainItemForSlot(i);
            if (strainItem != null) {
                List<ItemStack> colored = JeiStrainHelper.coloredStacks(strainItem, focuses);
                if (!colored.isEmpty()) {
                    builder.addSlot(RecipeIngredientRole.INPUT, x, y)
                            .addIngredients(net.minecraft.world.item.crafting.Ingredient.of(colored.stream()));
                }
            } else {
                builder.addSlot(RecipeIngredientRole.INPUT, x, y)
                        .addItemStack(display.slotStacks.get(i));
            }
        }
        builder.addSlot(RecipeIngredientRole.OUTPUT, 95, 19)
                .addItemStack(display.output);
    }

    public record Display(ItemStack output,
                          List<Integer> slotX, List<Integer> slotY,
                          List<ItemStack> slotStacks,
                          List<Item> strainItems) {
        public Display {
            if (slotX.size() != slotY.size() || slotX.size() != slotStacks.size() || slotX.size() != strainItems.size()) {
                throw new IllegalArgumentException("All slot lists must be same length");
            }
        }

        public Item strainItemForSlot(int index) {
            return strainItems.get(index);
        }
    }

    public static List<Display> buildDisplays() {
        List<Display> displays = new ArrayList<>();

        var weedVariants = JeiStrainHelper.coloredStacks(ModItems.GENERIC_WEED.get());

        // Infused butter: butter + weed -> infused butter
        displays.add(buildShapelessDisplay(
                List.of(
                        new SlotEntry(new ItemStack(ModItems.BUTTER.get()), null),
                        new SlotEntry(ItemStack.EMPTY, ModItems.GENERIC_WEED.get())
                ),
                new ItemStack(ModItems.INFUSED_BUTTER.get())
        ));

        // Hash brownie: 3x3
        // W B W
        // C H C
        // W B W
        displays.add(buildShaped3x3Display(List.of(
                new SlotEntry(ItemStack.EMPTY, ModItems.GENERIC_WEED.get()),
                new SlotEntry(new ItemStack(ModItems.INFUSED_BUTTER.get()), null),
                new SlotEntry(ItemStack.EMPTY, ModItems.GENERIC_WEED.get()),
                new SlotEntry(new ItemStack(Items.COCOA_BEANS), null),
                new SlotEntry(new ItemStack(ModFluids.HASH_OIL_BUCKET.get()), null),
                new SlotEntry(new ItemStack(Items.COCOA_BEANS), null),
                new SlotEntry(ItemStack.EMPTY, ModItems.GENERIC_WEED.get()),
                new SlotEntry(new ItemStack(ModItems.INFUSED_BUTTER.get()), null),
                new SlotEntry(ItemStack.EMPTY, ModItems.GENERIC_WEED.get())
        ), new ItemStack(ModItems.HASH_BROWNIE.get())));

        // Herb cake: 3x3
        // B S B
        // W E W
        // M M M
        displays.add(buildShaped3x3Display(List.of(
                new SlotEntry(new ItemStack(ModItems.INFUSED_BUTTER.get()), null),
                new SlotEntry(new ItemStack(Items.SUGAR), null),
                new SlotEntry(new ItemStack(ModItems.INFUSED_BUTTER.get()), null),
                new SlotEntry(ItemStack.EMPTY, ModItems.GENERIC_WEED.get()),
                new SlotEntry(new ItemStack(Items.EGG), null),
                new SlotEntry(ItemStack.EMPTY, ModItems.GENERIC_WEED.get()),
                new SlotEntry(new ItemStack(Items.MILK_BUCKET), null),
                new SlotEntry(new ItemStack(Items.MILK_BUCKET), null),
                new SlotEntry(new ItemStack(Items.MILK_BUCKET), null)
        ), new ItemStack(ModItems.HERB_CAKE.get())));

        // Weed cookie: cookie + infused butter -> weed cookie
        displays.add(buildShapelessDisplay(
                List.of(
                        new SlotEntry(new ItemStack(Items.COOKIE), null),
                        new SlotEntry(new ItemStack(ModItems.INFUSED_BUTTER.get()), null)
                ),
                new ItemStack(ModItems.WEED_COOKIE.get())
        ));

        // Strain copy: bag -> 8x weed
        displays.add(buildShapelessDisplay(
                List.of(new SlotEntry(ItemStack.EMPTY, ModItems.GENERIC_BAG.get())),
                new ItemStack(ModItems.GENERIC_WEED.get(), 8)
        ));

        // Strain copy: empty bag + 8x weed -> bag
        displays.add(buildShapelessDisplay(
                List.of(
                        new SlotEntry(new ItemStack(ModItems.EMPTY_BAG.get()), null),
                        new SlotEntry(ItemStack.EMPTY, ModItems.GENERIC_WEED.get()),
                        new SlotEntry(ItemStack.EMPTY, ModItems.GENERIC_WEED.get()),
                        new SlotEntry(ItemStack.EMPTY, ModItems.GENERIC_WEED.get()),
                        new SlotEntry(ItemStack.EMPTY, ModItems.GENERIC_WEED.get()),
                        new SlotEntry(ItemStack.EMPTY, ModItems.GENERIC_WEED.get()),
                        new SlotEntry(ItemStack.EMPTY, ModItems.GENERIC_WEED.get()),
                        new SlotEntry(ItemStack.EMPTY, ModItems.GENERIC_WEED.get()),
                        new SlotEntry(ItemStack.EMPTY, ModItems.GENERIC_WEED.get())
                ),
                new ItemStack(ModItems.GENERIC_BAG.get())
        ));

        return displays;
    }

    private record SlotEntry(ItemStack stack, Item strainItem) {}

    private static Display buildShapelessDisplay(List<SlotEntry> slots, ItemStack output) {
        int n = Math.min(slots.size(), 9);
        List<Integer> xs = new ArrayList<>();
        List<Integer> ys = new ArrayList<>();
        List<ItemStack> stacks = new ArrayList<>();
        List<Item> strainItems = new ArrayList<>();

        for (int idx = 0; idx < n; idx++) {
            SlotEntry entry = slots.get(idx);
            xs.add(1 + (idx % 3) * 18);
            ys.add(1 + (idx / 3) * 18);
            stacks.add(entry.stack);
            strainItems.add(entry.strainItem);
        }
        return new Display(output, xs, ys, stacks, strainItems);
    }

    private static Display buildShaped3x3Display(List<SlotEntry> grid9, ItemStack output) {
        if (grid9.size() != 9) {
            throw new IllegalArgumentException("Shaped 3x3 display needs exactly 9 slots");
        }
        List<Integer> xs = new ArrayList<>();
        List<Integer> ys = new ArrayList<>();
        List<ItemStack> stacks = new ArrayList<>();
        List<Item> strainItems = new ArrayList<>();
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                SlotEntry entry = grid9.get(r * 3 + c);
                xs.add(1 + c * 18);
                ys.add(1 + r * 18);
                stacks.add(entry.stack);
                strainItems.add(entry.strainItem);
            }
        }
        return new Display(output, xs, ys, stacks, strainItems);
    }
}
