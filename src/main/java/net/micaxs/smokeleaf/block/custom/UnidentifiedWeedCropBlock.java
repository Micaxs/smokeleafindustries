package net.micaxs.smokeleaf.block.custom;

import net.micaxs.smokeleaf.block.entity.ModBlockEntities;
import net.micaxs.smokeleaf.block.entity.UnidentifiedWeedCropBlockEntity;
import net.micaxs.smokeleaf.component.ModDataComponentTypes;
import net.micaxs.smokeleaf.item.ModItems;
import net.micaxs.smokeleaf.strain.StrainData;
import net.micaxs.smokeleaf.strain.StrainUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Player-created (unidentified) weed crop.
 *
 * - Growth stages & tall behavior mirror {@link BaseWeedCropBlock}.
 * - StrainData is stored on a BlockEntity (bottom half only) copied from planted seeds.
 * - Nutrient target for this crop is derived from the StrainData N/P/K values.
 */
public class UnidentifiedWeedCropBlock extends CropBlock implements EntityBlock {

    public static final int FIRST_STAGE_MAX_AGE = BaseWeedCropBlock.FIRST_STAGE_MAX_AGE;
    public static final int SECOND_STAGE_MAX_AGE = BaseWeedCropBlock.SECOND_STAGE_MAX_AGE;
    public static final IntegerProperty AGE = BaseWeedCropBlock.AGE;
    public static final BooleanProperty TOP = BaseWeedCropBlock.TOP;

