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
    /**
     * Purely cosmetic toggle with no effect on shape/model choice — both values map to the same
     * model. Its only purpose is to give {@link net.micaxs.smokeleaf.block.entity.UnidentifiedWeedCropBlockEntity#sync()}
     * a genuine BlockState value to flip. Merely re-sending block-entity data (which is all a
     * normal strain update needs) never triggers a client-side chunk re-render on its own —
     * Level.setBlock() short-circuits before notifying the renderer whenever the BlockState value
     * doesn't actually change — so without this, the BlockColor tint (which reads this block's
     * StrainData) stays stuck at its old color until some unrelated real state change (e.g. the
     * crop's next growth tick) happens to force a rebuild.
     */
    public static final BooleanProperty RENDER_SYNC = BooleanProperty.create("render_sync");

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
                .setValue(TOP, false)
                .setValue(RENDER_SYNC, false));
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
        return ModItems.GENERIC_SEEDS.get();
    }

    @Override
    public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE, TOP, RENDER_SYNC);
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
            String sid = stack.get(ModDataComponentTypes.STRAIN_ID.get());
            if (sid != null && !sid.isBlank()) cropBe.setStrainId(sid);
            String creator = stack.get(ModDataComponentTypes.STRAIN_CREATOR.get());
            if (creator != null && !creator.isBlank()) cropBe.setStrainCreator(creator);
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
            ItemStack seed = new ItemStack(ModItems.GENERIC_SEEDS.get());
        BlockEntity be = builder.getOptionalParameter(net.minecraft.world.level.storage.loot.parameters.LootContextParams.BLOCK_ENTITY);
        if (be instanceof UnidentifiedWeedCropBlockEntity cropBe) {
            StrainData d = cropBe.getStrain();
            if (d != StrainData.EMPTY) seed.set(ModDataComponentTypes.STRAIN_DATA.get(), d);
            String sid = cropBe.getStrainId();
            if (!sid.isBlank()) seed.set(ModDataComponentTypes.STRAIN_ID.get(), sid);
            String creator = cropBe.getStrainCreator();
            if (!creator.isBlank()) seed.set(ModDataComponentTypes.STRAIN_CREATOR.get(), creator);
        }
            return java.util.List.of(seed);
        }

        BlockEntity be = builder.getOptionalParameter(net.minecraft.world.level.storage.loot.parameters.LootContextParams.BLOCK_ENTITY);
        StrainData original = (be instanceof UnidentifiedWeedCropBlockEntity cropBe) ? cropBe.getStrain() : StrainData.EMPTY;
        String sid = (be instanceof UnidentifiedWeedCropBlockEntity cropBe2) ? cropBe2.getStrainId() : "";
        String creator = (be instanceof UnidentifiedWeedCropBlockEntity cropBe3) ? cropBe3.getStrainCreator() : "";

        // Compute bud scaling (THC/CBD and count) but keep original strain data for the seed drop
        int budCount = 1;
        StrainData budStrain = original;
        if (original != StrainData.EMPTY && be instanceof UnidentifiedWeedCropBlockEntity cropBe4) {
            int scaledThc = cropBe4.getThc();
            int scaledCbd = cropBe4.getCbd();
            budCount = cropBe4.getBudCount();
            // Create a copy of the original strain with patched THC/CBD for buds only.
            budStrain = new StrainData(original.colorArgb(), original.leafColor(), scaledThc, scaledCbd,
                    original.nitrogen(), original.phosphorus(), original.potassium(),
                    original.effects(), original.amplifier(), original.durationTicks(),
                    original.identified(), original.displayName(), original.typeColors(),
                    original.baseStrain1(), original.baseStrain2());
        }

        // Bud + seeds (seeds keep the original strain data) + hemp leaf.
        ItemStack bud = new ItemStack(ModItems.GENERIC_BUD.get(), budCount);
        ItemStack seeds = new ItemStack(ModItems.GENERIC_SEEDS.get());
        ItemStack leaf = new ItemStack(ModItems.HEMP_LEAF.get());
        if (budStrain != StrainData.EMPTY) {
            bud.set(ModDataComponentTypes.STRAIN_DATA.get(), budStrain);
        }
        if (original != StrainData.EMPTY) {
            seeds.set(ModDataComponentTypes.STRAIN_DATA.get(), original);
        }
        if (!sid.isBlank()) {
            bud.set(ModDataComponentTypes.STRAIN_ID.get(), sid);
            seeds.set(ModDataComponentTypes.STRAIN_ID.get(), sid);
        }
        if (!creator.isBlank()) {
            bud.set(ModDataComponentTypes.STRAIN_CREATOR.get(), creator);
            seeds.set(ModDataComponentTypes.STRAIN_CREATOR.get(), creator);
        }
        return java.util.List.of(bud, seeds, leaf);
    }

    @Override
    public @NotNull ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
        ItemStack stack = new ItemStack(ModItems.GENERIC_SEEDS.get());
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