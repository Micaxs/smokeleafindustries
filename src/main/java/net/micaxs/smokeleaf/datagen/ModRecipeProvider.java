package net.micaxs.smokeleaf.datagen;

import net.micaxs.smokeleaf.SmokeleafIndustries;
import net.micaxs.smokeleaf.block.ModBlocks;
import net.micaxs.smokeleaf.fluid.ModFluids;
import net.micaxs.smokeleaf.item.ModItems;
import net.micaxs.smokeleaf.utils.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;
import net.micaxs.smokeleaf.recipe.LiquifierRecipe;
import net.neoforged.neoforge.fluids.FluidStack;

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

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.INFUSED_BUTTER.get())
                .requires(ModItems.BUTTER)
                .requires(ModTags.WEEDS)
                .unlockedBy(getHasName(ModItems.BUTTER), has(ModItems.BUTTER))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.HEMP_PLASTIC.get(), 4)
                .requires(ModFluids.HEMP_OIL_BUCKET)
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
                .define('B', ModFluids.HEMP_OIL_BUCKET)
                .unlockedBy(getHasName(ModItems.HEMP_PLASTIC), has(ModItems.HEMP_PLASTIC))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.HEMP_STONE.get(), 8)
                .pattern("PPP")
                .pattern("PBP")
                .pattern("PPP")
                .define('P', Items.STONE)
                .define('B', ModFluids.HEMP_OIL_BUCKET)
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

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.WEED_COOKIE.get())
                .requires(Items.COOKIE)
                .requires(ModItems.INFUSED_BUTTER)
                .unlockedBy(getHasName(ModItems.INFUSED_BUTTER), has(ModItems.INFUSED_BUTTER))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.HASH_BROWNIE.get())
                .pattern("WBW")
                .pattern("CHC")
                .pattern("WBW")
                .define('C', Items.COCOA_BEANS)
                .define('W', ModTags.WEEDS)
                .define('B', ModItems.INFUSED_BUTTER)
                .define('H', ModFluids.HASH_OIL_BUCKET)
                .unlockedBy(getHasName(ModFluids.HASH_OIL_BUCKET), has(ModFluids.HASH_OIL_BUCKET))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.HERB_CAKE.get())
                .pattern("BSB")
                .pattern("WEW")
                .pattern("MMM")
                .define('E', Items.EGG)
                .define('S', Items.SUGAR)
                .define('W', ModTags.WEEDS)
                .define('B', ModItems.INFUSED_BUTTER)
                .define('M', Items.MILK_BUCKET)
                .unlockedBy(getHasName(ModItems.INFUSED_BUTTER), has(ModItems.INFUSED_BUTTER))
                .save(recipeOutput);


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


        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.UNFINISHED_HEMP_CORE.get())
                .pattern("RAR")
                .pattern("GDG")
                .pattern("RAR")
                .define('R', Items.REDSTONE)
                .define('G', Items.GLOWSTONE_DUST)
                .define('A', Items.AMETHYST_SHARD)
                .define('D', Items.DIAMOND)
                .unlockedBy(getHasName(Items.AMETHYST_SHARD), has(Items.AMETHYST_SHARD))
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
                .define('W', ModTags.WEEDS)
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
                .define('C', Items.GLOWSTONE)
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


        // Filled Bag Recipes
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.WHITE_WIDOW_BAG.get())
                .pattern("BWW")
                .pattern("WWW")
                .pattern("WWW")
                .define('B', ModItems.EMPTY_BAG)
                .define('W', ModItems.WHITE_WIDOW_WEED)
                .unlockedBy(getHasName(ModItems.EMPTY_BAG), has(ModItems.EMPTY_BAG))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.BUBBLE_KUSH_BAG.get())
                .pattern("BWW")
                .pattern("WWW")
                .pattern("WWW")
                .define('B', ModItems.EMPTY_BAG)
                .define('W', ModItems.BUBBLE_KUSH_WEED)
                .unlockedBy(getHasName(ModItems.EMPTY_BAG), has(ModItems.EMPTY_BAG))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.LEMON_HAZE_BAG.get())
                .pattern("BWW")
                .pattern("WWW")
                .pattern("WWW")
                .define('B', ModItems.EMPTY_BAG)
                .define('W', ModItems.LEMON_HAZE_WEED)
                .unlockedBy(getHasName(ModItems.EMPTY_BAG), has(ModItems.EMPTY_BAG))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.SOUR_DIESEL_BAG.get())
                .pattern("BWW")
                .pattern("WWW")
                .pattern("WWW")
                .define('B', ModItems.EMPTY_BAG)
                .define('W', ModItems.SOUR_DIESEL_WEED)
                .unlockedBy(getHasName(ModItems.EMPTY_BAG), has(ModItems.EMPTY_BAG))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.BLUE_ICE_BAG.get())
                .pattern("BWW")
                .pattern("WWW")
                .pattern("WWW")
                .define('B', ModItems.EMPTY_BAG)
                .define('W', ModItems.BLUE_ICE_WEED)
                .unlockedBy(getHasName(ModItems.EMPTY_BAG), has(ModItems.EMPTY_BAG))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.BUBBLEGUM_BAG.get())
                .pattern("BWW")
                .pattern("WWW")
                .pattern("WWW")
                .define('B', ModItems.EMPTY_BAG)
                .define('W', ModItems.BUBBLEGUM_WEED)
                .unlockedBy(getHasName(ModItems.EMPTY_BAG), has(ModItems.EMPTY_BAG))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.PURPLE_HAZE_BAG.get())
                .pattern("BWW")
                .pattern("WWW")
                .pattern("WWW")
                .define('B', ModItems.EMPTY_BAG)
                .define('W', ModItems.PURPLE_HAZE_WEED)
                .unlockedBy(getHasName(ModItems.EMPTY_BAG), has(ModItems.EMPTY_BAG))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.OG_KUSH_BAG.get())
                .pattern("BWW")
                .pattern("WWW")
                .pattern("WWW")
                .define('B', ModItems.EMPTY_BAG)
                .define('W', ModItems.OG_KUSH_WEED)
                .unlockedBy(getHasName(ModItems.EMPTY_BAG), has(ModItems.EMPTY_BAG))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.JACK_HERER_BAG.get())
                .pattern("BWW")
                .pattern("WWW")
                .pattern("WWW")
                .define('B', ModItems.EMPTY_BAG)
                .define('W', ModItems.JACK_HERER_WEED)
                .unlockedBy(getHasName(ModItems.EMPTY_BAG), has(ModItems.EMPTY_BAG))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.GARY_PEYTON_BAG.get())
                .pattern("BWW")
                .pattern("WWW")
                .pattern("WWW")
                .define('B', ModItems.EMPTY_BAG)
                .define('W', ModItems.GARY_PEYTON_WEED)
                .unlockedBy(getHasName(ModItems.EMPTY_BAG), has(ModItems.EMPTY_BAG))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.AMNESIA_HAZE_BAG.get())
                .pattern("BWW")
                .pattern("WWW")
                .pattern("WWW")
                .define('B', ModItems.EMPTY_BAG)
                .define('W', ModItems.AMNESIA_HAZE_WEED)
                .unlockedBy(getHasName(ModItems.EMPTY_BAG), has(ModItems.EMPTY_BAG))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.AK47_BAG.get())
                .pattern("BWW")
                .pattern("WWW")
                .pattern("WWW")
                .define('B', ModItems.EMPTY_BAG)
                .define('W', ModItems.AK47_WEED)
                .unlockedBy(getHasName(ModItems.EMPTY_BAG), has(ModItems.EMPTY_BAG))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.GHOST_TRAIN_BAG.get())
                .pattern("BWW")
                .pattern("WWW")
                .pattern("WWW")
                .define('B', ModItems.EMPTY_BAG)
                .define('W', ModItems.GHOST_TRAIN_WEED)
                .unlockedBy(getHasName(ModItems.EMPTY_BAG), has(ModItems.EMPTY_BAG))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.GRAPE_APE_BAG.get())
                .pattern("BWW")
                .pattern("WWW")
                .pattern("WWW")
                .define('B', ModItems.EMPTY_BAG)
                .define('W', ModItems.GRAPE_APE_WEED)
                .unlockedBy(getHasName(ModItems.EMPTY_BAG), has(ModItems.EMPTY_BAG))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.COTTON_CANDY_BAG.get())
                .pattern("BWW")
                .pattern("WWW")
                .pattern("WWW")
                .define('B', ModItems.EMPTY_BAG)
                .define('W', ModItems.COTTON_CANDY_WEED)
                .unlockedBy(getHasName(ModItems.EMPTY_BAG), has(ModItems.EMPTY_BAG))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.BANANA_KUSH_BAG.get())
                .pattern("BWW")
                .pattern("WWW")
                .pattern("WWW")
                .define('B', ModItems.EMPTY_BAG)
                .define('W', ModItems.BANANA_KUSH_WEED)
                .unlockedBy(getHasName(ModItems.EMPTY_BAG), has(ModItems.EMPTY_BAG))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.CARBON_FIBER_BAG.get())
                .pattern("BWW")
                .pattern("WWW")
                .pattern("WWW")
                .define('B', ModItems.EMPTY_BAG)
                .define('W', ModItems.CARBON_FIBER_WEED)
                .unlockedBy(getHasName(ModItems.EMPTY_BAG), has(ModItems.EMPTY_BAG))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.BIRTHDAY_CAKE_BAG.get())
                .pattern("BWW")
                .pattern("WWW")
                .pattern("WWW")
                .define('B', ModItems.EMPTY_BAG)
                .define('W', ModItems.BIRTHDAY_CAKE_WEED)
                .unlockedBy(getHasName(ModItems.EMPTY_BAG), has(ModItems.EMPTY_BAG))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.BLUE_COOKIES_BAG.get())
                .pattern("BWW")
                .pattern("WWW")
                .pattern("WWW")
                .define('B', ModItems.EMPTY_BAG)
                .define('W', ModItems.BLUE_COOKIES_WEED)
                .unlockedBy(getHasName(ModItems.EMPTY_BAG), has(ModItems.EMPTY_BAG))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.AFGHANI_BAG.get())
                .pattern("BWW")
                .pattern("WWW")
                .pattern("WWW")
                .define('B', ModItems.EMPTY_BAG)
                .define('W', ModItems.AFGHANI_WEED)
                .unlockedBy(getHasName(ModItems.EMPTY_BAG), has(ModItems.EMPTY_BAG))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.MOONBOW_BAG.get())
                .pattern("BWW")
                .pattern("WWW")
                .pattern("WWW")
                .define('B', ModItems.EMPTY_BAG)
                .define('W', ModItems.MOONBOW_WEED)
                .unlockedBy(getHasName(ModItems.EMPTY_BAG), has(ModItems.EMPTY_BAG))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.LAVA_CAKE_BAG.get())
                .pattern("BWW")
                .pattern("WWW")
                .pattern("WWW")
                .define('B', ModItems.EMPTY_BAG)
                .define('W', ModItems.LAVA_CAKE_WEED)
                .unlockedBy(getHasName(ModItems.EMPTY_BAG), has(ModItems.EMPTY_BAG))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.JELLY_RANCHER_BAG.get())
                .pattern("BWW")
                .pattern("WWW")
                .pattern("WWW")
                .define('B', ModItems.EMPTY_BAG)
                .define('W', ModItems.JELLY_RANCHER_WEED)
                .unlockedBy(getHasName(ModItems.EMPTY_BAG), has(ModItems.EMPTY_BAG))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.STRAWBERRY_SHORTCAKE_BAG.get())
                .pattern("BWW")
                .pattern("WWW")
                .pattern("WWW")
                .define('B', ModItems.EMPTY_BAG)
                .define('W', ModItems.STRAWBERRY_SHORTCAKE_WEED)
                .unlockedBy(getHasName(ModItems.EMPTY_BAG), has(ModItems.EMPTY_BAG))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.PINK_KUSH_BAG.get())
                .pattern("BWW")
                .pattern("WWW")
                .pattern("WWW")
                .define('B', ModItems.EMPTY_BAG)
                .define('W', ModItems.PINK_KUSH_WEED)
                .unlockedBy(getHasName(ModItems.EMPTY_BAG), has(ModItems.EMPTY_BAG))
                .save(recipeOutput);

        // Filled bag to Weed Recipes
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.WHITE_WIDOW_WEED.get(), 8)
                .requires(ModItems.WHITE_WIDOW_BAG)
                .unlockedBy(getHasName(ModItems.WHITE_WIDOW_BAG), has(ModItems.WHITE_WIDOW_BAG))
                .save(recipeOutput);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.BUBBLE_KUSH_WEED.get(), 8)
                .requires(ModItems.BUBBLE_KUSH_BAG)
                .unlockedBy(getHasName(ModItems.BUBBLE_KUSH_BAG), has(ModItems.BUBBLE_KUSH_BAG))
                .save(recipeOutput);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.LEMON_HAZE_WEED.get(), 8)
                .requires(ModItems.LEMON_HAZE_BAG)
                .unlockedBy(getHasName(ModItems.LEMON_HAZE_BAG), has(ModItems.LEMON_HAZE_BAG))
                .save(recipeOutput);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.SOUR_DIESEL_WEED.get(), 8)
                .requires(ModItems.SOUR_DIESEL_BAG)
                .unlockedBy(getHasName(ModItems.SOUR_DIESEL_BAG), has(ModItems.SOUR_DIESEL_BAG))
                .save(recipeOutput);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.BLUE_ICE_WEED.get(), 8)
                .requires(ModItems.BLUE_ICE_BAG)
                .unlockedBy(getHasName(ModItems.BLUE_ICE_BAG), has(ModItems.BLUE_ICE_BAG))
                .save(recipeOutput);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.BUBBLEGUM_WEED.get(), 8)
                .requires(ModItems.BUBBLEGUM_BAG)
                .unlockedBy(getHasName(ModItems.BUBBLEGUM_BAG), has(ModItems.BUBBLEGUM_BAG))
                .save(recipeOutput);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.PURPLE_HAZE_WEED.get(), 8)
                .requires(ModItems.PURPLE_HAZE_BAG)
                .unlockedBy(getHasName(ModItems.PURPLE_HAZE_BAG), has(ModItems.PURPLE_HAZE_BAG))
                .save(recipeOutput);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.OG_KUSH_WEED.get(), 8)
                .requires(ModItems.OG_KUSH_BAG)
                .unlockedBy(getHasName(ModItems.OG_KUSH_BAG), has(ModItems.OG_KUSH_BAG))
                .save(recipeOutput);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.JACK_HERER_WEED.get(), 8)
                .requires(ModItems.JACK_HERER_BAG)
                .unlockedBy(getHasName(ModItems.JACK_HERER_BAG), has(ModItems.JACK_HERER_BAG))
                .save(recipeOutput);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.GARY_PEYTON_WEED.get(), 8)
                .requires(ModItems.GARY_PEYTON_BAG)
                .unlockedBy(getHasName(ModItems.GARY_PEYTON_BAG), has(ModItems.GARY_PEYTON_BAG))
                .save(recipeOutput);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.AMNESIA_HAZE_WEED.get(), 8)
                .requires(ModItems.AMNESIA_HAZE_BAG)
                .unlockedBy(getHasName(ModItems.AMNESIA_HAZE_BAG), has(ModItems.AMNESIA_HAZE_BAG))
                .save(recipeOutput);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.AK47_WEED.get(), 8)
                .requires(ModItems.AK47_BAG)
                .unlockedBy(getHasName(ModItems.AK47_BAG), has(ModItems.AK47_BAG))
                .save(recipeOutput);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.GHOST_TRAIN_WEED.get(), 8)
                .requires(ModItems.GHOST_TRAIN_BAG)
                .unlockedBy(getHasName(ModItems.GHOST_TRAIN_BAG), has(ModItems.GHOST_TRAIN_BAG))
                .save(recipeOutput);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.GRAPE_APE_WEED.get(), 8)
                .requires(ModItems.GRAPE_APE_BAG)
                .unlockedBy(getHasName(ModItems.GRAPE_APE_BAG), has(ModItems.GRAPE_APE_BAG))
                .save(recipeOutput);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.COTTON_CANDY_WEED.get(), 8)
                .requires(ModItems.COTTON_CANDY_BAG)
                .unlockedBy(getHasName(ModItems.COTTON_CANDY_BAG), has(ModItems.COTTON_CANDY_BAG))
                .save(recipeOutput);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.BANANA_KUSH_WEED.get(), 8)
                .requires(ModItems.BANANA_KUSH_BAG)
                .unlockedBy(getHasName(ModItems.BANANA_KUSH_BAG), has(ModItems.BANANA_KUSH_BAG))
                .save(recipeOutput);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.CARBON_FIBER_WEED.get(), 8)
                .requires(ModItems.CARBON_FIBER_BAG)
                .unlockedBy(getHasName(ModItems.CARBON_FIBER_BAG), has(ModItems.CARBON_FIBER_BAG))
                .save(recipeOutput);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.BIRTHDAY_CAKE_WEED.get(), 8)
                .requires(ModItems.BIRTHDAY_CAKE_BAG)
                .unlockedBy(getHasName(ModItems.BIRTHDAY_CAKE_BAG), has(ModItems.BIRTHDAY_CAKE_BAG))
                .save(recipeOutput);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.BLUE_COOKIES_WEED.get(), 8)
                .requires(ModItems.BLUE_COOKIES_BAG)
                .unlockedBy(getHasName(ModItems.BLUE_COOKIES_BAG), has(ModItems.BLUE_COOKIES_BAG))
                .save(recipeOutput);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.AFGHANI_WEED.get(), 8)
                .requires(ModItems.AFGHANI_BAG)
                .unlockedBy(getHasName(ModItems.AFGHANI_BAG), has(ModItems.AFGHANI_BAG))
                .save(recipeOutput);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.MOONBOW_WEED.get(), 8)
                .requires(ModItems.MOONBOW_BAG)
                .unlockedBy(getHasName(ModItems.MOONBOW_BAG), has(ModItems.MOONBOW_BAG))
                .save(recipeOutput);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.LAVA_CAKE_WEED.get(), 8)
                .requires(ModItems.LAVA_CAKE_BAG)
                .unlockedBy(getHasName(ModItems.LAVA_CAKE_BAG), has(ModItems.LAVA_CAKE_BAG))
                .save(recipeOutput);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.JELLY_RANCHER_WEED.get(), 8)
                .requires(ModItems.JELLY_RANCHER_BAG)
                .unlockedBy(getHasName(ModItems.JELLY_RANCHER_BAG), has(ModItems.JELLY_RANCHER_BAG))
                .save(recipeOutput);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.STRAWBERRY_SHORTCAKE_WEED.get(), 8)
                .requires(ModItems.STRAWBERRY_SHORTCAKE_BAG)
                .unlockedBy(getHasName(ModItems.STRAWBERRY_SHORTCAKE_BAG), has(ModItems.STRAWBERRY_SHORTCAKE_BAG))
                .save(recipeOutput);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.PINK_KUSH_WEED.get(), 8)
                .requires(ModItems.PINK_KUSH_BAG)
                .unlockedBy(getHasName(ModItems.PINK_KUSH_BAG), has(ModItems.PINK_KUSH_BAG))
                .save(recipeOutput);



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

        // ---- Liquifier (Extract -> Extract Fluid) ----
        // Each extract item liquifies into 500 mB of its matching extract fluid.
        // We also set inherit_input_effects so the produced fluid keeps weed effect data.
        liquifierExtract(recipeOutput, "white_widow", ModItems.WHITE_WIDOW_EXTRACT.get(), ModFluids.SOURCE_WHITE_WIDOW_EXTRACT_FLUID.get());
        liquifierExtract(recipeOutput, "bubble_kush", ModItems.BUBBLE_KUSH_EXTRACT.get(), ModFluids.SOURCE_BUBBLE_KUSH_EXTRACT_FLUID.get());
        liquifierExtract(recipeOutput, "lemon_haze", ModItems.LEMON_HAZE_EXTRACT.get(), ModFluids.SOURCE_LEMON_HAZE_EXTRACT_FLUID.get());
        liquifierExtract(recipeOutput, "sour_diesel", ModItems.SOUR_DIESEL_EXTRACT.get(), ModFluids.SOURCE_SOUR_DIESEL_EXTRACT_FLUID.get());
        liquifierExtract(recipeOutput, "blue_ice", ModItems.BLUE_ICE_EXTRACT.get(), ModFluids.SOURCE_BLUE_ICE_EXTRACT_FLUID.get());
        liquifierExtract(recipeOutput, "bubblegum", ModItems.BUBBLEGUM_EXTRACT.get(), ModFluids.SOURCE_BUBBLEGUM_EXTRACT_FLUID.get());
        liquifierExtract(recipeOutput, "purple_haze", ModItems.PURPLE_HAZE_EXTRACT.get(), ModFluids.SOURCE_PURPLE_HAZE_EXTRACT_FLUID.get());
        liquifierExtract(recipeOutput, "og_kush", ModItems.OG_KUSH_EXTRACT.get(), ModFluids.SOURCE_OG_KUSH_EXTRACT_FLUID.get());
        liquifierExtract(recipeOutput, "jack_herer", ModItems.JACK_HERER_EXTRACT.get(), ModFluids.SOURCE_JACK_HERER_EXTRACT_FLUID.get());
        liquifierExtract(recipeOutput, "gary_peyton", ModItems.GARY_PEYTON_EXTRACT.get(), ModFluids.SOURCE_GARY_PEYTON_EXTRACT_FLUID.get());
        liquifierExtract(recipeOutput, "amnesia_haze", ModItems.AMNESIA_HAZE_EXTRACT.get(), ModFluids.SOURCE_AMNESIA_HAZE_EXTRACT_FLUID.get());
        liquifierExtract(recipeOutput, "ak47", ModItems.AK47_EXTRACT.get(), ModFluids.SOURCE_AK47_EXTRACT_FLUID.get());
        liquifierExtract(recipeOutput, "ghost_train", ModItems.GHOST_TRAIN_EXTRACT.get(), ModFluids.SOURCE_GHOST_TRAIN_EXTRACT_FLUID.get());
        liquifierExtract(recipeOutput, "grape_ape", ModItems.GRAPE_APE_EXTRACT.get(), ModFluids.SOURCE_GRAPE_APE_EXTRACT_FLUID.get());
        liquifierExtract(recipeOutput, "cotton_candy", ModItems.COTTON_CANDY_EXTRACT.get(), ModFluids.SOURCE_COTTON_CANDY_EXTRACT_FLUID.get());
        liquifierExtract(recipeOutput, "banana_kush", ModItems.BANANA_KUSH_EXTRACT.get(), ModFluids.SOURCE_BANANA_KUSH_EXTRACT_FLUID.get());
        liquifierExtract(recipeOutput, "carbon_fiber", ModItems.CARBON_FIBER_EXTRACT.get(), ModFluids.SOURCE_CARBON_FIBER_EXTRACT_FLUID.get());
        liquifierExtract(recipeOutput, "birthday_cake", ModItems.BIRTHDAY_CAKE_EXTRACT.get(), ModFluids.SOURCE_BIRTHDAY_CAKE_EXTRACT_FLUID.get());
        liquifierExtract(recipeOutput, "blue_cookies", ModItems.BLUE_COOKIES_EXTRACT.get(), ModFluids.SOURCE_BLUE_COOKIES_EXTRACT_FLUID.get());
        liquifierExtract(recipeOutput, "afghani", ModItems.AFGHANI_EXTRACT.get(), ModFluids.SOURCE_AFGHANI_EXTRACT_FLUID.get());
        liquifierExtract(recipeOutput, "moonbow", ModItems.MOONBOW_EXTRACT.get(), ModFluids.SOURCE_MOONBOW_EXTRACT_FLUID.get());
        liquifierExtract(recipeOutput, "lava_cake", ModItems.LAVA_CAKE_EXTRACT.get(), ModFluids.SOURCE_LAVA_CAKE_EXTRACT_FLUID.get());
        liquifierExtract(recipeOutput, "jelly_rancher", ModItems.JELLY_RANCHER_EXTRACT.get(), ModFluids.SOURCE_JELLY_RANCHER_EXTRACT_FLUID.get());
        liquifierExtract(recipeOutput, "strawberry_shortcake", ModItems.STRAWBERRY_SHORTCAKE_EXTRACT.get(), ModFluids.SOURCE_STRAWBERRY_SHORTCAKE_EXTRACT_FLUID.get());
        liquifierExtract(recipeOutput, "pink_kush", ModItems.PINK_KUSH_EXTRACT.get(), ModFluids.SOURCE_PINK_KUSH_EXTRACT_FLUID.get());

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
