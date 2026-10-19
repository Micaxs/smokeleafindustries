package net.micaxs.smokeleaf.recipe;

import net.micaxs.smokeleaf.SmokeleafIndustries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class ModRecipes {

    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, SmokeleafIndustries.MODID);
    public static final DeferredRegister<RecipeType<?>> TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, SmokeleafIndustries.MODID);
    public static final DeferredRegister<RecipeBookCategory> BOOK_CATEGORIES = DeferredRegister.create(Registries.RECIPE_BOOK_CATEGORY, SmokeleafIndustries.MODID);


    // Generator (Item -> Energy)
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<GeneratorRecipe>> GENERATOR_SERIALIZER = SERIALIZERS.register("generator", () -> new RecipeSerializer<>(GeneratorRecipe.Serializer.CODEC, GeneratorRecipe.Serializer.STREAM_CODEC));
    public static final DeferredHolder<RecipeType<?>, RecipeType<GeneratorRecipe>> GENERATOR_TYPE = TYPES.register("generator", () -> new RecipeType<GeneratorRecipe>() {
        @Override
        public String toString() {
            return "generator";
        }
    });

    // Grinder (Bud -> Weed)
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<GrinderRecipe>> GRINDER_SERIALIZER = SERIALIZERS.register("grinder", () -> new RecipeSerializer<>(GrinderRecipe.Serializer.CODEC, GrinderRecipe.Serializer.STREAM_CODEC));
    public static final DeferredHolder<RecipeType<?>, RecipeType<GrinderRecipe>> GRINDER_TYPE = TYPES.register("grinder", () -> new RecipeType<GrinderRecipe>() {
        @Override
        public String toString() {
            return "grinder";
        }
    });

    // Extractor (Weed -> Extracts)
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<ExtractorRecipe>> EXTRACTOR_SERIALIZER = SERIALIZERS.register("extractor", () -> new RecipeSerializer<>(ExtractorRecipe.Serializer.CODEC, ExtractorRecipe.Serializer.STREAM_CODEC));
    public static final DeferredHolder<RecipeType<?>, RecipeType<ExtractorRecipe>> EXTRACTOR_TYPE = TYPES.register("extractor", () -> new RecipeType<ExtractorRecipe>() {
        @Override
        public String toString() {
            return "extractor";
        }
    });

    // Liquifier (Item -> Fluid)
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<LiquifierRecipe>> LIQUIFIER_SERIALIZER = SERIALIZERS.register("liquifier", () -> new RecipeSerializer<>(LiquifierRecipe.Serializer.CODEC, LiquifierRecipe.Serializer.STREAM_CODEC));
    public static final DeferredHolder<RecipeType<?>, RecipeType<LiquifierRecipe>> LIQUIFIER_TYPE = TYPES.register("liquifier", () -> new RecipeType<LiquifierRecipe>() {
        @Override
        public String toString() {
            return "liquifier";
        }
    });


    // Mutator (Seed + Extract + Fluid -> New Seed)
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<MutatorRecipe>> MUTATOR_SERIALIZER = SERIALIZERS.register("mutator", () -> new RecipeSerializer<>(MutatorRecipe.Serializer.CODEC, MutatorRecipe.Serializer.STREAM_CODEC));
    public static final DeferredHolder<RecipeType<?>, RecipeType<MutatorRecipe>> MUTATOR_TYPE = TYPES.register("mutator", () -> new RecipeType<MutatorRecipe>() {
        @Override
        public String toString() {
            return "mutator";
        }
    });

    // Synthesizer (Empty DNA Strand + 3 Items -> Filled DNA Strand)
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<SynthesizerRecipe>> SYNTHESIZER_SERIALIZER = SERIALIZERS.register("synthesizer", () -> new RecipeSerializer<>(SynthesizerRecipe.Serializer.CODEC, SynthesizerRecipe.Serializer.STREAM_CODEC));
    public static final DeferredHolder<RecipeType<?>, RecipeType<SynthesizerRecipe>> SYNTHESIZER_TYPE = TYPES.register("synthesizer", () -> new RecipeType<SynthesizerRecipe>() {
        @Override
        public String toString() {
            return "synthesizer";
        }
    });

    // Sequencer (Filled DNA Strand + Base Extract -> New Item)
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<SequencerRecipe>> SEQUENCER_SERIALIZER = SERIALIZERS.register("sequencer", () -> new RecipeSerializer<>(SequencerRecipe.Serializer.CODEC, SequencerRecipe.Serializer.STREAM_CODEC));
    public static final DeferredHolder<RecipeType<?>, RecipeType<SequencerRecipe>> SEQUENCER_TYPE = TYPES.register("sequencer", () -> new RecipeType<SequencerRecipe>() {
        @Override
        public String toString() {
            return "sequencer";
        }
    });

    // Drying (Bud -> Dry Bud / Tobacco Leaf -> Dried Tabacco Leaf / etc..)
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<DryingRecipe>> DRYING_SERIALIZER = SERIALIZERS.register("drying", () -> new RecipeSerializer<>(DryingRecipe.Serializer.CODEC, DryingRecipe.Serializer.STREAM_CODEC));
    public static final DeferredHolder<RecipeType<?>, RecipeType<DryingRecipe>> DRYING_TYPE = TYPES.register("drying", () -> new RecipeType<DryingRecipe>() {
        @Override
        public String toString() {
            return "drying";
        }
    });

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<LoadManualGrinderRecipe>> LOAD_MANUAL_GRINDER_SERIALIZER = SERIALIZERS.register("load_manual_grinder", () -> new RecipeSerializer<>(LoadManualGrinderRecipe.Serializer.CODEC, LoadManualGrinderRecipe.Serializer.STREAM_CODEC));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<ManualGrinderRecipe>> MANUAL_GRINDER_SERIALIZER = SERIALIZERS.register("manual_grinder", () -> new RecipeSerializer<>(ManualGrinderRecipe.Serializer.CODEC, ManualGrinderRecipe.Serializer.STREAM_CODEC));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<StrainCopyShapelessRecipe>> STRAIN_COPY_SHAPELESS_SERIALIZER = SERIALIZERS.register("strain_copy_shapeless", () -> new RecipeSerializer<>(StrainCopyShapelessRecipe.Serializer.CODEC, StrainCopyShapelessRecipe.Serializer.STREAM_CODEC));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<StrainCopyShapedRecipe>> STRAIN_COPY_SHAPED_SERIALIZER = SERIALIZERS.register("strain_copy_shaped", () -> new RecipeSerializer<>(StrainCopyShapedRecipe.Serializer.CODEC, StrainCopyShapedRecipe.Serializer.STREAM_CODEC));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<ArmorUpgradeShapelessRecipe>> ARMOR_UPGRADE_SHAPELESS_SERIALIZER = SERIALIZERS.register("armor_upgrade_shapeless", () -> new RecipeSerializer<>(ArmorUpgradeShapelessRecipe.Serializer.CODEC, ArmorUpgradeShapelessRecipe.Serializer.STREAM_CODEC));
    public static final DeferredHolder<RecipeType<?>, RecipeType<ManualGrinderRecipe>> MANUAL_GRINDER_TYPE = TYPES.register("manual_grinder", () -> new RecipeType<ManualGrinderRecipe>() {
        @Override
        public String toString() {
        return "manual_grinder";
        }
    });


    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<JointRecipe>> JOINT_SERIALIZER = SERIALIZERS.register("joint", () -> new RecipeSerializer<>(JointRecipe.Serializer.CODEC, JointRecipe.Serializer.STREAM_CODEC));
    public static final DeferredHolder<RecipeType<?>, RecipeType<JointRecipe>> JOINT_TYPE = TYPES.register("joint", () -> new RecipeType<JointRecipe>() {
        @Override public String toString() {
            return "joint";
        }
    });

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<BluntRecipe>> BLUNT_SERIALIZER = SERIALIZERS.register("blunt", () -> new RecipeSerializer<>(BluntRecipe.Serializer.CODEC, BluntRecipe.Serializer.STREAM_CODEC));
    public static final DeferredHolder<RecipeType<?>, RecipeType<BluntRecipe>> BLUNT_TYPE = TYPES.register("blunt", () -> new RecipeType<BluntRecipe>() {
        @Override public String toString() {
            return "blunt";
        }
    });

    // Gummy Machine (Mold + Oil -> Gummy)
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<GummyRecipe>> GUMMY_SERIALIZER = SERIALIZERS.register("gummy_machine", () -> new RecipeSerializer<>(GummyRecipe.Serializer.CODEC, GummyRecipe.Serializer.STREAM_CODEC));
    public static final DeferredHolder<RecipeType<?>, RecipeType<GummyRecipe>> GUMMY_TYPE = TYPES.register("gummy_machine", () -> new RecipeType<GummyRecipe>() {
        @Override
        public String toString() {
            return "gummy_machine";
        }
    });

    // Recipe book category for all machine recipes (they never show up in the recipe book, but every
    // recipe has to name one).
    public static final DeferredHolder<RecipeBookCategory, RecipeBookCategory> MACHINE_BOOK_CATEGORY = BOOK_CATEGORIES.register("machine", RecipeBookCategory::new);

    /** Every machine recipe type, synced to clients so JEI and client-side previews can see them. */
    public static List<RecipeType<?>> machineTypes() {
        return List.of(GENERATOR_TYPE.get(), GRINDER_TYPE.get(), EXTRACTOR_TYPE.get(), LIQUIFIER_TYPE.get(),
                MUTATOR_TYPE.get(), SYNTHESIZER_TYPE.get(), SEQUENCER_TYPE.get(), DRYING_TYPE.get(),
                MANUAL_GRINDER_TYPE.get(), JOINT_TYPE.get(), BLUNT_TYPE.get(), GUMMY_TYPE.get());
    }

    /**
     * Looks up a recipe on either side: the server's recipe manager, or on the client the recipes
     * synced via {@link ClientRecipes} (machine types plus crafting, see {@code ModBusEvents}).
     */
    public static <I extends RecipeInput, T extends Recipe<I>> Optional<RecipeHolder<T>> find(@Nullable Level level, RecipeType<T> type, I input) {
        if (level == null) return Optional.empty();
        if (level instanceof ServerLevel serverLevel) {
            return serverLevel.recipeAccess().getRecipeFor(type, input, serverLevel);
        }
        return ClientRecipes.map().getRecipesFor(type, input, level).findFirst();
    }

    /** All recipes of a type, on either side (see {@link #find}). */
    public static <I extends RecipeInput, T extends Recipe<I>> Collection<RecipeHolder<T>> all(@Nullable Level level, RecipeType<T> type) {
        if (level == null) return List.of();
        if (level instanceof ServerLevel serverLevel) {
            return serverLevel.recipeAccess().recipeMap().byType(type);
        }
        return ClientRecipes.map().byType(type);
    }

    /** By id, on either side (see {@link #find}). */
    public static Optional<RecipeHolder<?>> byKey(@Nullable Level level, ResourceKey<Recipe<?>> id) {
        if (level == null) return Optional.empty();
        if (level instanceof ServerLevel serverLevel) {
            return serverLevel.recipeAccess().byKey(id);
        }
        return Optional.ofNullable(ClientRecipes.map().byKey(id));
    }

    public static void register(IEventBus eventBus) {
        SERIALIZERS.register(eventBus);
        TYPES.register(eventBus);
        BOOK_CATEGORIES.register(eventBus);
    }
}
