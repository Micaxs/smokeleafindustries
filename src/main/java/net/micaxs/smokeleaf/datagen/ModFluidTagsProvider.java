package net.micaxs.smokeleaf.datagen;

import net.micaxs.smokeleaf.SmokeleafIndustries;
import net.micaxs.smokeleaf.fluid.ModFluids;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.FluidTagsProvider;
import net.minecraft.tags.FluidTags;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModFluidTagsProvider extends FluidTagsProvider {
    public ModFluidTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, provider, SmokeleafIndustries.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
       tag(FluidTags.WATER)
               .add(ModFluids.SOURCE_HASH_OIL_FLUID.get())
               .add(ModFluids.FLOWING_HASH_OIL_FLUID.get())
               .add(ModFluids.SOURCE_HASH_OIL_SLUDGE_FLUID.get())
               .add(ModFluids.FLOWING_HASH_OIL_SLUDGE_FLUID.get());
    }
}
