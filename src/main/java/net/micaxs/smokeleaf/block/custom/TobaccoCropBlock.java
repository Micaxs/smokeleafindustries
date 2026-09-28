package net.micaxs.smokeleaf.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.function.Supplier;

public class TobaccoCropBlock extends CropBlock {

    // 7 growth stages: 0..6
    public static final int MAX_AGE = 6;
    public static final IntegerProperty AGE = IntegerProperty.create("age", 0, MAX_AGE);

    private static final VoxelShape[] SHAPE_BY_AGE = new VoxelShape[]{
            Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0),  // age 0
            Block.box(0.0, 0.0, 0.0, 16.0, 4.0, 16.0),  // age 1
            Block.box(0.0, 0.0, 0.0, 16.0, 6.0, 16.0),  // age 2
            Block.box(0.0, 0.0, 0.0, 16.0, 8.0, 16.0),  // age 3
            Block.box(0.0, 0.0, 0.0, 16.0, 10.0, 16.0), // age 4
            Block.box(0.0, 0.0, 0.0, 16.0, 12.0, 16.0), // age 5
            Block.box(0.0, 0.0, 0.0, 16.0, 14.0, 16.0)  // age 6 (mature)
    };

    private final Supplier<Item> seedItem;

    public TobaccoCropBlock(Properties properties, Supplier<Item> seedItem) {
        super(properties);
        this.seedItem = seedItem;
        this.registerDefaultState(this.stateDefinition.any().setValue(this.getAgeProperty(), 0));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        int age = state.getValue(AGE);
        return SHAPE_BY_AGE[Math.max(0, Math.min(MAX_AGE, age))];
    }

    @Override
    public IntegerProperty getAgeProperty() {
        return AGE;
    }

    @Override
    public int getMaxAge() {
        return MAX_AGE;
    }

    @Override
    public ItemLike getBaseSeedId() {
        return this.seedItem.get();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }
}