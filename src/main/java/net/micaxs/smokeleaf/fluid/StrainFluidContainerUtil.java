package net.micaxs.smokeleaf.fluid;

import net.micaxs.smokeleaf.component.ModDataComponentTypes;
import net.micaxs.smokeleaf.strain.StrainData;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

/**
 * NeoForge's generic {@code FluidBucketWrapper} (used for any plain {@link BucketItem}, including
 * this mod's strain-tagged oil buckets) reconstructs the drained {@link FluidStack} as
 * {@code new FluidStack(bucketItem.getFluid(), 1000)} with no data components at all — so pouring a
 * named strain-oil bucket into a tank via {@link FluidUtil#tryEmptyContainer} silently strips
 * STRAIN_DATA/STRAIN_ID/STRAIN_CREATOR/MIX_KEY, leaving a colorless "Unidentified Mixture Oil".
 * This helper reads those components directly off the bucket ItemStack instead, so filling a tank
 * from a bucket preserves strain identity exactly like draining a tank into a bucket already does
 * (via {@code FluidType.getBucket(FluidStack)}).
 */
public final class StrainFluidContainerUtil {
    private StrainFluidContainerUtil() {}

    public static FluidActionResult tryEmptyContainerIntoTank(ItemStack container, IFluidHandler tank, int amount,
                                                                @Nullable Player player, boolean doTransfer) {
        if (container.isEmpty()) return FluidActionResult.FAILURE;

        if (!(container.getItem() instanceof BucketItem bucketItem)
                || bucketItem.getFluid() == Fluids.EMPTY
                || container.getCount() != 1) {
            // Not a plain single bucket — defer to the vanilla/NeoForge generic handling
            // (covers tinctures and any other non-bucket fluid container).
            return FluidUtil.tryEmptyContainer(container, tank, amount, player, doTransfer);
        }

        FluidStack toFill = new FluidStack(bucketItem.getFluid(), 1000);
        copyStrainComponents(container, toFill);

        int simulated = tank.fill(toFill, IFluidHandler.FluidAction.SIMULATE);
        if (simulated < 1000) return FluidActionResult.FAILURE;

        if (!doTransfer) return new FluidActionResult(container);

        tank.fill(toFill, IFluidHandler.FluidAction.EXECUTE);
        return new FluidActionResult(new ItemStack(Items.BUCKET));
    }

    public static void copyStrainComponents(ItemStack from, FluidStack to) {
        StrainData strain = ModDataComponentTypes.STRAIN_DATA.get(from);
        if (strain != null) ModDataComponentTypes.STRAIN_DATA.set(to, strain);
        String strainId = ModDataComponentTypes.STRAIN_ID.get(from);
        if (strainId != null) ModDataComponentTypes.STRAIN_ID.set(to, strainId);
        String creator = ModDataComponentTypes.STRAIN_CREATOR.get(from);
        if (creator != null) ModDataComponentTypes.STRAIN_CREATOR.set(to, creator);
        String mixKey = ModDataComponentTypes.MIX_KEY.get(from);
        if (mixKey != null) ModDataComponentTypes.MIX_KEY.set(to, mixKey);
    }
}
