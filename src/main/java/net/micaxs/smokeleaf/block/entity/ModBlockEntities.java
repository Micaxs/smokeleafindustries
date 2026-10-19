package net.micaxs.smokeleaf.block.entity;

import net.micaxs.smokeleaf.SmokeleafIndustries;
import net.micaxs.smokeleaf.block.ModBlocks;
import net.micaxs.smokeleaf.block.custom.SequencerBlock;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, SmokeleafIndustries.MODID);


    public static final Supplier<BlockEntityType<BaseWeedCropBlockEntity>> BASE_WEED_CROP_BE =
            BLOCK_ENTITIES.register("base_weed_crop_be",
                    () -> new BlockEntityType<>(BaseWeedCropBlockEntity::new,
                            ModBlocks.HEMP_CROP.get()
                    ));


    public static final Supplier<BlockEntityType<UnidentifiedWeedCropBlockEntity>> UNIDENTIFIED_WEED_CROP_BE =
            BLOCK_ENTITIES.register("unidentified_weed_crop_be",
                    () -> new BlockEntityType<>(UnidentifiedWeedCropBlockEntity::new,
                            ModBlocks.UNIDENTIFIED_WEED_CROP.get()
                    ));

    public static final Supplier<BlockEntityType<GeneratorBlockEntity>> GENERATOR_BE = BLOCK_ENTITIES.register("generator_be",
            () -> new BlockEntityType<>(GeneratorBlockEntity::new, ModBlocks.GENERATOR.get()));

    public static final Supplier<BlockEntityType<GrinderBlockEntity>> GRINDER_BE = BLOCK_ENTITIES.register("grinder_be",
            () -> new BlockEntityType<>(GrinderBlockEntity::new, ModBlocks.GRINDER.get()));

    public static final Supplier<BlockEntityType<ExtractorBlockEntity>> EXTRACTOR_BE = BLOCK_ENTITIES.register("extractor_be",
            () -> new BlockEntityType<>(ExtractorBlockEntity::new, ModBlocks.EXTRACTOR.get()));

    public static final Supplier<BlockEntityType<LiquifierBlockEntity>> LIQUIFIER_BE = BLOCK_ENTITIES.register("liquifier_be",
            () -> new BlockEntityType<>(LiquifierBlockEntity::new, ModBlocks.LIQUIFIER.get()));

    public static final Supplier<BlockEntityType<MutatorBlockEntity>> MUTATOR_BE = BLOCK_ENTITIES.register("mutator_be",
            () -> new BlockEntityType<>(MutatorBlockEntity::new, ModBlocks.MUTATOR.get()));

    public static final Supplier<BlockEntityType<SynthesizerBlockEntity>> SYNTHESIZER_BE = BLOCK_ENTITIES.register("synthesizer_be",
            () -> new BlockEntityType<>(SynthesizerBlockEntity::new, ModBlocks.SYNTHESIZER.get()));

    public static final Supplier<BlockEntityType<SequencerBlockEntity>> SEQUENCER_BE = BLOCK_ENTITIES.register("sequencer_be",
            () -> new BlockEntityType<>(SequencerBlockEntity::new, ModBlocks.SEQUENCER.get()));

    public static final Supplier<BlockEntityType<DryerBlockEntity>> DRYER_BE = BLOCK_ENTITIES.register("dryer_be",
            () -> new BlockEntityType<>(DryerBlockEntity::new, ModBlocks.DRYER.get()));

    public static final Supplier<BlockEntityType<DryingRackBlockEntity>> DRYING_RACK_BE = BLOCK_ENTITIES.register("drying_rack_be",
            () -> new BlockEntityType<>(DryingRackBlockEntity::new, ModBlocks.DRYING_RACK.get()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ReflectorBlockEntity>> REFLECTOR = BLOCK_ENTITIES.register("reflector_be",
            () -> new BlockEntityType<>(ReflectorBlockEntity::new, ModBlocks.REFLECTOR.get()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<GrowLightBlockEntity>> GROW_LIGHT = BLOCK_ENTITIES.register("grow_light_be",
            () -> new BlockEntityType<>(GrowLightBlockEntity::new, ModBlocks.LED_LIGHT.get()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<GrowPotBlockEntity>> GROW_POT = BLOCK_ENTITIES.register("grow_pot_be",
            () -> new BlockEntityType<>(GrowPotBlockEntity::new, ModBlocks.GROW_POT.get()));

    public static final Supplier<BlockEntityType<MixerBlockEntity>> MIXER_BE = BLOCK_ENTITIES.register("mixer_be",
            () -> new BlockEntityType<>(MixerBlockEntity::new, ModBlocks.MIXER.get()));

    public static final Supplier<BlockEntityType<StrainModifierBlockEntity>> STRAIN_MODIFIER_BE = BLOCK_ENTITIES.register("strain_modifier_be",
            () -> new BlockEntityType<>(StrainModifierBlockEntity::new, ModBlocks.STRAIN_MODIFIER.get()));

    public static final Supplier<BlockEntityType<GummyMachineBlockEntity>> GUMMY_MACHINE_BE = BLOCK_ENTITIES.register("gummy_machine_be",
            () -> new BlockEntityType<>(GummyMachineBlockEntity::new, ModBlocks.GUMMY_MACHINE.get()));

    public static final Supplier<BlockEntityType<PipeBlockEntity>> PIPE_BE = BLOCK_ENTITIES.register("pipe_be",
            () -> new BlockEntityType<>(PipeBlockEntity::new, ModBlocks.PIPE.get()));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }

}
