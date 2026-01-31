package net.micaxs.smokeleaf.block.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.micaxs.smokeleaf.Config;
import net.micaxs.smokeleaf.block.custom.BaseWeedCropBlock;
import net.micaxs.smokeleaf.item.custom.BaseBudItem;
import net.micaxs.smokeleaf.utils.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GrowPotBlockEntity extends BlockEntity {

    public static int FULL_GROWTH_SECONDS_FAST = 60;
    public static int FULL_GROWTH_SECONDS_SLOW = 90;

    private static final int MAX_PERCENT = 100;
    private static final int MAX_PH = 14;

    @Nullable private BlockState soilState;
    @Nullable private BaseWeedCropBlock cropBlock;
    private int cropAge;
    private int growthProgressTicks;

    // Virtual crop stats stored in the pot
    private int thc;
    private int cbd;
    private int ph;
    private int nitrogen;
    private int phosphorus;
    private int potassium;

    public GrowPotBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.GROW_POT.get(), pos, state);
    }

    // Initialize stats from crop definition when planting
    public void initFromCrop(BaseWeedCropBlock crop) {
        this.thc = crop.getBaseThc();
        this.cbd = crop.getBaseCbd();
        this.nitrogen = crop.getBaseN();
        this.phosphorus = crop.getBaseP();
        this.potassium = crop.getBaseK();
        this.ph = crop.getBasePh();
    }

    // Nutrient mutators (clamped)
    public void setThc(int v) { this.thc = Mth.clamp(v, 0, MAX_PERCENT); }
    public void setCbd(int v) { this.cbd = Mth.clamp(v, 0, MAX_PERCENT); }
    public void setPh(int v) { this.ph = Mth.clamp(v, 0, MAX_PH); }
    public void setNitrogen(int v) { this.nitrogen = Mth.clamp(v, 0, MAX_PERCENT); }
    public void setPhosphorus(int v) { this.phosphorus = Mth.clamp(v, 0, MAX_PERCENT); }
    public void setPotassium(int v) { this.potassium = Mth.clamp(v, 0, MAX_PERCENT); }

    public void addNitrogen(int d) { setNitrogen(this.nitrogen + d); }
    public void addPhosphorus(int d) { setPhosphorus(this.phosphorus + d); }
    public void addPotassium(int d) { setPotassium(this.potassium + d); }
    public void addPh(int d) { setPh(this.ph + d); }

    public int getNitrogen() { return nitrogen; }
    public int getPhosphorus() { return phosphorus; }
    public int getPotassium() { return potassium; }

    private record PotData(Optional<ResourceLocation> soil, Optional<ResourceLocation> crop, int age, int prog,
                           int thc, int cbd, int ph, int n, int p, int k) {
        static final Codec<PotData> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                ResourceLocation.CODEC.optionalFieldOf("soil").forGetter(PotData::soil),
                ResourceLocation.CODEC.optionalFieldOf("crop").forGetter(PotData::crop),
                Codec.INT.fieldOf("age").forGetter(PotData::age),
                Codec.INT.fieldOf("prog").forGetter(PotData::prog),
                Codec.INT.fieldOf("thc").forGetter(PotData::thc),
                Codec.INT.fieldOf("cbd").forGetter(PotData::cbd),
                Codec.INT.fieldOf("ph").forGetter(PotData::ph),
                Codec.INT.fieldOf("n").forGetter(PotData::n),
                Codec.INT.fieldOf("p").forGetter(PotData::p),
                Codec.INT.fieldOf("k").forGetter(PotData::k)
        ).apply(inst, PotData::new));
    }

    public int getCropAge() {
        return cropAge;
    }

    public int getCropMaxAge() {
        return cropBlock != null ? cropBlock.getMaxAge() : 0;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, GrowPotBlockEntity be) {
        if (!(level instanceof ServerLevel)) return;
        if (!be.hasCrop()) return;

        int light = level.getMaxLocalRawBrightness(pos.above());
        if (light < 12) return;

        int maxAge = be.cropBlock.getMaxAge();
        if (be.cropAge >= maxAge) return;

        int fullGrowthTicks = be.getFullGrowthTicksForSoil();
        int perStepTicks = Math.max(1, fullGrowthTicks / Math.max(1, maxAge));

        int startAge = be.cropAge;

        be.growthProgressTicks++;
        while (be.growthProgressTicks >= perStepTicks && be.cropAge < maxAge) {
            be.growthProgressTicks -= perStepTicks;
            be.cropAge++;
        }

        if (be.cropAge != startAge) {
            be.setChangedAndSync();
        }
    }

    private int getFullGrowthTicksForSoil() {
        boolean fast = soilState != null && soilState.getBlock() instanceof FarmBlock;
        int seconds = fast ? FULL_GROWTH_SECONDS_FAST : FULL_GROWTH_SECONDS_SLOW;
        return Math.max(1, seconds * 20);
    }

    public boolean hasSoil() { return soilState != null; }
    public boolean hasCrop() { return cropBlock != null; }

    public void setSoil(BlockState soil) {
        this.soilState = soil;
        setChangedAndSync();
    }

    public void clearSoil() {
        this.soilState = null;
        clearCrop();
    }

    public void plantCrop(BaseWeedCropBlock crop) {
        this.cropBlock = crop;
        this.cropAge = 0;
        this.growthProgressTicks = 0;
        initFromCrop(crop);
        setChangedAndSync();
    }

    public void clearCrop() {
        this.cropBlock = null;
        this.cropAge = 0;
        this.growthProgressTicks = 0;
        this.thc = this.cbd = this.ph = this.nitrogen = this.phosphorus = this.potassium = 0;
        setChangedAndSync();
    }

    @Nullable public BlockState getSoilState() { return soilState; }

    @Nullable
    public BlockState getBottomCropStateForRender() {
        if (cropBlock == null) return null;
        return cropBlock.defaultBlockState()
                .setValue(BaseWeedCropBlock.AGE, Math.min(cropAge, cropBlock.getMaxAge()))
                .setValue(cropBlock.getTop(), Boolean.FALSE);
    }

    @Nullable
    public BlockState getTopCropStateForRender() {
        if (cropBlock == null) return null;
        int tallAge = cropBlock.getTallAge();
        if (cropAge < tallAge) return null;
        return cropBlock.defaultBlockState()
                .setValue(BaseWeedCropBlock.AGE, Math.min(cropAge, cropBlock.getMaxAge()))
                .setValue(cropBlock.getTop(), Boolean.TRUE);
    }

    public boolean canHarvest() {
        return hasCrop() && cropAge >= cropBlock.getMaxAge();
    }

    public boolean applyBonemeal(Level level) {
        if (!hasCrop()) return false;
        int maxAge = cropBlock.getMaxAge();
        if (cropAge >= maxAge) return false;

        int inc = Mth.nextInt(level.random, 2, 5);
        int newAge = Math.min(maxAge, cropAge + inc);
        if (newAge != cropAge) {
            cropAge = newAge;
            growthProgressTicks = 0;
            return true;
        }
        return false;
    }

    public void harvest(ServerLevel serverLevel) {
        if (!canHarvest()) return;

        BlockState lootState = cropBlock.defaultBlockState()
                .setValue(BaseWeedCropBlock.AGE, cropBlock.getMaxAge())
                .setValue(cropBlock.getTop(), Boolean.FALSE);

        LootParams.Builder builder = new LootParams.Builder(serverLevel)
                .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(worldPosition))
                .withParameter(LootContextParams.TOOL, ItemStack.EMPTY);

        List<ItemStack> drops = new ArrayList<>(lootState.getDrops(builder));

        Item seedItem = cropBlock.getBaseSeedId().asItem();
        drops.removeIf(stk -> stk.is(seedItem));

        int budFactor = getBudCount();
        int thcVal = getThc();
        int cbdVal = getCbd();

        for (ItemStack drop : drops) {
            if (drop.getItem() instanceof BaseBudItem) {
                if (drop.getCount() > 0 && budFactor > 1) {
                    drop.setCount(drop.getCount() * budFactor);
                }
                BaseBudItem.setThc(drop, thcVal);
                BaseBudItem.setCbd(drop, cbdVal);
            }
        }

        for (ItemStack drop : drops) {
            Block.popResource(serverLevel, worldPosition, drop);
        }

        this.cropAge = 0;
        this.growthProgressTicks = 0;
        setChangedAndSync();
    }

    public boolean removeCropAndGiveSeed(ServerLevel level, Player player) {
        if (!hasCrop()) return false;
        Item seedItem = cropBlock.getBaseSeedId().asItem();
        ItemStack seed = new ItemStack(seedItem);
        if (!player.addItem(seed)) {
            Block.popResource(level, worldPosition, seed);
        }
        clearCrop();
        return true;
    }

    public boolean removeSoilAndGiveBack(ServerLevel level, Player player) {
        if (!hasSoil() || hasCrop()) return false;
        ItemStack soil = new ItemStack(soilState.getBlock());
        if (!player.addItem(soil)) {
            Block.popResource(level, worldPosition, soil);
        }
        clearSoil();
        return true;
    }

    public void setChangedAndSync() {
        setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    // Virtual crop "stats" API for the magnifying glass or UI
    public Config.NutrientTarget getOptimalNutrientsLevels() {
        if (cropBlock == null) return new Config.NutrientTarget(0, 0, 0);
        var cropId = BuiltInRegistries.BLOCK.getKey(cropBlock);
        return Config.getNutrientTargetFor(cropId).orElseGet(() -> new Config.NutrientTarget(0, 0, 0));
    }

    public int getThc() {
        return computeWithNutrients(this.thc);
    }

    public int getCbd() {
        return computeWithNutrients(this.cbd);
    }

    public int getBudCount() {
        if (cropBlock == null) return 0;
        var cropId = BuiltInRegistries.BLOCK.getKey(cropBlock);
        var targetOpt = Config.getNutrientTargetFor(cropId);
        if (targetOpt.isEmpty()) return 1;

        var t = targetOpt.get();
        int dn = Math.abs(this.nitrogen - t.n);
        int dp = Math.abs(this.phosphorus - t.p);
        int dk = Math.abs(this.potassium - t.k);
        if (dn == 0 && dp == 0 && dk == 0) return 3;

        final int NPK_TOL = 3;
        int offCount = 0;
        if (dn > NPK_TOL) offCount++;
        if (dp > NPK_TOL) offCount++;
        if (dk > NPK_TOL) offCount++;
        return (offCount == 0) ? 2 : 1;
    }

    private int computeWithNutrients(int base) {
        if (cropBlock == null) return base;
        var cropId = BuiltInRegistries.BLOCK.getKey(cropBlock);
        var targetOpt = Config.getNutrientTargetFor(cropId);
        if (targetOpt.isEmpty()) return base;

        var t = targetOpt.get();
        int dn = Math.abs(this.nitrogen - t.n);
        int dp = Math.abs(this.phosphorus - t.p);
        int dk = Math.abs(this.potassium - t.k);

        if (dn == 0 && dp == 0 && dk == 0) {
            return Mth.clamp(base * 2, 0, 100);
        }

        int totalDiff = dn + dp + dk;
        int reduction = (int) Math.round(base * 0.10 * totalDiff);
        int value = base - reduction;
        return Mth.clamp(value, 0, 100);
    }

    @Nullable
    public static BaseWeedCropBlock resolveCropBySeed(Item seed) {
        for (Block b : BuiltInRegistries.BLOCK) {
            if (b instanceof BaseWeedCropBlock crop && crop.getBaseSeedId().asItem() == seed) {
                return crop;
            }
        }
        return null;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        Optional<ResourceLocation> soilId = soilState != null
                ? Optional.ofNullable(BuiltInRegistries.BLOCK.getKey(soilState.getBlock()))
                : Optional.empty();
        Optional<ResourceLocation> cropId = cropBlock != null
                ? Optional.ofNullable(BuiltInRegistries.BLOCK.getKey(cropBlock))
                : Optional.empty();

        PotData data = new PotData(soilId, cropId, cropAge, growthProgressTicks,
                thc, cbd, ph, nitrogen, phosphorus, potassium);
        PotData.CODEC.encodeStart(NbtOps.INSTANCE, data)
                .resultOrPartial(err -> {})
                .ifPresent(encoded -> tag.put("Pot", encoded));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.soilState = null;
        this.cropBlock = null;
        this.cropAge = 0;
        this.growthProgressTicks = 0;
        this.thc = this.cbd = this.ph = this.nitrogen = this.phosphorus = this.potassium = 0;

        if (tag.contains("Pot")) {
            PotData.CODEC.parse(NbtOps.INSTANCE, tag.get("Pot"))
                    .result()
                    .ifPresent(data -> {
                        data.soil().ifPresent(rl -> {
                            Block b = BuiltInRegistries.BLOCK.get(rl);
                            if (b != null) this.soilState = b.defaultBlockState();
                        });
                        data.crop().ifPresent(rl -> {
                            Block b = BuiltInRegistries.BLOCK.get(rl);
                            if (b instanceof BaseWeedCropBlock crop) this.cropBlock = crop;
                        });
                        this.cropAge = Math.max(0, data.age());
                        this.growthProgressTicks = Math.max(0, data.prog());
                        this.thc = Math.max(0, data.thc());
                        this.cbd = Math.max(0, data.cbd());
                        this.ph = Math.max(0, data.ph());
                        this.nitrogen = Math.max(0, data.n());
                        this.phosphorus = Math.max(0, data.p());
                        this.potassium = Math.max(0, data.k());
                    });
        }
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(2);
        int slot = 0;
        if (hasSoil() && soilState != null) {
            inventory.setItem(slot++, new ItemStack(soilState.getBlock()));
        }
        if (hasCrop() && cropBlock != null) {
            Item seedItem = cropBlock.getBaseSeedId().asItem();
            inventory.setItem(slot, new ItemStack(seedItem));
        }
        if (level != null) {
            Containers.dropContents(level, worldPosition, inventory);
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
}
