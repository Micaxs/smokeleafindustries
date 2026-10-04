package net.micaxs.smokeleaf.gametest;

import net.micaxs.smokeleaf.SmokeleafIndustries;
import net.micaxs.smokeleaf.recipe.StrainCopyShapedRecipe;
import net.minecraft.core.NonNullList;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Checks that none of our crafting recipes can be matched by the same grid as a vanilla crafting recipe
 * or another one of our own.
 */
@GameTestHolder(SmokeleafIndustries.MODID)
@PrefixGameTestTemplate(false)
public class RecipeConflictGameTests {

    private static final int SPECIAL = 0;
    private static final int SHAPELESS = -1;
    private static final int MAX_SAMPLES = 16;

    /**
     * width > 0: shaped, cells holds width * height entries (empty set = empty slot).
     * width == SHAPELESS: cells holds one entry per ingredient.
     * width == SPECIAL: custom matches() logic with no ingredient list, only checked by sampling.
     */
    private record Info(ResourceLocation id, CraftingRecipe recipe, int width, int height,
                        List<Set<Item>> cells, List<Ingredient> ingredients) {
        boolean special() {
            return width == SPECIAL;
        }
    }

    @GameTest(template = "empty_3x3x3")
    public static void noConflictsWithVanillaCrafting(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        List<Info> ours = new ArrayList<>();
        List<Info> vanilla = new ArrayList<>();
        for (RecipeHolder<CraftingRecipe> holder : level.getRecipeManager().getAllRecipesFor(RecipeType.CRAFTING)) {
            String ns = holder.id().getNamespace();
            if (ns.equals(SmokeleafIndustries.MODID)) ours.add(describe(holder));
            else if (ns.equals("minecraft")) vanilla.add(describe(holder));
        }

        List<String> conflicts = new ArrayList<>();
        for (Info a : ours) {
            for (Info b : vanilla) {
                if (conflicts(a, b, level)) {
                    conflicts.add(a.id() + " <-> " + b.id());
                }
            }
        }

        // Our recipes must not shadow each other either.
        for (int i = 0; i < ours.size(); i++) {
            for (int j = i + 1; j < ours.size(); j++) {
                if (conflicts(ours.get(i), ours.get(j), level)) {
                    conflicts.add(ours.get(i).id() + " <-> " + ours.get(j).id());
                }
            }
        }

        SmokeleafIndustries.LOGGER.info("[RecipeConflictGameTests] checked {} smokeleaf vs {} vanilla crafting recipes, {} conflicts",
                ours.size(), vanilla.size(), conflicts.size());
        conflicts.forEach(c -> SmokeleafIndustries.LOGGER.error("[RecipeConflictGameTests] conflict: {}", c));
        helper.assertTrue(conflicts.isEmpty(), conflicts.size() + " crafting recipe conflicts: " + conflicts);
        helper.succeed();
    }

    private static Info describe(RecipeHolder<CraftingRecipe> holder) {
        CraftingRecipe recipe = holder.value();
        NonNullList<Ingredient> ingredients = recipe.getIngredients();
        int width = SPECIAL;
        int height = 0;
        if (recipe instanceof ShapedRecipe shaped) {
            width = shaped.getWidth();
            height = shaped.getHeight();
        } else if (recipe instanceof StrainCopyShapedRecipe shaped) {
            width = shaped.pattern().width();
            height = shaped.pattern().height();
        } else if (!ingredients.isEmpty()) {
            width = SHAPELESS;
        }
        List<Set<Item>> cells = ingredients.stream()
                .map(ing -> Arrays.stream(ing.getItems()).map(ItemStack::getItem).collect(Collectors.toSet()))
                .toList();
        return new Info(holder.id(), recipe, width, height, cells, ingredients);
    }

    private static boolean conflicts(Info a, Info b, ServerLevel level) {
        if (a.special() || b.special()) {
            // Custom matching logic: feed each side's sample grids into the other's real matches().
            if (a.special() && b.special()) return false;
            Info special = a.special() ? a : b;
            Info plain = a.special() ? b : a;
            return samples(plain).stream().anyMatch(input -> special.recipe().matches(input, level));
        }
        if (a.width() > 0 && b.width() > 0) {
            return a.width() == b.width() && a.height() == b.height()
                    && (cellsOverlap(a.cells(), b.cells()) || cellsOverlap(a.cells(), mirror(b)));
        }
        return canPair(nonEmpty(a.cells()), nonEmpty(b.cells()));
    }

    private static boolean cellsOverlap(List<Set<Item>> a, List<Set<Item>> b) {
        for (int i = 0; i < a.size(); i++) {
            if (!slotOverlap(a.get(i), b.get(i))) return false;
        }
        return true;
    }

    private static boolean slotOverlap(Set<Item> a, Set<Item> b) {
        if (a.isEmpty() || b.isEmpty()) return a.isEmpty() && b.isEmpty();
        return a.stream().anyMatch(b::contains);
    }

    private static List<Set<Item>> mirror(Info shaped) {
        List<Set<Item>> out = new ArrayList<>();
        for (int y = 0; y < shaped.height(); y++) {
            for (int x = shaped.width() - 1; x >= 0; x--) {
                out.add(shaped.cells().get(y * shaped.width() + x));
            }
        }
        return out;
    }

    private static List<Set<Item>> nonEmpty(List<Set<Item>> cells) {
        return cells.stream().filter(s -> !s.isEmpty()).toList();
    }

    /** True if every ingredient on one side can be paired with a distinct, overlapping ingredient on the other. */
    private static boolean canPair(List<Set<Item>> a, List<Set<Item>> b) {
        if (a.size() != b.size()) return false;
        int[] matchOfB = new int[b.size()];
        Arrays.fill(matchOfB, -1);
        for (int i = 0; i < a.size(); i++) {
            if (!augment(i, a, b, matchOfB, new boolean[b.size()])) return false;
        }
        return true;
    }

    private static boolean augment(int i, List<Set<Item>> a, List<Set<Item>> b, int[] matchOfB, boolean[] seen) {
        for (int j = 0; j < b.size(); j++) {
            if (seen[j] || !slotOverlap(a.get(i), b.get(j))) continue;
            seen[j] = true;
            if (matchOfB[j] < 0 || augment(matchOfB[j], a, b, matchOfB, seen)) {
                matchOfB[j] = i;
                return true;
            }
        }
        return false;
    }

    /** Concrete grids that the given shaped/shapeless recipe accepts, cycling through each ingredient's items. */
    private static List<CraftingInput> samples(Info info) {
        int variants = 1;
        for (Ingredient ing : info.ingredients()) {
            variants = Math.max(variants, ing.getItems().length);
        }
        variants = Math.min(variants, MAX_SAMPLES);

        List<CraftingInput> out = new ArrayList<>();
        Set<List<Item>> seen = new HashSet<>();
        for (int k = 0; k < variants; k++) {
            List<ItemStack> grid = new ArrayList<>();
            for (Ingredient ing : info.ingredients()) {
                ItemStack[] items = ing.getItems();
                grid.add(items.length == 0 ? ItemStack.EMPTY : items[k % items.length].copy());
            }
            if (!seen.add(grid.stream().map(ItemStack::getItem).toList())) continue;
            if (info.width() > 0) {
                out.add(CraftingInput.of(info.width(), info.height(), grid));
            } else {
                while (grid.size() < 9) grid.add(ItemStack.EMPTY);
                out.add(CraftingInput.of(3, 3, grid));
            }
        }
        return out;
    }
}