    private static final VoxelShape[] SHAPE_BY_AGE = new VoxelShape[]{
            Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0),
            Block.box(0.0, 0.0, 0.0, 16.0, 4.0, 16.0),
            Block.box(0.0, 0.0, 0.0, 16.0, 6.0, 16.0),
            Block.box(0.0, 0.0, 0.0, 16.0, 8.0, 16.0),
            Block.box(0.0, 0.0, 0.0, 16.0, 10.0, 16.0),
            Block.box(0.0, 0.0, 0.0, 16.0, 12.0, 16.0),
            Block.box(0.0, 0.0, 0.0, 16.0, 14.0, 16.0),
            Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 16.0),
            Block.box(0.0, 0.0, 0.0, 16.0, 3.0, 16.0),
            Block.box(0.0, 0.0, 0.0, 16.0, 8.0, 16.0),
            Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 16.0),
    };

    public UnidentifiedWeedCropBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(this.getAgeProperty(), 0)
                .setValue(TOP, false));
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (isTop(state)) {
            return SHAPE_BY_AGE[getAge(state)];
        }
        return SHAPE_BY_AGE[Math.max(0, Math.min(FIRST_STAGE_MAX_AGE, getAge(state)))];
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!level.isAreaLoaded(pos, 1) || isTop(state) || level.getRawBrightness(pos, 0) < 11 || !canSurvive(state, level, pos)) {
            return;
        }

        int age = this.getAge(state);
        if (age >= getMaxAge()) return;

        float growthSpeed = getGrowthSpeed(this.defaultBlockState(), level, pos);
        if (random.nextInt((int) (25.0F / growthSpeed) + 1) == 0) {
            int nextAge = age + 1;
            level.setBlock(pos, getStateForAge(nextAge), 2);
            if (nextAge >= getTallAge()) {
                level.setBlockAndUpdate(pos.above(), getStateForAge(nextAge).setValue(TOP, true));
            }
        }
    }

    @Override
    public void growCrops(Level level, BlockPos pos, BlockState state) {
        if (isTop(state) || !canSurvive(state, level, pos)) return;

        BlockPos above = pos.above();
        if (!level.isEmptyBlock(above) && level.getBlockState(above).getBlock() != this) return;

        int nextAge = this.getAge(state) + this.getBonemealAgeIncrease(level);
        nextAge = Math.min(nextAge, getMaxAge());

        level.setBlock(pos, getStateForAge(nextAge), 2);
        if (nextAge >= getTallAge()) {
            level.setBlockAndUpdate(above, getStateForAge(nextAge).setValue(TOP, true));
        }
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        if (isTop(state)) {
            BlockState below = level.getBlockState(pos.below());
            return below.getBlock() == this && below.getValue(AGE) >= getTallAge();
        }

        if (getAge(state) >= getTallAge()) {
            BlockState above = level.getBlockState(pos.above());
            if (above.getBlock() == this && above.getValue(AGE) <= getTallAge() - 1) {
                return false;
            }
            return above.getBlock() == this && super.canSurvive(state, level, pos);
        }

        return pos.getY() < level.getMaxBuildHeight() &&
                super.canSurvive(state, level, pos) &&
                level.isEmptyBlock(pos.above());
    }

    @Override
    protected boolean isRandomlyTicking(BlockState state) {
        return !state.getValue(TOP);
    }

    @Override
    public IntegerProperty getAgeProperty() {
        return AGE;
    }

    @Override
    public int getMaxAge() {
        return FIRST_STAGE_MAX_AGE + SECOND_STAGE_MAX_AGE;
    }

    @Override
    protected ItemLike getBaseSeedId() {
        return ModItems.UNIDENTIFIED_SEEDS.get();
    }

    @Override
    public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE, TOP);
    }

    protected boolean isTop(BlockState state) {
        return state.getValue(TOP);
    }

    public int getTallAge() {
        return FIRST_STAGE_MAX_AGE + 1;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        // Only bottom half owns a BlockEntity
        if (state.hasProperty(TOP) && state.getValue(TOP)) {
            return null;
        }

        return ModBlockEntities.UNIDENTIFIED_WEED_CROP_BE.get().create(pos, state);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable net.minecraft.world.entity.LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (level.isClientSide) return;
        if (isTop(state)) return;

        StrainData d = StrainUtil.getStrain(stack);
        if (d == StrainData.EMPTY) return;
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof UnidentifiedWeedCropBlockEntity cropBe) {
            cropBe.setStrain(d);
            cropBe.sync();
        }
    }

    private StrainData getStrainAt(LevelReader level, BlockPos pos) {
        if (level instanceof Level l) {
            BlockEntity be = l.getBlockEntity(pos);
            if (be instanceof UnidentifiedWeedCropBlockEntity cropBe) {
                return cropBe.getStrain();
            }
        }
        return StrainData.EMPTY;
    }

    public boolean isValidNutrientsLevels(LevelReader level, BlockPos pos) {
        StrainData d = getStrainAt(level, pos);
        if (d == StrainData.EMPTY) return true;
        // For this crop, the strain's NPK values are the target.
        // We compare against the crop BE's current NPK values.
        if (level instanceof Level l) {
            BlockEntity be = l.getBlockEntity(pos);
            if (be instanceof UnidentifiedWeedCropBlockEntity cropBe) {
                return cropBe.isValidAgainstTarget(d.nitrogen(), d.phosphorus(), d.potassium());
            }
        }
        return true;
    }

    @Override
    public java.util.List<ItemStack> getDrops(BlockState state, net.minecraft.world.level.storage.loot.LootParams.Builder builder) {
        // Let loot tables handle vanilla crops. For the unidentified crop we need to include StrainData on drops.
        // We'll keep it simple: only bottom segment drops, matching existing crop behavior.
        if (state.getValue(TOP)) {
            return java.util.List.of();
        }

        // If not fully grown, drop 1 seed.
        if (state.getValue(AGE) < getMaxAge()) {
            ItemStack seed = new ItemStack(ModItems.UNIDENTIFIED_SEEDS.get());
            BlockEntity be = builder.getOptionalParameter(net.minecraft.world.level.storage.loot.parameters.LootContextParams.BLOCK_ENTITY);
            if (be instanceof UnidentifiedWeedCropBlockEntity cropBe) {
                StrainData d = cropBe.getStrain();
                if (d != StrainData.EMPTY) seed.set(ModDataComponentTypes.STRAIN_DATA.get(), d);
            }
            return java.util.List.of(seed);
        }

        BlockEntity be = builder.getOptionalParameter(net.minecraft.world.level.storage.loot.parameters.LootContextParams.BLOCK_ENTITY);
        StrainData d = (be instanceof UnidentifiedWeedCropBlockEntity cropBe) ? cropBe.getStrain() : StrainData.EMPTY;

        // Weed + seeds with strain + hemp leaf.
        ItemStack weed = new ItemStack(ModItems.UNIDENTIFIED_WEED.get());
        ItemStack seeds = new ItemStack(ModItems.UNIDENTIFIED_SEEDS.get());
        ItemStack leaf = new ItemStack(ModItems.HEMP_LEAF.get());
        if (d != StrainData.EMPTY) {
            weed.set(ModDataComponentTypes.STRAIN_DATA.get(), d);
            seeds.set(ModDataComponentTypes.STRAIN_DATA.get(), d);
        }
        return java.util.List.of(weed, seeds, leaf);
    }

    @Override
    public @NotNull ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
        ItemStack stack = new ItemStack(ModItems.UNIDENTIFIED_SEEDS.get());
        StrainData d = getStrainAt(level, isTop(state) ? pos.below() : pos);
        if (d != StrainData.EMPTY) {
            stack.set(ModDataComponentTypes.STRAIN_DATA.get(), d);
            String name = d.displayName() == null ? "" : d.displayName();
            if (!name.isEmpty()) {
                stack.set(DataComponents.CUSTOM_NAME, Component.literal(name + " Plant"));
            }
        }
        return stack;
    }
}