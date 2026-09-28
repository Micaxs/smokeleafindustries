package net.micaxs.smokeleaf.recipe;

import net.micaxs.smokeleaf.SmokeleafIndustries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
public class ModRecipes {

    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, SmokeleafIndustries.MODID);
    public static final DeferredRegister<RecipeType<?>> TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, SmokeleafIndustries.MODID);


    // Generator (Item -> Energy)
    public static final RegistryObject<RecipeSerializer<GeneratorRecipe>> GENERATOR_SERIALIZER = SERIALIZERS.register("generator", GeneratorRecipe.Serializer::new);
    public static final RegistryObject<RecipeType<GeneratorRecipe>> GENERATOR_TYPE = TYPES.register("generator", () -> new RecipeType<GeneratorRecipe>() {
        @Override
        public String toString() {
            return "generator";
        }
    });

    // Grinder (Bud -> Weed)
    public static final RegistryObject<RecipeSerializer<GrinderRecipe>> GRINDER_SERIALIZER = SERIALIZERS.register("grinder", GrinderRecipe.Serializer::new);
    public static final RegistryObject<RecipeType<GrinderRecipe>> GRINDER_TYPE = TYPES.register("grinder", () -> new RecipeType<GrinderRecipe>() {
        @Override
        public String toString() {
            return "grinder";
        }
    });

    // Extractor (Weed -> Extracts)
    public static final RegistryObject<RecipeSerializer<ExtractorRecipe>> EXTRACTOR_SERIALIZER = SERIALIZERS.register("extractor", ExtractorRecipe.Serializer::new);
    public static final RegistryObject<RecipeType<ExtractorRecipe>> EXTRACTOR_TYPE = TYPES.register("extractor", () -> new RecipeType<ExtractorRecipe>() {
        @Override
        public String toString() {
            return "extractor";
        }
    });

    // Liquifier (Item -> Fluid)
    public static final RegistryObject<RecipeSerializer<LiquifierRecipe>> LIQUIFIER_SERIALIZER = SERIALIZERS.register("liquifier", LiquifierRecipe.Serializer::new);
    public static final RegistryObject<RecipeType<LiquifierRecipe>> LIQUIFIER_TYPE = TYPES.register("liquifier", () -> new RecipeType<LiquifierRecipe>() {
        @Override
        public String toString() {
            return "liquifier";
        }
    });


    // Mutator (Seed + Extract + Fluid -> New Seed)
    public static final RegistryObject<RecipeSerializer<MutatorRecipe>> MUTATOR_SERIALIZER = SERIALIZERS.register("mutator", MutatorRecipe.Serializer::new);
    public static final RegistryObject<RecipeType<MutatorRecipe>> MUTATOR_TYPE = TYPES.register("mutator", () -> new RecipeType<MutatorRecipe>() {
        @Override
        public String toString() {
            return "mutator";
        }
    });

    // Synthesizer (Empty DNA Strand + 3 Items -> Filled DNA Strand)
    public static final RegistryObject<RecipeSerializer<SynthesizerRecipe>> SYNTHESIZER_SERIALIZER = SERIALIZERS.register("synthesizer", SynthesizerRecipe.Serializer::new);
    public static final RegistryObject<RecipeType<SynthesizerRecipe>> SYNTHESIZER_TYPE = TYPES.register("synthesizer", () -> new RecipeType<SynthesizerRecipe>() {
        @Override
        public String toString() {
            return "synthesizer";
        }
    });

    // Sequencer (Filled DNA Strand + Base Extract -> New Item)
    public static final RegistryObject<RecipeSerializer<SequencerRecipe>> SEQUENCER_SERIALIZER = SERIALIZERS.register("sequencer", SequencerRecipe.Serializer::new);
    public static final RegistryObject<RecipeType<SequencerRecipe>> SEQUENCER_TYPE = TYPES.register("sequencer", () -> new RecipeType<SequencerRecipe>() {
        @Override
        public String toString() {
            return "sequencer";
        }
    });

    // Drying (Bud -> Dry Bud / Tobacco Leaf -> Dried Tabacco Leaf / etc..)
    public static final RegistryObject<RecipeSerializer<DryingRecipe>> DRYING_SERIALIZER = SERIALIZERS.register("drying", DryingRecipe.Serializer::new);
    public static final RegistryObject<RecipeType<DryingRecipe>> DRYING_TYPE = TYPES.register("drying", () -> new RecipeType<DryingRecipe>() {
        @Override
        public String toString() {
            return "drying";
        }
    });

    public static final RegistryObject<RecipeSerializer<LoadManualGrinderRecipe>> LOAD_MANUAL_GRINDER_SERIALIZER = SERIALIZERS.register("load_manual_grinder", () -> LoadManualGrinderRecipe.Serializer.INSTANCE);
    public static final RegistryObject<RecipeSerializer<ManualGrinderRecipe>> MANUAL_GRINDER_SERIALIZER = SERIALIZERS.register("manual_grinder", ManualGrinderRecipe.Serializer::new);
    public static final RegistryObject<RecipeSerializer<StrainCopyShapelessRecipe>> STRAIN_COPY_SHAPELESS_SERIALIZER = SERIALIZERS.register("strain_copy_shapeless", StrainCopyShapelessRecipe.Serializer::new);
    public static final RegistryObject<RecipeSerializer<StrainCopyShapedRecipe>> STRAIN_COPY_SHAPED_SERIALIZER = SERIALIZERS.register("strain_copy_shaped", StrainCopyShapedRecipe.Serializer::new);
    public static final RegistryObject<RecipeSerializer<ArmorUpgradeShapelessRecipe>> ARMOR_UPGRADE_SHAPELESS_SERIALIZER = SERIALIZERS.register("armor_upgrade_shapeless", ArmorUpgradeShapelessRecipe.Serializer::new);
    public static final RegistryObject<RecipeType<ManualGrinderRecipe>> MANUAL_GRINDER_TYPE = TYPES.register("manual_grinder", () -> new RecipeType<ManualGrinderRecipe>() {
        @Override
        public String toString() {
        return "manual_grinder";
        }
    });


    public static final RegistryObject<RecipeSerializer<JointRecipe>> JOINT_SERIALIZER = SERIALIZERS.register("joint", JointRecipe.Serializer::new);
    public static final RegistryObject<RecipeType<JointRecipe>> JOINT_TYPE = TYPES.register("joint", () -> new RecipeType<JointRecipe>() {
        @Override public String toString() {
            return "joint";
        }
    });

    public static final RegistryObject<RecipeSerializer<BluntRecipe>> BLUNT_SERIALIZER = SERIALIZERS.register("blunt", BluntRecipe.Serializer::new);
    public static final RegistryObject<RecipeType<BluntRecipe>> BLUNT_TYPE = TYPES.register("blunt", () -> new RecipeType<BluntRecipe>() {
        @Override public String toString() {
            return "blunt";
        }
    });

    // Gummy Machine (Mold + Oil -> Gummy)
    public static final RegistryObject<RecipeSerializer<GummyRecipe>> GUMMY_SERIALIZER = SERIALIZERS.register("gummy_machine", GummyRecipe.Serializer::new);
    public static final RegistryObject<RecipeType<GummyRecipe>> GUMMY_TYPE = TYPES.register("gummy_machine", () -> new RecipeType<GummyRecipe>() {
        @Override
        public String toString() {
            return "gummy_machine";
        }
    });

    public static void register(IEventBus eventBus) {
        SERIALIZERS.register(eventBus);
        TYPES.register(eventBus);
    }
}
