package net.micaxs.smokeleaf.compat;

import net.micaxs.smokeleaf.block.custom.BaseWeedCropBlock;
import net.micaxs.smokeleaf.block.custom.DryingRackBlock;
import net.micaxs.smokeleaf.block.custom.GrowPotBlock;
import net.micaxs.smokeleaf.block.custom.ReflectorBlock;
import net.micaxs.smokeleaf.block.custom.UnidentifiedWeedCropBlock;
import net.micaxs.smokeleaf.compat.jade.DryingRackProvider;
import net.micaxs.smokeleaf.compat.jade.GrowPotProvider;
import net.micaxs.smokeleaf.compat.jade.ReflectorProvider;
import net.micaxs.smokeleaf.compat.jade.UnidentifiedCropNameProvider;
import net.micaxs.smokeleaf.compat.jade.WeedCropProvider;
import net.micaxs.smokeleaf.block.entity.BaseWeedCropBlockEntity;
import net.micaxs.smokeleaf.block.entity.DryingRackBlockEntity;
import net.micaxs.smokeleaf.block.entity.GrowPotBlockEntity;
import net.micaxs.smokeleaf.block.entity.ReflectorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class JadeSmokeleafIndustriesPlugin implements IWailaPlugin {

    @Override
    public void register(IWailaCommonRegistration registration) {
        // Jade 11 keys server data by block entity class (UnidentifiedWeedCropBlockEntity extends BaseWeedCropBlockEntity).
        registration.registerBlockDataProvider(WeedCropProvider.INSTANCE, BaseWeedCropBlockEntity.class);
        registration.registerBlockDataProvider(GrowPotProvider.INSTANCE, GrowPotBlockEntity.class);
        registration.registerBlockDataProvider(DryingRackProvider.INSTANCE, DryingRackBlockEntity.class);
        registration.registerBlockDataProvider(ReflectorProvider.INSTANCE, ReflectorBlockEntity.class);

    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(WeedCropProvider.INSTANCE, BaseWeedCropBlock.class);
        registration.registerBlockComponent(WeedCropProvider.INSTANCE, UnidentifiedWeedCropBlock.class);
        registration.registerBlockComponent(UnidentifiedCropNameProvider.INSTANCE, UnidentifiedWeedCropBlock.class);
        registration.registerBlockComponent(GrowPotProvider.INSTANCE, GrowPotBlock.class);
        registration.registerBlockComponent(DryingRackProvider.INSTANCE, DryingRackBlock.class);
        registration.registerBlockComponent(ReflectorProvider.INSTANCE, ReflectorBlock.class);

        // The top half of a two-tall crop has no block entity, and Jade 11 only syncs server data
        // for block entities — so point lookups at the top half to the bottom half instead.
        registration.addRayTraceCallback((hitResult, accessor, originalAccessor) -> {
            if (!(accessor instanceof BlockAccessor blockAccessor) || blockAccessor.getBlockEntity() != null) return accessor;
            BlockState state = blockAccessor.getBlockState();
            boolean top = (state.getBlock() instanceof BaseWeedCropBlock && state.getValue(BaseWeedCropBlock.TOP))
                    || (state.getBlock() instanceof UnidentifiedWeedCropBlock && state.getValue(UnidentifiedWeedCropBlock.TOP));
            if (!top) return accessor;
            BlockPos below = blockAccessor.getPosition().below();
            BlockEntity be = blockAccessor.getLevel().getBlockEntity(below);
            if (be == null) return accessor;
            return registration.blockAccessor()
                    .from(blockAccessor)
                    .blockState(blockAccessor.getLevel().getBlockState(below))
                    .blockEntity(be)
                    .hit(blockAccessor.getHitResult().withPosition(below))
                    .build();
        });
    }

}
