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
                    () -> BlockEntityType.Builder.of(BaseWeedCropBlockEntity::new,
                            ModBlocks.HEMP_CROP.get()
                    ).build(null));


    public static final Supplier<BlockEntityType<UnidentifiedWeedCropBlockEntity>> UNIDENTIFIED_WEED_CROP_BE =
            BLOCK_ENTITIES.register("unidentified_weed_crop_be",
                    () -> BlockEntityType.Builder.of(UnidentifiedWeedCropBlockEntity::new,
                            ModBlocks.UNIDENTIFIED_WEED_CROP.get()
                    ).build(null));

    public static final Supplier<BlockEntityType<GeneratorBlockEntity>> GENERATOR_BE = BLOCK_ENTITIES.register("generator_be",
            () -> BlockEntityType.Builder.of(GeneratorBlockEntity::new, ModBlocks.GENERATOR.get()).build(null));

    public static final Supplier<BlockEntityType<GrinderBlockEntity>> GRINDER_BE = BLOCK_ENTITIES.register("grinder_be",
            () -> BlockEntityType.Builder.of(GrinderBlockEntity::new, ModBlocks.GRINDER.get()).build(null));

    public static final Supplier<BlockEntityType<ExtractorBlockEntity>> EXTRACTOR_BE = BLOCK_ENTITIES.register("extractor_be",
            () -> BlockEntityType.Builder.of(ExtractorBlockEntity::new, ModBlocks.EXTRACTOR.get()).build(null));

    public static final Supplier<BlockEntityType<LiquifierBlockEntity>> LIQUIFIER_BE = BLOCK_ENTITIES.register("liquifier_be",
            () -> BlockEntityType.Builder.of(LiquifierBlockEntity::new, ModBlocks.LIQUIFIER.get()).build(null));

    public static final Supplier<BlockEntityType<MutatorBlockEntity>> MUTATOR_BE = BLOCK_ENTITIES.register("mutator_be",
            () -> BlockEntityType.Builder.of(MutatorBlockEntity::new, ModBlocks.MUTATOR.get()).build(null));

    public static final Supplier<BlockEntityType<SynthesizerBlockEntity>> SYNTHESIZER_BE = BLOCK_ENTITIES.register("synthesizer_be",
            () -> BlockEntityType.Builder.of(SynthesizerBlockEntity::new, ModBlocks.SYNTHESIZER.get()).build(null));

    public static final Supplier<BlockEntityType<SequencerBlockEntity>> SEQUENCER_BE = BLOCK_ENTITIES.register("sequencer_be",
            () -> BlockEntityType.Builder.of(SequencerBlockEntity::new, ModBlocks.SEQUENCER.get()).build(null));

    public static final Supplier<BlockEntityType<DryerBlockEntity>> DRYER_BE = BLOCK_ENTITIES.register("dryer_be",
            () -> BlockEntityType.Builder.of(DryerBlockEntity::new, ModBlocks.DRYER.get()).build(null));

    public static final Supplier<BlockEntityType<DryingRackBlockEntity>> DRYING_RACK_BE = BLOCK_ENTITIES.register("drying_rack_be",
            () -> BlockEntityType.Builder.of(DryingRackBlockEntity::new, ModBlocks.DRYING_RACK.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ReflectorBlockEntity>> REFLECTOR = BLOCK_ENTITIES.register("reflector_be",
            () -> BlockEntityType.Builder.of(ReflectorBlockEntity::new, ModBlocks.REFLECTOR.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<GrowLightBlockEntity>> GROW_LIGHT = BLOCK_ENTITIES.register("grow_light_be",
            () -> BlockEntityType.Builder.of(GrowLightBlockEntity::new, ModBlocks.LED_LIGHT.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<GrowPotBlockEntity>> GROW_POT = BLOCK_ENTITIES.register("grow_pot_be",
            () -> BlockEntityType.Builder.of(GrowPotBlockEntity::new, ModBlocks.GROW_POT.get()).build(null));

    public static final Supplier<BlockEntityType<MixerBlockEntity>> MIXER_BE = BLOCK_ENTITIES.register("mixer_be",
            () -> BlockEntityType.Builder.of(MixerBlockEntity::new, ModBlocks.MIXER.get()).build(null));


    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }

}
