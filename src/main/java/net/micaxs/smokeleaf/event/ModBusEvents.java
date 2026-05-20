package net.micaxs.smokeleaf.event;

import net.micaxs.smokeleaf.SmokeleafIndustries;
import net.micaxs.smokeleaf.block.entity.*;
import net.micaxs.smokeleaf.fluid.ModFluids;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.capability.wrappers.FluidBucketWrapper;

@EventBusSubscriber(modid = SmokeleafIndustries.MODID)
public class ModBusEvents {
    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        // Generator BlockEntity Capabilities
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModBlockEntities.GENERATOR_BE.get(), GeneratorBlockEntity::getEnergyStorage);
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.GENERATOR_BE.get(), GeneratorBlockEntity::getItemHandler);

        // Grinder BlockEntity Capabilities
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModBlockEntities.GRINDER_BE.get(), GrinderBlockEntity::getEnergyStorage);
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.GRINDER_BE.get(), GrinderBlockEntity::getItemHandler);

        // Extractor BlockEntity Capabilities
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModBlockEntities.EXTRACTOR_BE.get(), ExtractorBlockEntity::getEnergyStorage);
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.EXTRACTOR_BE.get(), ExtractorBlockEntity::getItemHandler);

        // Liquifier BlockEntity Capabilities
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModBlockEntities.LIQUIFIER_BE.get(), LiquifierBlockEntity::getEnergyStorage);
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, ModBlockEntities.LIQUIFIER_BE.get(), LiquifierBlockEntity::getTank);
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.LIQUIFIER_BE.get(), LiquifierBlockEntity::getItemHandler);

        // Mutator BlockEntity Capabilities
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModBlockEntities.MUTATOR_BE.get(), MutatorBlockEntity::getEnergyStorage);
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, ModBlockEntities.MUTATOR_BE.get(), MutatorBlockEntity::getTank);
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.MUTATOR_BE.get(), MutatorBlockEntity::getItemHandler);

        // Synthesizer BlockEntity Capabilities
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModBlockEntities.SYNTHESIZER_BE.get(), SynthesizerBlockEntity::getEnergyStorage);
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.SYNTHESIZER_BE.get(), SynthesizerBlockEntity::getItemHandler);

        // Sequencer BlockEntity Capabilities
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModBlockEntities.SEQUENCER_BE.get(), SequencerBlockEntity::getEnergyStorage);
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.SEQUENCER_BE.get(), SequencerBlockEntity::getItemHandler);

        // Dryer BlockEntity Capabilities
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModBlockEntities.DRYER_BE.get(), DryerBlockEntity::getEnergyStorage);
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.DRYER_BE.get(), DryerBlockEntity::getItemHandler);

        // Mixer BlockEntity Capabilities
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModBlockEntities.MIXER_BE.get(), MixerBlockEntity::getEnergyStorage);
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, ModBlockEntities.MIXER_BE.get(), MixerBlockEntity::getFluidHandler);
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.MIXER_BE.get(), MixerBlockEntity::getItemHandler);

        // Item fluid handler capability for custom oil bucket (needed for FluidUtil compatibility)
        event.registerItem(Capabilities.FluidHandler.ITEM,
                (stack, ctx) -> new FluidBucketWrapper(stack),
                ModFluids.UNIDENTIFIED_MIXTURE_BUCKET.get());

    }




}
