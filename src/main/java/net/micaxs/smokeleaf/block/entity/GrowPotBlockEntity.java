package net.micaxs.smokeleaf.block.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.micaxs.smokeleaf.Config;
import net.micaxs.smokeleaf.block.ModBlocks;
import net.micaxs.smokeleaf.block.custom.BaseWeedCropBlock;
import net.micaxs.smokeleaf.block.custom.UnidentifiedWeedCropBlock;
import net.micaxs.smokeleaf.component.ModDataComponentTypes;
import net.micaxs.smokeleaf.item.ModItems;
import net.micaxs.smokeleaf.item.custom.BaseBudItem;
import net.micaxs.smokeleaf.strain.StrainData;
import net.micaxs.smokeleaf.strain.StrainUtil;
import net.micaxs.smokeleaf.utils.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
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

    /**
     * Lag-safe throttle for auto-harvest/export. We only attempt when harvestable,
     * and at most once per second per pot.
     */
    private static final int AUTO_EXPORT_INTERVAL_TICKS = 20;
    private int autoExportCooldown = 0;

    @Nullable
    private StrainData customStrain;
    @Nullable
    private String customStrainId;

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
        if (!(level instanceof ServerLevel serverLevel)) return;

        // Auto-export when fully grown (runs before growth logic, throttled).
        if (be.canHarvest()) {
            be.tryAutoHarvestAndExport(serverLevel);
            return;
        }

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

    private void tryAutoHarvestAndExport(ServerLevel serverLevel) {
        // Defensive: only server side
        if (level == null || level.isClientSide) return;
        if (!canHarvest()) return;

        if (autoExportCooldown > 0) {
            autoExportCooldown--;
            return;
        }
        autoExportCooldown = AUTO_EXPORT_INTERVAL_TICKS;

        IItemHandler below = getBelowItemHandler();
        if (below == null) return; // no valid target below, do nothing (no lag)

        // Simulate harvest drops and attempt to insert everything.
        List<ItemStack> drops = getHarvestDrops(serverLevel);
        if (drops.isEmpty()) {
            // Still reset growth so it doesn't spam attempts.
            this.cropAge = 0;
            this.growthProgressTicks = 0;
            setChangedAndSync();
            return;
        }

        // Try to insert; keep leftovers.
        List<ItemStack> leftovers = new ArrayList<>();
        for (ItemStack stack : drops) {
            if (stack.isEmpty()) continue;
            ItemStack remaining = ItemHandlerHelper.insertItemStacked(below, stack, false);
            if (!remaining.isEmpty()) leftovers.add(remaining);
        }

        // If we managed to insert at least something, complete harvest and drop leftovers (if any).
        // If we couldn't insert anything, we leave the crop grown so it can try again later.
        boolean insertedSomething = leftovers.size() != drops.size();
        if (!insertedSomething) return;

        // Complete the actual harvest: reset growth
        this.cropAge = 0;
        this.growthProgressTicks = 0;
        setChangedAndSync();

        // Drop leftovers into the world (rare, only if target inventory fills up)
        for (ItemStack rem : leftovers) {
            Block.popResource(serverLevel, worldPosition, rem);
        }
    }

    @Nullable
    private IItemHandler getBelowItemHandler() {
        if (level == null) return null;
        BlockPos belowPos = worldPosition.below();

        // Try to find an item handler below, exposed on its UP face.
        return level.getCapability(Capabilities.ItemHandler.BLOCK, belowPos, Direction.UP);
    }

    private List<ItemStack> getHarvestDrops(ServerLevel serverLevel) {
        if (cropBlock == null) return List.of();

        // Custom strain: emit UNIDENTIFIED_BUD with strain data (mirrors harvest() logic)
        if (hasCustomStrain()) {
            ItemStack bud = new ItemStack(ModItems.GENERIC_BUD.get());
            StrainData d = customStrain;
            if (d != null) {
                d = new StrainData(d.colorArgb(), d.leafColor(), getThc(), getCbd(),
                        getNitrogen(), getPhosphorus(), getPotassium(),
                        d.effects(), d.amplifier(), d.durationTicks(),
                        d.identified(), d.displayName(),
                        d.typeColors());
                bud.set(ModDataComponentTypes.STRAIN_DATA.get(), d);
            }
            if (customStrainId != null) {
                bud.set(ModDataComponentTypes.STRAIN_ID.get(), customStrainId);
            }
            int budFactor = getBudCount();
            if (budFactor > 1) bud.setCount(bud.getCount() * budFactor);
            return List.of(bud);
        }

        BlockState lootState = cropBlock.defaultBlockState()
                .setValue(BaseWeedCropBlock.AGE, cropBlock.getMaxAge())
                .setValue(cropBlock.getTop(), Boolean.FALSE);

        LootParams.Builder builder = new LootParams.Builder(serverLevel)
                .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(worldPosition))
                .withParameter(LootContextParams.TOOL, ItemStack.EMPTY);

        List<ItemStack> drops = new ArrayList<>(lootState.getDrops(builder));

        // Remove seed drops (same behavior as manual harvest)
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
                StrainData existing = drop.get(ModDataComponentTypes.STRAIN_DATA.get());
                if (existing != null) {
                    drop.set(ModDataComponentTypes.STRAIN_DATA.get(), new StrainData(
                            existing.colorArgb(), existing.leafColor(), thcVal, cbdVal,
                            existing.nitrogen(), existing.phosphorus(), existing.potassium(),
                            existing.effects(), existing.amplifier(), existing.durationTicks(),
                            existing.identified(), existing.displayName(),
                            existing.typeColors()
                    ));
                } else {
                    BaseBudItem.setThc(drop, thcVal);
                    BaseBudItem.setCbd(drop, cbdVal);
                }
            }
        }

        return drops;
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
        clearCustomStrain();
        setChangedAndSync();
    }

    @Nullable public BlockState getSoilState() { return soilState; }

    @Nullable
    public BlockState getBottomCropStateForRender() {
        if (cropBlock == null) return null;
        int age = Math.min(cropAge, cropBlock.getMaxAge());
        if (hasCustomStrain()) {
            return ModBlocks.UNIDENTIFIED_WEED_CROP.get().defaultBlockState()
                    .setValue(UnidentifiedWeedCropBlock.AGE, age)
                    .setValue(UnidentifiedWeedCropBlock.TOP, Boolean.FALSE);
        }
        return cropBlock.defaultBlockState()
                .setValue(BaseWeedCropBlock.AGE, age)
                .setValue(cropBlock.getTop(), Boolean.FALSE);
    }

    @Nullable
    public BlockState getTopCropStateForRender() {
        if (cropBlock == null) return null;
        if (hasCustomStrain()) {
            int tallAge = UnidentifiedWeedCropBlock.FIRST_STAGE_MAX_AGE + 1;
            if (cropAge < tallAge) return null;
            int age = Math.min(cropAge, cropBlock.getMaxAge());
            return ModBlocks.UNIDENTIFIED_WEED_CROP.get().defaultBlockState()
                    .setValue(UnidentifiedWeedCropBlock.AGE, age)
                    .setValue(UnidentifiedWeedCropBlock.TOP, Boolean.TRUE);
        }
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

        // Custom strain harvest: bypass vanilla crop drops and directly emit Unidentified Bud.
        if (hasCustomStrain()) {
            ItemStack bud = new ItemStack(ModItems.GENERIC_BUD.get());
            StrainData d = customStrain;
            if (d != null) {
                // Apply current pot nutrient-derived THC/CBD into the strain data (so player care matters)
                d = new StrainData(d.colorArgb(), d.leafColor(), getThc(), getCbd(), getNitrogen(), getPhosphorus(), getPotassium(),
                        d.effects(), d.amplifier(), d.durationTicks(), d.identified(), d.displayName(),
                        d.typeColors());
                bud.set(ModDataComponentTypes.STRAIN_DATA.get(), d);
            }
            if (customStrainId != null) {
                bud.set(ModDataComponentTypes.STRAIN_ID.get(), customStrainId);
            }
            int budFactor = getBudCount();
            if (budFactor > 1) bud.setCount(bud.getCount() * budFactor);
            Block.popResource(serverLevel, worldPosition, bud);

            this.cropAge = 0;
            this.growthProgressTicks = 0;
            setChangedAndSync();
            return;
        }

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
                StrainData existing = drop.get(ModDataComponentTypes.STRAIN_DATA.get());
                if (existing != null) {
                    drop.set(ModDataComponentTypes.STRAIN_DATA.get(), new StrainData(
                            existing.colorArgb(), existing.leafColor(), thcVal, cbdVal,
                            existing.nitrogen(), existing.phosphorus(), existing.potassium(),
                            existing.effects(), existing.amplifier(), existing.durationTicks(),
                            existing.identified(), existing.displayName(),
                            existing.typeColors()
                    ));
                } else {
                    BaseBudItem.setThc(drop, thcVal);
                    BaseBudItem.setCbd(drop, cbdVal);
                }
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

        // Return correct seed for custom strains
        if (hasCustomStrain()) {
            ItemStack seed = new ItemStack(ModItems.GENERIC_SEEDS.get());
            StrainData d = customStrain;
            if (d != null) seed.set(ModDataComponentTypes.STRAIN_DATA.get(), d);
            if (customStrainId != null) seed.set(ModDataComponentTypes.STRAIN_ID.get(), customStrainId);
            if (!player.addItem(seed)) {
                Block.popResource(level, worldPosition, seed);
            }
            clearCrop();
            return true;
        }

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
        if (hasCustomStrain() && customStrain != null) {
            return new Config.NutrientTarget(customStrain.nitrogen(), customStrain.phosphorus(), customStrain.potassium());
        }
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

    public void setCustomStrain(ItemStack seedStack) {
        if (seedStack == null || seedStack.isEmpty()) {
            this.customStrain = null;
            this.customStrainId = null;
            return;
        }
        StrainData d = seedStack.get(ModDataComponentTypes.STRAIN_DATA.get());
        this.customStrain = (d != null && d != StrainData.EMPTY) ? d : null;
        this.customStrainId = seedStack.get(ModDataComponentTypes.STRAIN_ID.get());

        // Seed's strain values override the crop-block defaults so growth starts at the right baseline
        if (this.customStrain != null) {
            this.thc = this.customStrain.thc();
            this.cbd = this.customStrain.cbd();
            this.nitrogen = this.customStrain.nitrogen();
            this.phosphorus = this.customStrain.phosphorus();
            this.potassium = this.customStrain.potassium();
        }
    }

    public boolean hasCustomStrain() {
        return customStrain != null;
    }

    @Nullable
    public StrainData getCustomStrain() {
        return customStrain;
    }

    public void clearCustomStrain() {
        this.customStrain = null;
        this.customStrainId = null;
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

        if (customStrain != null && customStrain != StrainData.EMPTY) {
            tag.put("custom_strain", (CompoundTag) StrainData.CODEC.encodeStart(NbtOps.INSTANCE, customStrain).result().orElse(new CompoundTag()));
        }
        if (customStrainId != null) {
            tag.putString("custom_strain_id", customStrainId);
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.soilState = null;
        this.cropBlock = null;
        this.cropAge = 0;
        this.growthProgressTicks = 0;
        this.thc = this.cbd = this.ph = this.nitrogen = this.phosphorus = this.potassium = 0;
        this.autoExportCooldown = 0;
        this.customStrain = null;
        this.customStrainId = null;

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
        if (tag.contains("custom_strain")) {
            StrainData.CODEC.parse(NbtOps.INSTANCE, tag.get("custom_strain"))
                    .result()
                    .ifPresent(d -> this.customStrain = d);
        }
        if (tag.contains("custom_strain_id")) {
            this.customStrainId = tag.getString("custom_strain_id");
        }
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(2);
        int slot = 0;
        if (hasSoil() && soilState != null) {
            inventory.setItem(slot++, new ItemStack(soilState.getBlock()));
        }
        if (hasCrop() && cropBlock != null) {
            ItemStack seedStack;
            if (hasCustomStrain()) {
                seedStack = new ItemStack(ModItems.GENERIC_SEEDS.get());
                if (customStrain != null && customStrain != StrainData.EMPTY) {
                    seedStack.set(ModDataComponentTypes.STRAIN_DATA.get(), customStrain);
                }
                if (customStrainId != null) {
                    seedStack.set(ModDataComponentTypes.STRAIN_ID.get(), customStrainId);
                }
            } else {
                seedStack = new ItemStack(cropBlock.getBaseSeedId().asItem());
            }
            inventory.setItem(slot, seedStack);
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
