package net.micaxs.smokeleaf.block.custom;

import net.micaxs.smokeleaf.block.ModBlocks;
import net.micaxs.smokeleaf.block.entity.BaseWeedCropBlockEntity;
import net.micaxs.smokeleaf.block.entity.ModBlockEntities;
import net.micaxs.smokeleaf.component.ModDataComponentTypes;
import net.micaxs.smokeleaf.item.ModItems;
import net.micaxs.smokeleaf.strain.StrainData;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
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
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;

import java.util.List;
import java.util.function.Supplier;

public class BaseWeedCropBlock extends CropBlock implements EntityBlock {

    public static final int FIRST_STAGE_MAX_AGE = 6;
    public static final int SECOND_STAGE_MAX_AGE = 4;

    public static final IntegerProperty AGE = IntegerProperty.create("age", 0, 10);
    public static final BooleanProperty TOP = BooleanProperty.create("top");

    /**
     * Only used for hemp: counts how many times a fully-grown hemp plant was sheared.
     * After 3 leaf drops, the 4th shear breaks/harvests the crop.
     */
    public static final EnumProperty<HempShearStage> HEMP_SHEAR_STAGE = EnumProperty.create("hemp_shear_stage", HempShearStage.class);

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
                .setValue(TOP, false)
                .setValue(HEMP_SHEAR_STAGE, HempShearStage.S0));
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
        builder.add(AGE, TOP, HEMP_SHEAR_STAGE);
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

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        // Allow other interactions to behave normally.
        return super.useWithoutItem(state, level, pos, player, hitResult);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                          Player player, InteractionHand hand, BlockHitResult hitResult) {
        // Only run this behavior on the actual hemp crop.
        if (!state.is(ModBlocks.HEMP_CROP.get())) {
            return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
        }

        // Only bottom part should handle interactions.
        if (isTop(state)) {
            return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
        }

        // Must be fully-grown.
        if (getAge(state) != getMaxAge()) {
            return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
        }

        // Only with shears.
        if (!stack.is(Items.SHEARS)) {
            return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
        }

        if (level.isClientSide) {
            // Let client show hand swing; actual drops/state changes happen server-side.
            return ItemInteractionResult.SUCCESS;
        }

        HempShearStage stage = state.getValue(HEMP_SHEAR_STAGE);

        if (stage.nextOrNull() != null) {
            // First 3 times: drop a hemp leaf and advance stage.
            popResource(level, pos, new ItemStack(ModItems.HEMP_LEAF.get()));
            BlockState newState = state.setValue(HEMP_SHEAR_STAGE, stage.nextOrNull());
            level.setBlock(pos, newState, 2);
            level.gameEvent(player, GameEvent.SHEAR, pos);

            // Damage shears.
            stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));
            return ItemInteractionResult.CONSUME;
        }

        // 4th time: break/harvest the crop normally (uses loot table, etc.)
        // Also remove the top half if it exists.
        BlockPos topPos = pos.above();
        if (level.getBlockState(topPos).getBlock() == this) {
            level.destroyBlock(topPos, false);
        }
        level.destroyBlock(pos, true);
        level.gameEvent(player, GameEvent.BLOCK_DESTROY, pos);

        stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));
        return ItemInteractionResult.CONSUME;
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        List<ItemStack> drops = super.getDrops(state, builder);

        // Apply nutrient-scaled THC/CBD to any bud drops that carry STRAIN_DATA
        BlockEntity be = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if (!(be instanceof BaseWeedCropBlockEntity cropBe)) return drops;

        int scaledThc = cropBe.getThc();
        int scaledCbd = cropBe.getCbd();

        for (ItemStack drop : drops) {
            StrainData d = drop.get(ModDataComponentTypes.STRAIN_DATA.get());
            if (d != null) {
                drop.set(ModDataComponentTypes.STRAIN_DATA.get(), new StrainData(
                        d.colorArgb(), d.leafColor(), scaledThc, scaledCbd,
                        d.nitrogen(), d.phosphorus(), d.potassium(),
                        d.effects(), d.amplifier(), d.durationTicks(),
                        d.identified(), d.displayName(), d.typeColors()));
            }
        }
        return drops;
    }

    /** 0..3 shear clicks; on S3 the next shear harvests the plant. */
    public enum HempShearStage implements net.minecraft.util.StringRepresentable {
        S0("0"),
        S1("1"),
        S2("2"),
        S3("3");

        private final String name;

        HempShearStage(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }

        /** @return next stage for the first 3 clicks, or null when already at S3. */
        public HempShearStage nextOrNull() {
            return switch (this) {
                case S0 -> S1;
                case S1 -> S2;
                case S2 -> S3;
                case S3 -> null;
            };
        }
    }
}
