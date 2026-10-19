package net.micaxs.smokeleaf.event;

import net.micaxs.smokeleaf.SmokeleafIndustries;
import net.micaxs.smokeleaf.block.entity.*;
import net.micaxs.smokeleaf.fluid.ModFluids;
import net.micaxs.smokeleaf.utils.ExtractRestrictedItemHandler;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.micaxs.smokeleaf.recipe.ModRecipes;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.transfer.fluid.BucketResourceHandler;

@EventBusSubscriber(modid = SmokeleafIndustries.MODID)
public class ModBusEvents {
    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        // Generator BlockEntity Capabilities
        event.registerBlockEntity(Capabilities.Energy.BLOCK, ModBlockEntities.GENERATOR_BE.get(), GeneratorBlockEntity::getEnergyStorage);
        event.registerBlockEntity(Capabilities.Item.BLOCK, ModBlockEntities.GENERATOR_BE.get(), GeneratorBlockEntity::getItemHandler);

        // Grinder BlockEntity Capabilities
        event.registerBlockEntity(Capabilities.Energy.BLOCK, ModBlockEntities.GRINDER_BE.get(), GrinderBlockEntity::getEnergyStorage);
        event.registerBlockEntity(Capabilities.Item.BLOCK, ModBlockEntities.GRINDER_BE.get(), GrinderBlockEntity::getItemHandler);

        // Extractor BlockEntity Capabilities
        event.registerBlockEntity(Capabilities.Energy.BLOCK, ModBlockEntities.EXTRACTOR_BE.get(), ExtractorBlockEntity::getEnergyStorage);
        event.registerBlockEntity(Capabilities.Item.BLOCK, ModBlockEntities.EXTRACTOR_BE.get(), ExtractorBlockEntity::getItemHandler);

        // Liquifier BlockEntity Capabilities
        event.registerBlockEntity(Capabilities.Energy.BLOCK, ModBlockEntities.LIQUIFIER_BE.get(), LiquifierBlockEntity::getEnergyStorage);
        event.registerBlockEntity(Capabilities.Fluid.BLOCK, ModBlockEntities.LIQUIFIER_BE.get(), LiquifierBlockEntity::getTank);
        event.registerBlockEntity(Capabilities.Item.BLOCK, ModBlockEntities.LIQUIFIER_BE.get(), LiquifierBlockEntity::getItemHandler);

        // Mutator BlockEntity Capabilities
        event.registerBlockEntity(Capabilities.Energy.BLOCK, ModBlockEntities.MUTATOR_BE.get(), MutatorBlockEntity::getEnergyStorage);
        event.registerBlockEntity(Capabilities.Fluid.BLOCK, ModBlockEntities.MUTATOR_BE.get(), MutatorBlockEntity::getTank);
        event.registerBlockEntity(Capabilities.Item.BLOCK, ModBlockEntities.MUTATOR_BE.get(), MutatorBlockEntity::getItemHandler);

        // Synthesizer BlockEntity Capabilities
        event.registerBlockEntity(Capabilities.Energy.BLOCK, ModBlockEntities.SYNTHESIZER_BE.get(), SynthesizerBlockEntity::getEnergyStorage);
        event.registerBlockEntity(Capabilities.Item.BLOCK, ModBlockEntities.SYNTHESIZER_BE.get(), SynthesizerBlockEntity::getItemHandler);

        // Sequencer BlockEntity Capabilities
        event.registerBlockEntity(Capabilities.Energy.BLOCK, ModBlockEntities.SEQUENCER_BE.get(), SequencerBlockEntity::getEnergyStorage);
        event.registerBlockEntity(Capabilities.Item.BLOCK, ModBlockEntities.SEQUENCER_BE.get(), SequencerBlockEntity::getItemHandler);

        // Dryer BlockEntity Capabilities
        event.registerBlockEntity(Capabilities.Energy.BLOCK, ModBlockEntities.DRYER_BE.get(), DryerBlockEntity::getEnergyStorage);
        event.registerBlockEntity(Capabilities.Item.BLOCK, ModBlockEntities.DRYER_BE.get(), DryerBlockEntity::getItemHandler);

        // Drying Rack is deliberately NOT given an ItemHandler capability — it's the free, manual,
        // unpowered counterpart to the Dryer, meant to be loaded/unloaded by hand, not automated.
        // Registered explicitly as a no-op provider (rather than just omitting it) so that stays
        // true even if a future refactor makes it implement Container or some other interface a
        // capability might otherwise auto-attach to.
        event.registerBlockEntity(Capabilities.Item.BLOCK, ModBlockEntities.DRYING_RACK_BE.get(), (be, dir) -> null);

        // Mixer BlockEntity Capabilities
        event.registerBlockEntity(Capabilities.Energy.BLOCK, ModBlockEntities.MIXER_BE.get(), MixerBlockEntity::getEnergyStorage);
        event.registerBlockEntity(Capabilities.Fluid.BLOCK, ModBlockEntities.MIXER_BE.get(), MixerBlockEntity::getFluidHandler);
        event.registerBlockEntity(Capabilities.Item.BLOCK, ModBlockEntities.MIXER_BE.get(), MixerBlockEntity::getItemHandler);

        // Strain Modifier (Strain Identifier) BlockEntity Capabilities
        event.registerBlockEntity(Capabilities.Energy.BLOCK, ModBlockEntities.STRAIN_MODIFIER_BE.get(), StrainModifierBlockEntity::getEnergyStorage);
        event.registerBlockEntity(Capabilities.Item.BLOCK, ModBlockEntities.STRAIN_MODIFIER_BE.get(), StrainModifierBlockEntity::getItemHandler);

        // Gummy Machine (Confectioner) BlockEntity Capabilities
        event.registerBlockEntity(Capabilities.Energy.BLOCK, ModBlockEntities.GUMMY_MACHINE_BE.get(), GummyMachineBlockEntity::getEnergyStorage);
        event.registerBlockEntity(Capabilities.Fluid.BLOCK, ModBlockEntities.GUMMY_MACHINE_BE.get(), GummyMachineBlockEntity::getTank);
        event.registerBlockEntity(Capabilities.Item.BLOCK, ModBlockEntities.GUMMY_MACHINE_BE.get(), GummyMachineBlockEntity::getItemHandler);

        // Pipe BlockEntity Capabilities — only exposed on faces wrenched to IMPORT/EXPORT (see PipeBlockEntity getters)
        event.registerBlockEntity(Capabilities.Item.BLOCK, ModBlockEntities.PIPE_BE.get(), PipeBlockEntity::getItemHandler);
        event.registerBlockEntity(Capabilities.Fluid.BLOCK, ModBlockEntities.PIPE_BE.get(), PipeBlockEntity::getFluidHandler);
        event.registerBlockEntity(Capabilities.Energy.BLOCK, ModBlockEntities.PIPE_BE.get(), PipeBlockEntity::getEnergyStorage);

        // Item fluid handler capability for custom oil bucket (needed for FluidUtil compatibility)
        event.registerItem(Capabilities.Fluid.ITEM,
                (stack, access) -> new BucketResourceHandler(access),
                ModFluids.UNIDENTIFIED_MIXTURE_BUCKET.get());

    }





    /** Recipes are no longer synced to clients by default; JEI and client-side lookups need ours. */
    @SubscribeEvent
    public static void onDatapackSync(OnDatapackSyncEvent event) {
        event.sendRecipes(ModRecipes.machineTypes());
        // The guide book shows crafting/smelting recipes from the client side.
        event.sendRecipes(RecipeType.CRAFTING, RecipeType.SMELTING);
    }
}
