package net.micaxs.smokeleaf.block.entity;

import net.micaxs.smokeleaf.Config;
import net.micaxs.smokeleaf.component.ModDataComponentTypes;
import net.micaxs.smokeleaf.strain.StrainData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class BaseWeedCropBlockEntity extends BlockEntity {

    private static final int MAX_PERCENT = 100;
    private static final int MAX_PH = 14;
    private static final int NPK_TOLERANCE = 3;

    private int thc;
    private int cbd;
    private int ph;
    private int nitrogen;
    private int phosphorus;
    private int potassium;

    public BaseWeedCropBlockEntity(BlockPos pos, BlockState blockState) {
        this(ModBlockEntities.BASE_WEED_CROP_BE.get(), pos, blockState);
    }

    /**
     * Internal ctor to let subclasses use a different BlockEntityType.
     */
    protected BaseWeedCropBlockEntity(BlockEntityType<? extends BaseWeedCropBlockEntity> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    // Getters
    public int getPh() { return ph; }
    public int getNitrogen() { return nitrogen; }
    public int getPhosphorus() { return phosphorus; }
    public int getPotassium() { return potassium; }

    // Setters (clamped)
    public void setThc(int v) { this.thc = Mth.clamp(v, 0, MAX_PERCENT); }
    public void setCbd(int v) { this.cbd = Mth.clamp(v, 0, MAX_PERCENT); }
    public void setPh(int v) { this.ph = Mth.clamp(v, 0, MAX_PH); }
    public void setNitrogen(int v) { this.nitrogen = Mth.clamp(v, 0, MAX_PERCENT); }
    public void setPhosphorus(int v) { this.phosphorus = Mth.clamp(v, 0, MAX_PERCENT); }
    public void setPotassium(int v) { this.potassium = Mth.clamp(v, 0, MAX_PERCENT); }

    // Convenience increase/decrease
    public void addThc(int d) { setThc(this.thc + d); }
    public void addCbd(int d) { setCbd(this.cbd + d); }
    public void addPh(int d) { setPh(this.ph + d); }
    public void addNitrogen(int d) { setNitrogen(this.nitrogen + d); }
    public void addPhosphorus(int d) { setPhosphorus(this.phosphorus + d); }
    public void addPotassium(int d) { setPotassium(this.potassium + d); }


    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("thc", this.thc);
        tag.putInt("cbd", this.cbd);
        tag.putInt("ph", this.ph);
        tag.putInt("nitrogen", this.nitrogen);
        tag.putInt("phosphorus", this.phosphorus);
        tag.putInt("potassium", this.potassium);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.thc = tag.getInt("thc");
        this.cbd = tag.getInt("cbd");
        this.ph = tag.getInt("ph");
        this.nitrogen = tag.getInt("nitrogen");
        this.phosphorus = tag.getInt("phosphorus");
        this.potassium = tag.getInt("potassium");
    }


    private ResourceLocation getCropId() {
        if (level == null) return null;
        return BuiltInRegistries.BLOCK.getKey(getBlockState().getBlock());
    }

    public boolean isValidNutrientsLevels() {
        var cropId = getCropId();
        var targetOpt = Config.getNutrientTargetFor(cropId);
        if (targetOpt.isEmpty()) {
            return true; // no config entry -> considered valid
        }
        var t = targetOpt.get();
        boolean nOk = Math.abs(this.nitrogen - t.n) <= NPK_TOLERANCE;
        boolean pOk = Math.abs(this.phosphorus - t.p) <= NPK_TOLERANCE;
        boolean kOk = Math.abs(this.potassium - t.k) <= NPK_TOLERANCE;

        return nOk && pOk && kOk;
    }

    public Config.NutrientTarget getOptimalNutrientsLevels() {
        var cropId = getCropId();
        var targetOpt = Config.getNutrientTargetFor(cropId);
        return targetOpt.orElseGet(() -> new Config.NutrientTarget(0, 0, 0));
    }

    public int getThc() {
        return computeWithNutrients(this.thc);
    }

    public int getCbd() {
        return computeWithNutrients(this.cbd);
    }

    public int getBudCount() {
        // If the nutrients are optimal, triple the buds. If from NPK there is only 1 off, double the buds, if they are more then 1 off only 1.
        var cropId = getCropId();
        var targetOpt = Config.getNutrientTargetFor(cropId);
        if (targetOpt.isEmpty()) {
            return 1; // no config entry -> base
        }

        var t = targetOpt.get();
        int dn = Math.abs(this.nitrogen - t.n);
        int dp = Math.abs(this.phosphorus - t.p);
        int dk = Math.abs(this.potassium - t.k);
        if (dn == 0 && dp == 0 && dk == 0) {
            return 3; // optimal
        }

        int offCount = 0;
        if (dn > NPK_TOLERANCE) offCount++;
        if (dp > NPK_TOLERANCE) offCount++;
        if (dk > NPK_TOLERANCE) offCount++;
        if (offCount == 0) {
            return 2; // only 1 off
        }
        return 1;
    }

    private int computeWithNutrients(int base) {
        var cropId = getCropId();
        var targetOpt = Config.getNutrientTargetFor(cropId);
        if (targetOpt.isEmpty()) {
            return base; // no config entry -> base
        }
        var t = targetOpt.get();
        int dn = Math.abs(this.nitrogen - t.n);
        int dp = Math.abs(this.phosphorus - t.p);
        int dk = Math.abs(this.potassium - t.k);

        if (dn == 0 && dp == 0 && dk == 0) {
            return Mth.clamp(base * 2, 0, MAX_PERCENT);
        }

        int totalDiff = dn + dp + dk;
        int reduction = (int)Math.round(base * 0.10 * totalDiff);
        int floor = Math.max(1, base / 2);
        return Mth.clamp(base - reduction, floor, MAX_PERCENT);
    }

    public void writeToItem(ItemStack stack) {
        StrainData existing = stack.get(ModDataComponentTypes.STRAIN_DATA.get());
        if (existing != null) {
            stack.set(ModDataComponentTypes.STRAIN_DATA.get(), new StrainData(
                    existing.colorArgb(), existing.leafColor(), thc, cbd,
                    existing.nitrogen(), existing.phosphorus(), existing.potassium(),
                    existing.effects(), existing.amplifier(), existing.durationTicks(),
                    existing.identified(), existing.displayName(),
                    existing.typeColors()
            ));
        } else {
            stack.set(ModDataComponentTypes.THC.get(), thc);
            stack.set(ModDataComponentTypes.CBD.get(), cbd);
        }
    }


    // Server / Client Syncing
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

    public void sync() {
        if (this.level instanceof ServerLevel server) {
            this.setChanged();
            server.sendBlockUpdated(this.worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

}
