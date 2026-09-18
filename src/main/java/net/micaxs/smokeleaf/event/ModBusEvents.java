package net.micaxs.smokeleaf.event;

import net.micaxs.smokeleaf.SmokeleafIndustries;
import net.micaxs.smokeleaf.block.entity.*;
import net.micaxs.smokeleaf.fluid.ModFluids;
import net.micaxs.smokeleaf.utils.ExtractRestrictedItemHandler;
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

        // Drying Rack is deliberately NOT given an ItemHandler capability — it's the free, manual,
        // unpowered counterpart to the Dryer, meant to be loaded/unloaded by hand, not automated.
        // Registered explicitly as a no-op provider (rather than just omitting it) so that stays
        // true even if a future refactor makes it implement Container or some other interface a
        // capability might otherwise auto-attach to.
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.DRYING_RACK_BE.get(), (be, dir) -> null);

        // Mixer BlockEntity Capabilities
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModBlockEntities.MIXER_BE.get(), MixerBlockEntity::getEnergyStorage);
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, ModBlockEntities.MIXER_BE.get(), MixerBlockEntity::getFluidHandler);
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.MIXER_BE.get(), MixerBlockEntity::getItemHandler);

        // Strain Modifier (Strain Identifier) BlockEntity Capabilities
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModBlockEntities.STRAIN_MODIFIER_BE.get(), StrainModifierBlockEntity::getEnergyStorage);
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.STRAIN_MODIFIER_BE.get(), StrainModifierBlockEntity::getItemHandler);

        // Gummy Machine (Confectioner) BlockEntity Capabilities
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModBlockEntities.GUMMY_MACHINE_BE.get(), GummyMachineBlockEntity::getEnergyStorage);
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, ModBlockEntities.GUMMY_MACHINE_BE.get(), GummyMachineBlockEntity::getTank);
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.GUMMY_MACHINE_BE.get(), GummyMachineBlockEntity::getItemHandler);

        // Pipe BlockEntity Capabilities — only exposed on faces wrenched to IMPORT/EXPORT (see PipeBlockEntity getters)
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.PIPE_BE.get(), PipeBlockEntity::getItemHandler);
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, ModBlockEntities.PIPE_BE.get(), PipeBlockEntity::getFluidHandler);
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModBlockEntities.PIPE_BE.get(), PipeBlockEntity::getEnergyStorage);

        // Item fluid handler capability for custom oil bucket (needed for FluidUtil compatibility)
        event.registerItem(Capabilities.FluidHandler.ITEM,
                (stack, ctx) -> new FluidBucketWrapper(stack),
                ModFluids.UNIDENTIFIED_MIXTURE_BUCKET.get());

    }




}
