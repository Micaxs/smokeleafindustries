package net.micaxs.smokeleaf.block.entity;

import net.micaxs.smokeleaf.Config;
import net.micaxs.smokeleaf.component.ModDataComponentTypes;
import net.micaxs.smokeleaf.strain.StrainData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * BlockEntity for {@link net.micaxs.smokeleaf.block.custom.UnidentifiedWeedCropBlock}.
 * Stores StrainData for the planted custom strain and dynamic nutrient levels.
 */
public class UnidentifiedWeedCropBlockEntity extends BaseWeedCropBlockEntity {

    private static final int MAX_PERCENT = 100;
    private static final int NPK_TOLERANCE = 3;

    private StrainData strain = StrainData.EMPTY;

    public UnidentifiedWeedCropBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.UNIDENTIFIED_WEED_CROP_BE.get(), pos, state);
    }

    public StrainData getStrain() {
        return strain != null ? strain : StrainData.EMPTY;
    }

    public void setStrain(StrainData d) {
        this.strain = d != null ? d : StrainData.EMPTY;
        this.setChanged();
    }

    /**
     * For unidentified crop, target nutrient levels come from strain N/P/K.
     */
    public boolean isValidAgainstTarget(int targetN, int targetP, int targetK) {
        int n = getNitrogen();
        int p = getPhosphorus();
        int k = getPotassium();
        boolean nOk = Math.abs(n - targetN) <= NPK_TOLERANCE;
        boolean pOk = Math.abs(p - targetP) <= NPK_TOLERANCE;
        boolean kOk = Math.abs(k - targetK) <= NPK_TOLERANCE;
        return nOk && pOk && kOk;
    }

    @Override
    public int getBudCount() {
        // Same general rule as BaseWeedCropBlockEntity but with target from strain.
        if (strain == null || strain == StrainData.EMPTY) {
            return super.getBudCount();
        }

        int dn = Math.abs(getNitrogen() - strain.nitrogen());
        int dp = Math.abs(getPhosphorus() - strain.phosphorus());
        int dk = Math.abs(getPotassium() - strain.potassium());

        if (dn == 0 && dp == 0 && dk == 0) {
            return 3;
        }

        int offCount = 0;
        if (dn > NPK_TOLERANCE) offCount++;
        if (dp > NPK_TOLERANCE) offCount++;
        if (dk > NPK_TOLERANCE) offCount++;
        if (offCount == 0) {
            return 2;
        }
        return 1;
    }

    @Override
    public int getThc() {
        if (strain == null || strain == StrainData.EMPTY) return super.getThc();
        return computeWithNutrients(strain.thc());
    }

    @Override
    public int getCbd() {
        if (strain == null || strain == StrainData.EMPTY) return super.getCbd();
        return computeWithNutrients(strain.cbd());
    }

    private int computeWithNutrients(int base) {
        if (strain == null || strain == StrainData.EMPTY) return base;

        int dn = Math.abs(getNitrogen() - strain.nitrogen());
        int dp = Math.abs(getPhosphorus() - strain.phosphorus());
        int dk = Math.abs(getPotassium() - strain.potassium());

        if (dn == 0 && dp == 0 && dk == 0) {
            return Mth.clamp(base * 2, 0, MAX_PERCENT);
        }

        int totalDiff = dn + dp + dk;
        int reduction = (int) Math.round(base * 0.10 * totalDiff);
        return Mth.clamp(base - reduction, 0, MAX_PERCENT);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (strain != null && strain != StrainData.EMPTY) {
            tag.put("strain_data", StrainData.CODEC.encodeStart(net.minecraft.nbt.NbtOps.INSTANCE, strain).result().orElse(new CompoundTag()));
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        strain = StrainData.EMPTY;
        if (tag.contains("strain_data")) {
            StrainData.CODEC.parse(net.minecraft.nbt.NbtOps.INSTANCE, tag.get("strain_data"))
                    .result()
                    .ifPresent(d -> strain = d);
        }
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider lookupProvider) {
        super.onDataPacket(net, pkt, lookupProvider);
    }

    @Override
    public void sync() {
        if (this.level instanceof ServerLevel server) {
            this.setChanged();
            server.sendBlockUpdated(this.worldPosition, getBlockState(), getBlockState(), 3);
        }
    }



    @Override
    public Config.NutrientTarget getOptimalNutrientsLevels() {
        StrainData d = getStrain();
        if (d == null || d == StrainData.EMPTY) {
            return super.getOptimalNutrientsLevels();
        }
        return new Config.NutrientTarget(d.nitrogen(), d.phosphorus(), d.potassium());
    }
}
