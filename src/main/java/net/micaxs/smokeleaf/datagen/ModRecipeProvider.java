package net.micaxs.smokeleaf.datagen;

import net.micaxs.smokeleaf.SmokeleafIndustries;
import net.micaxs.smokeleaf.block.ModBlocks;
import net.micaxs.smokeleaf.fluid.ModFluids;
import net.micaxs.smokeleaf.item.ModItems;
import net.micaxs.smokeleaf.utils.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;
import net.micaxs.smokeleaf.recipe.ArmorUpgradeShapelessRecipe;
import net.micaxs.smokeleaf.recipe.LiquifierRecipe;
import net.micaxs.smokeleaf.recipe.StrainCopyShapedRecipe;
import net.micaxs.smokeleaf.recipe.StrainCopyShapelessRecipe;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {
    public ModRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        slab(recipeOutput, RecipeCategory.BUILDING_BLOCKS, ModBlocks.HEMP_PLANK_SLAB.get(), ModBlocks.HEMP_PLANKS.get());
        slab(recipeOutput, RecipeCategory.BUILDING_BLOCKS, ModBlocks.HEMP_STONE_SLAB.get(), ModBlocks.HEMP_STONE.get());
        slab(recipeOutput, RecipeCategory.BUILDING_BLOCKS, ModBlocks.HEMP_BRICK_SLAB.get(), ModBlocks.HEMP_BRICKS.get());
        slab(recipeOutput, RecipeCategory.BUILDING_BLOCKS, ModBlocks.HEMP_CHISELED_STONE_SLAB.get(), ModBlocks.HEMP_CHISELED_STONE.get());

        stairBuilder(ModBlocks.HEMP_PLANK_STAIRS.get(), Ingredient.of(ModBlocks.HEMP_PLANKS.get())).group("hemp_planks").unlockedBy("has_hemp_planks", has(ModBlocks.HEMP_PLANKS.get())).save(recipeOutput);
        stairBuilder(ModBlocks.HEMP_STONE_STAIRS.get(), Ingredient.of(ModBlocks.HEMP_STONE.get())).group("hemp_stone").unlockedBy("has_hemp_stone", has(ModBlocks.HEMP_STONE.get())).save(recipeOutput);
        stairBuilder(ModBlocks.HEMP_BRICK_STAIRS.get(), Ingredient.of(ModBlocks.HEMP_BRICKS.get())).group("hemp_bricks").unlockedBy("has_hemp_bricks", has(ModBlocks.HEMP_BRICKS.get())).save(recipeOutput);
        stairBuilder(ModBlocks.HEMP_CHISELED_STONE_STAIRS.get(), Ingredient.of(ModBlocks.HEMP_CHISELED_STONE.get())).group("hemp_chiseled_stone").unlockedBy("has_hemp_chiseled_stone", has(ModBlocks.HEMP_CHISELED_STONE.get())).save(recipeOutput);

        pressurePlate(recipeOutput, ModBlocks.HEMP_STONE_PRESSURE_PLATE.get(), ModBlocks.HEMP_STONE.get());
        pressurePlate(recipeOutput, ModBlocks.HEMP_PLANK_PRESSURE_PLATE.get(), ModBlocks.HEMP_PLANKS.get());
        buttonBuilder(ModBlocks.HEMP_STONE_BUTTON.get(), Ingredient.of(ModBlocks.HEMP_STONE.get())).group("hemp_stone").unlockedBy("has_hemp_stone", has(ModBlocks.HEMP_STONE.get())).save(recipeOutput);
        buttonBuilder(ModBlocks.HEMP_PLANK_BUTTON.get(), Ingredient.of(ModBlocks.HEMP_PLANKS.get())).group("hemp_planks").unlockedBy("has_hemp_planks", has(ModBlocks.HEMP_PLANKS.get())).save(recipeOutput);

        wall(recipeOutput, RecipeCategory.BUILDING_BLOCKS, ModBlocks.HEMP_STONE_WALL.get(), ModBlocks.HEMP_STONE.get());
        wall(recipeOutput, RecipeCategory.BUILDING_BLOCKS, ModBlocks.HEMP_BRICK_WALL.get(), ModBlocks.HEMP_BRICKS.get());
        wall(recipeOutput, RecipeCategory.BUILDING_BLOCKS, ModBlocks.HEMP_CHISELED_STONE_WALL.get(), ModBlocks.HEMP_CHISELED_STONE.get());

        fenceBuilder(ModBlocks.HEMP_PLANK_FENCE.get(), Ingredient.of(ModBlocks.HEMP_PLANKS.get())).group("hemp_planks").unlockedBy("has_hemp_planks", has(ModBlocks.HEMP_PLANKS.get())).save(recipeOutput);
        fenceGateBuilder(ModBlocks.HEMP_PLANK_FENCE_GATE.get(), Ingredient.of(ModBlocks.HEMP_PLANKS.get())).group("hemp_planks").unlockedBy("has_hemp_planks", has(ModBlocks.HEMP_PLANKS.get())).save(recipeOutput);

        doorBuilder(ModBlocks.HEMP_PLANK_DOOR.get(), Ingredient.of(ModBlocks.HEMP_PLANKS.get())).group("hemp_planks").unlockedBy("has_hemp_planks", has(ModBlocks.HEMP_PLANKS.get())).save(recipeOutput);
        trapdoorBuilder(ModBlocks.HEMP_PLANK_TRAPDOOR.get(), Ingredient.of(ModBlocks.HEMP_PLANKS.get())).group("hemp_planks").unlockedBy("has_hemp_planks", has(ModBlocks.HEMP_PLANKS.get())).save(recipeOutput);


        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.BASE_EXTRACT.get())
                .requires(Items.MILK_BUCKET)
                .requires(Items.SUGAR)
                .requires(Items.GLASS_BOTTLE)
                .unlockedBy(getHasName(Items.MILK_BUCKET), has(Items.GLASS_BOTTLE))
                .save(recipeOutput);

        saveStrainCopyShapelessRecipe(recipeOutput, ModItems.INFUSED_BUTTER.get(),
                Ingredient.of(ModItems.BUTTER.get()), Ingredient.of(ModTags.WEEDS));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.HEMP_PLASTIC.get(), 4)
                .requires(ModFluids.HASH_OIL_BUCKET)
                .requires(ModItems.BIO_COMPOSITE)
                .unlockedBy(getHasName(ModItems.BIO_COMPOSITE), has(ModItems.BIO_COMPOSITE))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.HEMP_COAL.get())
                .requires(Items.COAL)
                .requires(ModItems.HEMP_LEAF, 2)
                .unlockedBy(getHasName(ModItems.HEMP_LEAF), has(ModItems.HEMP_LEAF))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.REFLECTOR.get())
                .pattern(" I ")
                .pattern("IRI")
                .pattern("   ")
                .define('I', ModItems.HEMP_PLASTIC)
                .define('R', Items.REDSTONE)
                .unlockedBy(getHasName(ModItems.HEMP_PLASTIC), has(ModItems.HEMP_PLASTIC))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.HPS_LAMP.get())
                .pattern("PPP")
                .pattern("RGP")
                .pattern("PPP")
                .define('P', Items.GLASS_PANE)
                .define('R', Items.REDSTONE)
                .define('G', Items.GLOWSTONE)
                .unlockedBy(getHasName(Items.REDSTONE), has(Items.REDSTONE))
                .save(recipeOutput);


        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.EMPTY_BAG.get())
                .pattern("I I")
                .pattern(" I ")
                .define('I', ModItems.HEMP_PLASTIC)
                .unlockedBy(getHasName(ModItems.HEMP_PLASTIC), has(ModItems.HEMP_PLASTIC))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.HEMP_PLANKS.get(), 8)
                .pattern("PPP")
                .pattern("PBP")
                .pattern("PPP")
                .define('P', Items.OAK_PLANKS)
                .define('B', ModFluids.HASH_OIL_BUCKET)
                .unlockedBy(getHasName(ModItems.HEMP_PLASTIC), has(ModItems.HEMP_PLASTIC))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.HEMP_STONE.get(), 8)
                .pattern("PPP")
                .pattern("PBP")
                .pattern("PPP")
                .define('P', Items.STONE)
                .define('B', ModFluids.HASH_OIL_BUCKET)
                .unlockedBy(getHasName(ModItems.HEMP_PLASTIC), has(ModItems.HEMP_PLASTIC))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.HEMP_FABRIC.get(), 1)
                .pattern("PP ")
                .pattern("PP ")
                .pattern("   ")
                .define('P', ModItems.HEMP_FIBERS)
                .unlockedBy(getHasName(ModItems.HEMP_FIBERS), has(ModItems.HEMP_FIBERS))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.HEMP_BRICKS.get(), 4)
                .pattern("PP")
                .pattern("PP")
                .define('P', ModBlocks.HEMP_STONE)
                .unlockedBy(getHasName(ModItems.HEMP_PLASTIC), has(ModItems.HEMP_PLASTIC))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.MANUAL_GRINDER.get())
                .pattern("SHS")
                .pattern("ISI")
                .pattern("SHS")
                .define('S', Items.STONE)
                .define('H', ModItems.HEMP_FIBERS)
                .define('I', Items.IRON_INGOT)
                .unlockedBy(getHasName(ModItems.HEMP_FIBERS), has(ModItems.HEMP_FIBERS))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.DRYING_RACK.get())
                .pattern("SHS")
                .pattern("SPS")
                .pattern("SHS")
                .define('S', Items.IRON_INGOT)
                .define('H', ModItems.HEMP_FIBERS)
                .define('P', Items.OAK_PLANKS)
                .unlockedBy(getHasName(ModItems.HEMP_FIBERS), has(ModItems.HEMP_FIBERS))
                .save(recipeOutput);

        SimpleCookingRecipeBuilder.smelting(
                        Ingredient.of(ModItems.BIO_COMPOSITE.get()),
                        RecipeCategory.MISC,
                        ModItems.HEMP_PLASTIC.get(),
                        0.15f,
                        320)
                .unlockedBy(getHasName(ModItems.BIO_COMPOSITE), has(ModItems.BIO_COMPOSITE))
                .save(recipeOutput, SmokeleafIndustries.MODID + ":smelting/hemp_plastic_from_bio_composite");

        saveStrainCopyShapelessRecipe(recipeOutput, ModItems.WEED_COOKIE.get(),
                Ingredient.of(Items.COOKIE), Ingredient.of(ModItems.INFUSED_BUTTER.get()));

        saveStrainCopyShapedRecipe(recipeOutput, ModItems.HASH_BROWNIE.get(), Map.of(
                        'W', Ingredient.of(ModTags.WEEDS),
                        'B', Ingredient.of(ModItems.INFUSED_BUTTER.get()),
                        'C', Ingredient.of(Items.COCOA_BEANS),
                        'H', Ingredient.of(ModFluids.HASH_OIL_BUCKET.get())
                ), "WBW", "CHC", "WBW");

        saveStrainCopyShapedRecipe(recipeOutput, ModItems.HERB_CAKE.get(), Map.of(
                        'W', Ingredient.of(ModTags.WEEDS),
                        'B', Ingredient.of(ModItems.INFUSED_BUTTER.get()),
                        'E', Ingredient.of(Items.EGG),
                        'S', Ingredient.of(Items.SUGAR),
                        'M', Ingredient.of(Items.MILK_BUCKET)
                ), "BSB", "WEW", "MMM");


        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.HEMP_STICK.get())
                .pattern("  F")
                .pattern(" S ")
                .pattern("F  ")
                .define('S', Items.STICK)
                .define('F', ModItems.HEMP_FIBERS)
                .unlockedBy(getHasName(ModItems.HEMP_FIBERS), has(ModItems.HEMP_FIBERS))
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(SmokeleafIndustries.MODID, "hemp_stick"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.HEMP_STICK.get())
                .pattern("F  ")
                .pattern(" S ")
                .pattern("  F")
                .define('S', Items.STICK)
                .define('F', ModItems.HEMP_FIBERS)
                .unlockedBy(getHasName(ModItems.HEMP_FIBERS), has(ModItems.HEMP_FIBERS))
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(SmokeleafIndustries.MODID, "hemp_stick_alt"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.HEMP_HAMMER.get())
                .pattern(" CF")
                .pattern(" SC")
                .pattern("S  ")
                .define('S', ModItems.HEMP_STICK)
                .define('F', ModItems.HEMP_FIBERS)
                .define('C', Items.COPPER_INGOT)
                .unlockedBy(getHasName(ModItems.HEMP_FIBERS), has(ModItems.HEMP_FIBERS))
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(SmokeleafIndustries.MODID, "hemp_hammer"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.HEMP_STICK.get())
                .pattern("P")
                .pattern("P")
                .define('P', ModBlocks.HEMP_PLANKS)
                .unlockedBy(getHasName(ModBlocks.HEMP_PLANKS), has(ModBlocks.HEMP_PLANKS))
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(SmokeleafIndustries.MODID, "hemp_stick_from_planks"));


        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.BIO_COMPOSITE.get())
                .requires(ModItems.HEMP_HAMMER)
                .requires(ModItems.HEMP_LEAF, 3)
                .requires(ModItems.HEMP_FIBERS, 1)
                .unlockedBy(getHasName(ModItems.HEMP_HAMMER), has(ModItems.HEMP_HAMMER))
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(SmokeleafIndustries.MODID, "hemp_plastic_from_hammering"));


        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.UNFINISHED_HEMP_CORE.get(), 2)
                .requires(ModItems.BIO_COMPOSITE)
                .requires(Items.DIAMOND)
                .unlockedBy(getHasName(ModItems.BIO_COMPOSITE), has(ModItems.BIO_COMPOSITE))
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(SmokeleafIndustries.MODID, "unfinished_hemp_core"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.HEMP_CORE.get())
                .requires(ModItems.HEMP_HAMMER)
                .requires(ModItems.HEMP_PLASTIC)
                .requires(ModItems.UNFINISHED_HEMP_CORE)
                .unlockedBy(getHasName(ModItems.UNFINISHED_HEMP_CORE), has(ModItems.UNFINISHED_HEMP_CORE))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.BONG.get())
                .pattern("  P")
                .pattern(" BP")
                .pattern("PPP")
                .define('P', Items.GLASS_PANE)
                .define('B', Items.WATER_BUCKET)
                .unlockedBy(getHasName(Items.WATER_BUCKET), has(Items.WATER_BUCKET))
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(SmokeleafIndustries.MODID, "bong_recipe"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.DAB_RIG.get())
                .pattern("P  ")
                .pattern("PBG")
                .pattern("PPP")
                .define('P', Items.GLASS_PANE)
                .define('B', Items.WATER_BUCKET)
                .define('G', Items.GLASS_BOTTLE)
                .unlockedBy(getHasName(Items.WATER_BUCKET), has(Items.WATER_BUCKET))
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(SmokeleafIndustries.MODID, "dab_rig_recipe"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.PLANT_ANALYZER.get())
                .pattern(" C ")
                .pattern("WIW")
                .pattern("PPP")
                .define('W', ModItems.HEMP_LEAF)
                .define('C', ModItems.HEMP_CORE)
                .define('I', Items.IRON_INGOT)
                .define('P', ModItems.HEMP_PLASTIC)
                .unlockedBy(getHasName(ModItems.HEMP_CORE), has(ModItems.HEMP_CORE))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.LED_LIGHT.get())
                .pattern("PLP")
                .pattern("RGB")
                .pattern("CAC")
                .define('P', ModItems.HEMP_PLASTIC)
                .define('L', ModItems.HEMP_CORE)
                .define('R', Items.RED_DYE)
                .define('G', Items.GREEN_DYE)
                .define('B', Items.BLUE_DYE)
                .define('C', Items.NETHER_STAR)
                .define('A', Items.TINTED_GLASS)
                .unlockedBy(getHasName(ModItems.HEMP_CORE), has(ModItems.HEMP_CORE))
                .save(recipeOutput);


        // Machine Recipes
        // Generator
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.GENERATOR.get())
                .pattern("HIH")
                .pattern("CRC")
                .pattern("HIH")
                .define('H', ModItems.HEMP_PLASTIC)
                .define('C', ModItems.HEMP_CORE)
                .define('I', Items.IRON_INGOT)
                .define('R', Items.FURNACE)
                .unlockedBy(getHasName(ModItems.HEMP_CORE), has(ModItems.HEMP_CORE))
                .save(recipeOutput);

        // Grinder
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.GRINDER.get())
                .pattern("HIH")
                .pattern("CGC")
                .pattern("HIH")
                .define('H', ModItems.HEMP_PLASTIC)
                .define('C', ModItems.HEMP_CORE)
                .define('I', Items.IRON_INGOT)
                .define('G', Items.GRINDSTONE)
                .unlockedBy(getHasName(ModItems.HEMP_CORE), has(ModItems.HEMP_CORE))
                .save(recipeOutput);

        // Extractor
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.EXTRACTOR.get())
                .pattern("HCH")
                .pattern("IGI")
                .pattern("HCH")
                .define('H', ModItems.HEMP_PLASTIC)
                .define('C', ModItems.HEMP_CORE)
                .define('I', Items.IRON_INGOT)
                .define('G', Items.GLASS_BOTTLE)
                .unlockedBy(getHasName(ModItems.HEMP_CORE), has(ModItems.HEMP_CORE))
                .save(recipeOutput);

        // Liquifier
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.LIQUIFIER.get())
                .pattern("HCH")
                .pattern("IBI")
                .pattern("HCH")
                .define('H', ModItems.HEMP_PLASTIC)
                .define('C', ModItems.HEMP_CORE)
                .define('I', Items.GOLD_INGOT)
                .define('B', Items.BUCKET)
                .unlockedBy(getHasName(ModItems.HEMP_CORE), has(ModItems.HEMP_CORE))
                .save(recipeOutput);

        // Mutator
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.MUTATOR.get())
                .pattern("HCH")
                .pattern("IGI")
                .pattern("HCH")
                .define('H', ModItems.HEMP_PLASTIC)
                .define('C', ModItems.HEMP_CORE)
                .define('I', ModItems.HEMP_STICK)
                .define('G', Items.DIAMOND_BLOCK)
                .unlockedBy(getHasName(ModItems.HEMP_CORE), has(ModItems.HEMP_CORE))
                .save(recipeOutput);

        // Synthesizer
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.SYNTHESIZER.get())
                .pattern("HCH")
                .pattern("IGI")
                .pattern("HCH")
                .define('H', ModItems.HEMP_PLASTIC)
                .define('C', ModItems.HEMP_CORE)
                .define('I', Items.DIAMOND)
                .define('G', Items.IRON_BLOCK)
                .unlockedBy(getHasName(ModItems.HEMP_CORE), has(ModItems.HEMP_CORE))
                .save(recipeOutput);

        // Sequencer
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.SEQUENCER.get())
                .pattern("HCH")
                .pattern("IGI")
                .pattern("HCH")
                .define('H', ModItems.HEMP_PLASTIC)
                .define('C', ModItems.HEMP_CORE)
                .define('I', Items.GLASS_BOTTLE)
                .define('G', Items.GLOWSTONE)
                .unlockedBy(getHasName(ModItems.HEMP_CORE), has(ModItems.HEMP_CORE))
                .save(recipeOutput);

        // Mixer
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.MIXER.get())
                .pattern("HCH")
                .pattern("IGI")
                .pattern("HCH")
                .define('H', ModItems.HEMP_PLASTIC)
                .define('C', ModItems.HEMP_CORE)
                .define('I', Items.IRON_INGOT)
                .define('G', Items.CAULDRON)
                .unlockedBy(getHasName(ModItems.HEMP_CORE), has(ModItems.HEMP_CORE))
                .save(recipeOutput);

        // Strain Modifier
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.STRAIN_MODIFIER.get())
                .pattern("HCH")
                .pattern("IGI")
                .pattern("HCH")
                .define('H', ModItems.HEMP_PLASTIC)
                .define('C', ModItems.HEMP_CORE)
                .define('I', Items.IRON_INGOT)
                .define('G', Items.COMPARATOR)
                .unlockedBy(getHasName(ModItems.HEMP_CORE), has(ModItems.HEMP_CORE))
                .save(recipeOutput);

        // Grow Pot
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.GROW_POT.get())
                .pattern("H H")
                .pattern("HCH")
                .pattern("HBH")
                .define('H', ModItems.HEMP_PLASTIC)
                .define('C', ModItems.HEMP_CORE)
                .define('B', Items.WATER_BUCKET)
                .unlockedBy(getHasName(ModItems.HEMP_CORE), has(ModItems.HEMP_CORE))
                .save(recipeOutput);

        // Dryer
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.DRYER.get())
                .pattern("HCH")
                .pattern("IGI")
                .pattern("HCH")
                .define('H', ModItems.HEMP_PLASTIC)
                .define('C', ModItems.HEMP_CORE)
                .define('I', Items.MAGMA_BLOCK)
                .define('G', ModBlocks.DRYING_RACK)
                .unlockedBy(getHasName(ModBlocks.DRYING_RACK), has(ModBlocks.DRYING_RACK))
                .save(recipeOutput);

        // Confectioner (Gummy Machine) — previously an all-vanilla iron/hopper/redstone recipe with
        // no machine-tier gate at all; brought in line with every other machine's Hemp Plastic/Hemp
        // Core frame.
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.GUMMY_MACHINE.get())
                .pattern("HCH")
                .pattern("IGI")
                .pattern("HCH")
                .define('H', ModItems.HEMP_PLASTIC)
                .define('C', ModItems.HEMP_CORE)
                .define('I', Items.IRON_INGOT)
                .define('G', Items.HOPPER)
                .unlockedBy(getHasName(ModItems.HEMP_CORE), has(ModItems.HEMP_CORE))
                .save(recipeOutput);



        // Stonecutting Recipes
        SingleItemRecipeBuilder.stonecutting(
                        Ingredient.of(ModBlocks.HEMP_STONE.get()),
                        RecipeCategory.BUILDING_BLOCKS,
                        ModBlocks.HEMP_BRICKS.get())
                .unlockedBy(getHasName(ModBlocks.HEMP_STONE.get()), has(ModBlocks.HEMP_STONE.get()))
                .save(recipeOutput, "smokeleafindustries:stonecutting/hemp_bricks_from_hemp_stone");

        SingleItemRecipeBuilder.stonecutting(
                Ingredient.of(ModBlocks.HEMP_STONE.get()),
                RecipeCategory.BUILDING_BLOCKS,
                ModBlocks.HEMP_CHISELED_STONE.get())
                .unlockedBy(getHasName(ModBlocks.HEMP_STONE.get()), has(ModBlocks.HEMP_STONE.get()))
                .save(recipeOutput, "smokeleafindustries:stonecutting/hemp_chiseled_stone_from_hemp_stone");

        SingleItemRecipeBuilder.stonecutting(
                        Ingredient.of(ModBlocks.HEMP_BRICKS.get()),
                        RecipeCategory.BUILDING_BLOCKS,
                        ModBlocks.HEMP_CHISELED_STONE.get())
                .unlockedBy(getHasName(ModBlocks.HEMP_BRICKS.get()), has(ModBlocks.HEMP_BRICKS.get()))
                .save(recipeOutput, "smokeleafindustries:stonecutting/hemp_chiseled_stone_from_hemp_bricks");


        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.WORM_CASTINGS.get(), 2)
                .requires(Items.DIRT)
                .requires(Items.BONE_MEAL)
                .requires(Items.ROTTEN_FLESH)
                .requires(ModItems.COMPOST)
                .unlockedBy(getHasName(ModItems.COMPOST), has(ModItems.COMPOST))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.COMPOST.get(), 2)
                .requires(Items.OAK_LEAVES)
                .requires(ModItems.HEMP_SEEDS)
                .requires(ModTags.LEAVES)
                .requires(ModItems.COMPOST)
                .unlockedBy(getHasName(ModItems.HEMP_SEEDS), has(ModItems.HEMP_SEEDS))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.MYCORRHIZAE.get(), 2)
                .requires(Items.RED_MUSHROOM)
                .requires(Items.BROWN_MUSHROOM)
                .requires(Items.BONE_MEAL)
                .requires(ModItems.BIO_COMPOSITE)
                .unlockedBy(getHasName(ModItems.BIO_COMPOSITE), has(ModItems.BIO_COMPOSITE))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.DOLOMITE_LIME.get(), 2)
                .requires(Items.CALCITE)
                .requires(Items.BONE_MEAL)
                .requires(Items.SAND)
                .unlockedBy(getHasName(Items.CALCITE), has(Items.CALCITE))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.BLOOD_MEAL.get(), 2)
                .requires(Items.ROTTEN_FLESH)
                .requires(ModItems.CAT_URINE_BOTTLE)
                .requires(Items.BONE_MEAL)
                .unlockedBy(getHasName(ModItems.CAT_URINE_BOTTLE), has(ModItems.CAT_URINE_BOTTLE))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.PHOSPHORUS_POWDER.get(), 2)
                .requires(Items.GLOWSTONE_DUST)
                .requires(ModItems.BIO_COMPOSITE)
                .requires(Items.BONE_MEAL)
                .unlockedBy(getHasName(ModItems.BIO_COMPOSITE), has(ModItems.BIO_COMPOSITE))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.BAT_GUANO.get(), 2)
                .requires(Items.BONE_MEAL)
                .requires(Items.CHARCOAL)
                .requires(ModItems.COMPOST)
                .unlockedBy(getHasName(ModItems.COMPOST), has(ModItems.COMPOST))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.KELP_MEAL.get(), 2)
                .requires(Items.DRIED_KELP)
                .requires(Items.DRIED_KELP)
                .requires(ModItems.TOBACCO_LEAF)
                .unlockedBy(getHasName(ModItems.TOBACCO_LEAF), has(ModItems.TOBACCO_LEAF))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.WOOD_ASH.get(), 2)
                .requires(Items.GRAY_CONCRETE_POWDER)
                .requires(Items.CHARCOAL)
                .requires(Items.GUNPOWDER)
                .unlockedBy(getHasName(Items.GRAY_CONCRETE_POWDER), has(Items.GRAY_CONCRETE_POWDER))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.EMPTY_VIAL.get())
                .pattern("P P")
                .pattern("P P")
                .pattern(" G ")
                .define('P', Items.GLASS_PANE)
                .define('G', Items.WHITE_STAINED_GLASS)
                .unlockedBy(getHasName(Items.WHITE_STAINED_GLASS), has(Items.WHITE_STAINED_GLASS))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.FISH_EMULSION.get(), 2)
                .requires(ModItems.EMPTY_VIAL)
                .requires(Items.TROPICAL_FISH)
                .requires(Items.COD)
                .requires(Items.SALMON)
                .unlockedBy(getHasName(ModItems.EMPTY_VIAL), has(ModItems.EMPTY_VIAL))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.BLOOM_BOOSTER.get(), 2)
                .requires(ModItems.EMPTY_VIAL)
                .requires(Items.ALLIUM)
                .requires(Items.POPPY)
                .requires(Items.DANDELION)
                .unlockedBy(getHasName(ModItems.EMPTY_VIAL), has(ModItems.EMPTY_VIAL))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.FRUIT_FINISHER.get(), 2)
                .requires(ModItems.EMPTY_VIAL)
                .requires(Items.APPLE)
                .requires(Items.MELON_SLICE)
                .requires(Items.SWEET_BERRIES)
                .unlockedBy(getHasName(ModItems.EMPTY_VIAL), has(ModItems.EMPTY_VIAL))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.NITROGEN_BOOST.get(), 2)
                .requires(ModItems.EMPTY_VIAL)
                .requires(ModItems.CAT_URINE_BOTTLE)
                .requires(Items.TORCHFLOWER)
                .unlockedBy(getHasName(ModItems.EMPTY_VIAL), has(ModItems.EMPTY_VIAL))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.POTASH_BOOST.get(), 2)
                .requires(ModItems.EMPTY_VIAL)
                .requires(ModItems.WOOD_ASH)
                .requires(Items.BONE_MEAL)
                .unlockedBy(getHasName(ModItems.EMPTY_VIAL), has(ModItems.EMPTY_VIAL))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.BALANCED_BOOST.get(), 2)
                .requires(ModItems.EMPTY_VIAL)
                .requires(Items.CRIMSON_FUNGUS)
                .requires(Items.VINE)
                .unlockedBy(getHasName(ModItems.EMPTY_VIAL), has(ModItems.EMPTY_VIAL))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.PHOSPHORUS_REDUCER.get(), 2)
                .requires(ModItems.EMPTY_VIAL)
                .requires(Items.BLUE_ORCHID)
                .requires(Items.SUGAR)
                .unlockedBy(getHasName(ModItems.EMPTY_VIAL), has(ModItems.EMPTY_VIAL))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.POTASSIUM_REDUCER.get(), 2)
                .requires(ModItems.EMPTY_VIAL)
                .requires(Items.CHORUS_FRUIT)
                .requires(Items.GLOW_BERRIES)
                .unlockedBy(getHasName(ModItems.EMPTY_VIAL), has(ModItems.EMPTY_VIAL))
                .save(recipeOutput);

        // --- Logistics Pipes ---
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.ITEM_PIPE.get(), 4)
                .pattern("PPP")
                .pattern("   ")
                .pattern("PPP")
                .define('P', ModItems.HEMP_PLASTIC)
                .unlockedBy(getHasName(ModItems.HEMP_PLASTIC), has(ModItems.HEMP_PLASTIC))
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(SmokeleafIndustries.MODID, "item_pipe"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.FLUID_PIPE.get(), 4)
                .pattern("PPP")
                .pattern("LLL")
                .pattern("PPP")
                .define('P', ModItems.HEMP_PLASTIC)
                .define('L', Items.LAPIS_LAZULI)
                .unlockedBy(getHasName(ModItems.HEMP_PLASTIC), has(ModItems.HEMP_PLASTIC))
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(SmokeleafIndustries.MODID, "fluid_pipe"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.ENERGY_PIPE.get(), 4)
                .pattern("PPP")
                .pattern("RRR")
                .pattern("PPP")
                .define('P', ModItems.HEMP_PLASTIC)
                .define('R', Items.REDSTONE)
                .unlockedBy(getHasName(ModItems.HEMP_PLASTIC), has(ModItems.HEMP_PLASTIC))
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(SmokeleafIndustries.MODID, "energy_pipe"));

        // Same shape/ingredients as the Hemp Hammer (2 Copper Ingot, Hemp Stick, Hemp Fibers),
        // with one Hemp Stick swapped for Hemp Fabric.
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.PIPE_WRENCH.get())
                .pattern(" CF")
                .pattern(" HC")
                .pattern("S  ")
                .define('S', ModItems.HEMP_STICK)
                .define('H', ModItems.HEMP_FABRIC)
                .define('F', ModItems.HEMP_FIBERS)
                .define('C', Items.COPPER_INGOT)
                .unlockedBy(getHasName(ModItems.HEMP_FABRIC), has(ModItems.HEMP_FABRIC))
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(SmokeleafIndustries.MODID, "pipe_wrench"));

        // Hemp Wool — 4 Hemp Fabric makes the plain (white) block, then any Hemp Wool + a dye
        // recolors it, exactly like vanilla wool + dye.
        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, ModBlocks.HEMP_WOOL.get(DyeColor.WHITE).get())
                .requires(ModItems.HEMP_FABRIC, 4)
                .unlockedBy(getHasName(ModItems.HEMP_FABRIC), has(ModItems.HEMP_FABRIC))
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(SmokeleafIndustries.MODID, "hemp_wool"));

        for (DyeColor color : DyeColor.values()) {
            var coloredWool = ModBlocks.HEMP_WOOL.get(color).get();
            ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, coloredWool)
                    .requires(ModTags.HEMP_WOOL)
                    .requires(DyeItem.byColor(color))
                    .unlockedBy("has_hemp_wool", has(ModTags.HEMP_WOOL))
                    .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(SmokeleafIndustries.MODID,
                            "dye_" + ModBlocks.hempWoolName(color)));
        }

        // Baja Hoodie — woven from Hemp Fabric, same shapes as vanilla leather armor.
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.BAJA_HOODIE_HELMET.get())
                .pattern("XXX")
                .pattern("X X")
                .define('X', ModItems.HEMP_FABRIC)
                .unlockedBy(getHasName(ModItems.HEMP_FABRIC), has(ModItems.HEMP_FABRIC))
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(SmokeleafIndustries.MODID, "baja_hoodie_helmet"));
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.BAJA_HOODIE_CHESTPLATE.get())
                .pattern("X X")
                .pattern("XXX")
                .pattern("XXX")
                .define('X', ModItems.HEMP_FABRIC)
                .unlockedBy(getHasName(ModItems.HEMP_FABRIC), has(ModItems.HEMP_FABRIC))
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(SmokeleafIndustries.MODID, "baja_hoodie_chestplate"));
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.BAJA_HOODIE_LEGGINGS.get())
                .pattern("XXX")
                .pattern("X X")
                .pattern("X X")
                .define('X', ModItems.HEMP_FABRIC)
                .unlockedBy(getHasName(ModItems.HEMP_FABRIC), has(ModItems.HEMP_FABRIC))
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(SmokeleafIndustries.MODID, "baja_hoodie_leggings"));
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.BAJA_HOODIE_BOOTS.get())
                .pattern("X X")
                .pattern("X X")
                .define('X', ModItems.HEMP_FABRIC)
                .unlockedBy(getHasName(ModItems.HEMP_FABRIC), has(ModItems.HEMP_FABRIC))
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(SmokeleafIndustries.MODID, "baja_hoodie_boots"));

        // Reinforced Baja Hoodie — upgrade each piece with 1 Hemp Plastic + 1 Netherite Ingot,
        // preserving enchantments/trim/durability like a smithing upgrade would.
        saveArmorUpgradeRecipe(recipeOutput, ModItems.BAJA_HOODIE_HELMET.get(), ModItems.REINFORCED_BAJA_HOODIE_HELMET.get());
        saveArmorUpgradeRecipe(recipeOutput, ModItems.BAJA_HOODIE_CHESTPLATE.get(), ModItems.REINFORCED_BAJA_HOODIE_CHESTPLATE.get());
        saveArmorUpgradeRecipe(recipeOutput, ModItems.BAJA_HOODIE_LEGGINGS.get(), ModItems.REINFORCED_BAJA_HOODIE_LEGGINGS.get());
        saveArmorUpgradeRecipe(recipeOutput, ModItems.BAJA_HOODIE_BOOTS.get(), ModItems.REINFORCED_BAJA_HOODIE_BOOTS.get());

    }

    private void saveArmorUpgradeRecipe(RecipeOutput out, net.minecraft.world.item.Item base, net.minecraft.world.item.Item upgraded) {
        ResourceLocation id = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(upgraded);
        Ingredient baseIngredient = Ingredient.of(base);
        NonNullList<Ingredient> ingredients = NonNullList.create();
        ingredients.add(baseIngredient);
        ingredients.add(Ingredient.of(ModItems.HEMP_PLASTIC));
        ingredients.add(Ingredient.of(Items.NETHERITE_INGOT));
        ArmorUpgradeShapelessRecipe recipe = new ArmorUpgradeShapelessRecipe(
                "", CraftingBookCategory.EQUIPMENT, baseIngredient, new ItemStack(upgraded), ingredients);
        out.accept(id, recipe, null);
    }

    private void saveStrainCopyShapelessRecipe(RecipeOutput out, net.minecraft.world.item.Item result, Ingredient... ingredients) {
        ResourceLocation id = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(result);
        NonNullList<Ingredient> ingList = NonNullList.create();
        java.util.Collections.addAll(ingList, ingredients);
        StrainCopyShapelessRecipe recipe = new StrainCopyShapelessRecipe(
                "", CraftingBookCategory.MISC, new ItemStack(result), ingList);
        out.accept(id, recipe, null);
    }

    private void saveStrainCopyShapedRecipe(RecipeOutput out, net.minecraft.world.item.Item result, Map<Character, Ingredient> key, String... pattern) {
        ResourceLocation id = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(result);
        ShapedRecipePattern shapedPattern = ShapedRecipePattern.of(key, List.of(pattern));
        StrainCopyShapedRecipe recipe = new StrainCopyShapedRecipe(
                "", CraftingBookCategory.MISC, shapedPattern, new ItemStack(result), false);
        out.accept(id, recipe, null);
    }

    private static void liquifierExtract(RecipeOutput out,
                                        String name,
                                        net.minecraft.world.item.Item extractItem,
                                        net.minecraft.world.level.material.Fluid extractFluid) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(SmokeleafIndustries.MODID,
                "liquifier/" + name + "_extract_to_fluid");

        LiquifierRecipe recipe = new LiquifierRecipe(
                Ingredient.of(extractItem),
                new FluidStack(extractFluid, 500),
                true
        );

        out.accept(id, recipe, null);
    }
}
