package net.micaxs.smokeleaf.compat;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.micaxs.smokeleaf.SmokeleafIndustries;
import net.micaxs.smokeleaf.block.ModBlocks;
import net.micaxs.smokeleaf.compat.jei.*;
import net.micaxs.smokeleaf.fluid.ModFluids;
import net.micaxs.smokeleaf.item.ModItems;
import net.micaxs.smokeleaf.recipe.*;
import net.micaxs.smokeleaf.screen.custom.*;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.List;

@JeiPlugin
public class JEISmokeleafInudstriesPlugin implements IModPlugin {

    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(SmokeleafIndustries.MODID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        var guiHelper = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(
                new ExtractorRecipeCategory(guiHelper),
                new GeneratorRecipeCategory(guiHelper),
                new LiquifierRecipeCategory(guiHelper),
                new GrinderRecipeCategory(guiHelper),
                new DryingRecipeCategory(guiHelper),
                new MutatorRecipeCategory(guiHelper),
                new SequencerRecipeCategory(guiHelper),
                new SynthesizerRecipeCategory(guiHelper),
                new ManualGrinderRecipeCategory(guiHelper),
                new JointRecipeCategory(guiHelper),
                new BluntRecipeCategory(guiHelper)
        );
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;
        RecipeManager recipeManager = mc.level.getRecipeManager();

        List<ExtractorRecipe> extractorRecipes =
                recipeManager.getAllRecipesFor(ModRecipes.EXTRACTOR_TYPE.get())
                        .stream().map(RecipeHolder::value).toList();
        registration.addRecipes(ExtractorRecipeCategory.EXTRACTOR_RECIPE_RECIPE_TYPE, extractorRecipes);

        List<GeneratorRecipe> generatorRecipes =
                recipeManager.getAllRecipesFor(ModRecipes.GENERATOR_TYPE.get())
                        .stream().map(RecipeHolder::value).toList();
        registration.addRecipes(GeneratorRecipeCategory.GENERATOR_RECIPE_TYPE, generatorRecipes);

        List<LiquifierRecipeCategory.Display> liquifierDisplays =
                recipeManager.getAllRecipesFor(ModRecipes.LIQUIFIER_TYPE.get())
                        .stream().map(RecipeHolder::value)
                        .flatMap(r -> LiquifierRecipeCategory.buildStrainDisplays(r).stream())
                        .collect(java.util.stream.Collectors.toList());
        registration.addRecipes(LiquifierRecipeCategory.LIQUIFIER_RECIPE_TYPE, liquifierDisplays);

        List<GrinderRecipe> grinderRecipes =
                recipeManager.getAllRecipesFor(ModRecipes.GRINDER_TYPE.get())
                        .stream().map(RecipeHolder::value).toList();
        registration.addRecipes(GrinderRecipeCategory.GRINDER_RECIPE_TYPE, grinderRecipes);

        List<DryingRecipe> dryingRecipes =
                recipeManager.getAllRecipesFor(ModRecipes.DRYING_TYPE.get())
                        .stream().map(RecipeHolder::value).toList();
        registration.addRecipes(DryingRecipeCategory.DRYING_RECIPE_TYPE, dryingRecipes);

        List<MutatorRecipe> mutatorRecipes =
                recipeManager.getAllRecipesFor(ModRecipes.MUTATOR_TYPE.get())
                        .stream().map(RecipeHolder::value).toList();
        registration.addRecipes(MutatorRecipeCategory.MUTATOR_RECIPE_TYPE, mutatorRecipes);

        List<SequencerRecipe> sequencerRecipes =
                recipeManager.getAllRecipesFor(ModRecipes.SEQUENCER_TYPE.get())
                        .stream().map(RecipeHolder::value).toList();
        registration.addRecipes(SequencerRecipeCategory.SEQUENCER_RECIPE_TYPE, sequencerRecipes);

        List<SynthesizerRecipe> synthesizer =
                recipeManager.getAllRecipesFor(ModRecipes.SYNTHESIZER_TYPE.get())
                        .stream().map(RecipeHolder::value).toList();
        var synthDisplays = synthesizer.stream()
                .flatMap(r -> SynthesizerRecipeCategory.buildValidStrainDisplays(r, sequencerRecipes).stream())
                .toList();
        registration.addRecipes(SynthesizerRecipeCategory.SYNTHESIZER_RECIPE_TYPE, synthDisplays);

        List<ManualGrinderRecipe> manualGrinderRecipes =
                recipeManager.getAllRecipesFor(ModRecipes.MANUAL_GRINDER_TYPE.get())
                        .stream().map(RecipeHolder::value).toList();
        registration.addRecipes(ManualGrinderRecipeCategory.RECIPE_TYPE, manualGrinderRecipes);

        List<JointRecipe> jointRecipes =
                recipeManager.getAllRecipesFor(RecipeType.CRAFTING).stream()
                        .map(RecipeHolder::value)
                        .filter(r -> r.getSerializer() == ModRecipes.JOINT_SERIALIZER.get())
                        .map(r -> (JointRecipe) r)
                        .toList();
        registration.addRecipes(JointRecipeCategory.JOINT_RECIPE_TYPE, jointRecipes);

        // Robust: pick all loaded BluntRecipe instances
        List<BluntRecipe> bluntRecipes =
                recipeManager.getAllRecipesFor(RecipeType.CRAFTING).stream()
                        .map(RecipeHolder::value)
                        .filter(BluntRecipe.class::isInstance)
                        .map(BluntRecipe.class::cast)
                        .toList();
        registration.addRecipes(BluntRecipeCategory.BLUNT_RECIPE_TYPE, bluntRecipes);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(ModItems.MANUAL_GRINDER.get()), ManualGrinderRecipeCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.DRYING_RACK.get()), DryingRecipeCategory.DRYING_RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.EXTRACTOR.get()), ExtractorRecipeCategory.EXTRACTOR_RECIPE_RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.GENERATOR.get()), GeneratorRecipeCategory.GENERATOR_RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.LIQUIFIER.get()), LiquifierRecipeCategory.LIQUIFIER_RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.GRINDER.get()), GrinderRecipeCategory.GRINDER_RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.MUTATOR.get()), MutatorRecipeCategory.MUTATOR_RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.SEQUENCER.get()), SequencerRecipeCategory.SEQUENCER_RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.SYNTHESIZER.get()), SynthesizerRecipeCategory.SYNTHESIZER_RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(Items.CRAFTING_TABLE), JointRecipeCategory.JOINT_RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(Items.CRAFTING_TABLE), BluntRecipeCategory.BLUNT_RECIPE_TYPE);
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(ExtractorScreen.class, 80, 30, 20, 30, ExtractorRecipeCategory.EXTRACTOR_RECIPE_RECIPE_TYPE);
        registration.addRecipeClickArea(GeneratorScreen.class, 80, 25, 20, 30, GeneratorRecipeCategory.GENERATOR_RECIPE_TYPE);
        registration.addRecipeClickArea(LiquifierScreen.class, 59, 35, 54, 16, LiquifierRecipeCategory.LIQUIFIER_RECIPE_TYPE);
        registration.addRecipeClickArea(GrinderScreen.class, 84, 30, 8, 26, GrinderRecipeCategory.GRINDER_RECIPE_TYPE);
        registration.addRecipeClickArea(MutatorScreen.class, 102, 37, 8, 18, MutatorRecipeCategory.MUTATOR_RECIPE_TYPE);
        registration.addRecipeClickArea(SequencerScreen.class, 62, 33, 37, 16, SequencerRecipeCategory.SEQUENCER_RECIPE_TYPE);
        registration.addRecipeClickArea(SynthesizerScreen.class, 130, 30, 8, 26, SynthesizerRecipeCategory.SYNTHESIZER_RECIPE_TYPE);
    }

    @Override
    public void registerItemSubtypes(mezz.jei.api.registration.ISubtypeRegistration registration) {
        mezz.jei.api.ingredients.subtypes.IIngredientSubtypeInterpreter<ItemStack> strainSubtype =
            (stack, context) -> {
                String id = stack.get(net.micaxs.smokeleaf.component.ModDataComponentTypes.STRAIN_ID.get());
                return id != null ? id : mezz.jei.api.ingredients.subtypes.IIngredientSubtypeInterpreter.NONE;
            };
        registration.registerSubtypeInterpreter(mezz.jei.api.constants.VanillaTypes.ITEM_STACK, ModItems.GENERIC_BUD.get(), strainSubtype);
        registration.registerSubtypeInterpreter(mezz.jei.api.constants.VanillaTypes.ITEM_STACK, ModItems.GENERIC_WEED.get(), strainSubtype);
        registration.registerSubtypeInterpreter(mezz.jei.api.constants.VanillaTypes.ITEM_STACK, ModItems.GENERIC_EXTRACT.get(), strainSubtype);
        registration.registerSubtypeInterpreter(mezz.jei.api.constants.VanillaTypes.ITEM_STACK, ModItems.GENERIC_SEEDS.get(), strainSubtype);
        registration.registerSubtypeInterpreter(mezz.jei.api.constants.VanillaTypes.ITEM_STACK, ModFluids.UNIDENTIFIED_MIXTURE_BUCKET.get(), strainSubtype);
    }
}
