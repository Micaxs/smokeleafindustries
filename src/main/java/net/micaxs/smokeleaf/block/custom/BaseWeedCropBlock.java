package net.micaxs.smokeleaf.block.custom;

import net.micaxs.smokeleaf.block.ModBlocks;
import net.micaxs.smokeleaf.block.entity.BaseWeedCropBlockEntity;
import net.micaxs.smokeleaf.block.entity.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
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
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.function.Supplier;

public class BaseWeedCropBlock extends CropBlock implements EntityBlock {

    public static final int FIRST_STAGE_MAX_AGE = 6;
    public static final int SECOND_STAGE_MAX_AGE = 4;

    public static final IntegerProperty AGE = IntegerProperty.create("age", 0, 10);
    public static final BooleanProperty TOP = BooleanProperty.create("top");

    private static final int MAX_PERCENT = 100;
    private static final int MAX_PH = 14;

    public BaseWeedCropBlockEntity blockEntity;

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

    private final Supplier<Item> seedItem;
    private final int baseN;
    private final int baseP;
    private final int baseK;
    private final int basePh;

    private int baseThc = 0;
    private int baseCbd = 0;

    // Hemp should grow faster than the other weed crops as its required to even begin.
    private static final float HEMP_GROWTH_SPEED_MULTIPLIER = 3.5f;

    public BaseWeedCropBlock(Properties properties, Supplier<Item> seedItem) {
        this(properties, seedItem, 0, 0, 0, 7, 0, 0);
    }

    public BaseWeedCropBlock(Properties properties, Supplier<Item> seedItem, int n, int p, int k, int ph, int thc, int cbd) {
        super(properties);
        this.seedItem = seedItem;
        this.baseN = n;
        this.baseP = p;
        this.baseK = k;
        this.basePh = ph;
        this.baseThc = thc;
        this.baseCbd = cbd;

        this.registerDefaultState(this.stateDefinition.any()
                .setValue(this.getAgeProperty(), 0)
                .setValue(TOP, false));
    }

    public int getBaseThc() { return this.baseThc; }
    public int getBaseCbd() { return this.baseCbd; }
    public int getBaseN()   { return this.baseN; }
    public int getBaseP()   { return this.baseP; }
    public int getBaseK()   { return this.baseK; }
    public int getBasePh()  { return this.basePh; }

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
        if (state.is(ModBlocks.HEMP_CROP.get())) {
            growthSpeed *= HEMP_GROWTH_SPEED_MULTIPLIER;
        }

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
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
        return !isMaxAge(state) && !state.getValue(TOP);
    }

    @Override
    public ItemLike getBaseSeedId() {
        return this.seedItem.get();
    }

    @Override
    public int getMaxAge() {
        return FIRST_STAGE_MAX_AGE + SECOND_STAGE_MAX_AGE;
    }

    @Override
    public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE, TOP);
    }

    protected boolean isTop(BlockState state) {
        return state.getValue(TOP);
    }

    public BooleanProperty getTop() {
        return TOP;
    }

    public int getTallAge() {
        return FIRST_STAGE_MAX_AGE + 1;
    }

    public float getLocalGrowthSpeed(BlockGetter level, BlockPos pos) {
        return getGrowthSpeed(this.defaultBlockState(), level, pos);
    }

    public BaseWeedCropBlockEntity getBlockEntity() {
        if (this.blockEntity instanceof BaseWeedCropBlockEntity) {
            return this.blockEntity;
        }
        return null;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        // Only the bottom half owns a BlockEntity
        if (state.hasProperty(TOP) && state.getValue(TOP)) {
            return null;
        }

        BaseWeedCropBlockEntity be = ModBlockEntities.BASE_WEED_CROP_BE.get().create(pos, state);
        if (be != null) {
            be.setThc(this.baseThc);
            be.setCbd(this.baseCbd);
            be.setNitrogen(this.baseN);
            be.setPhosphorus(this.baseP);
            be.setPotassium(this.baseK);
            be.setPh(this.basePh);
        }
        this.blockEntity = be;
        return be;
    }
}
