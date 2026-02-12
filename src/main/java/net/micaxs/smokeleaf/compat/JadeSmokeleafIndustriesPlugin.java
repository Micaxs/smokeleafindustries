package net.micaxs.smokeleaf.compat;

import net.micaxs.smokeleaf.block.custom.BaseWeedCropBlock;
import net.micaxs.smokeleaf.block.custom.DryingRackBlock;
import net.micaxs.smokeleaf.block.custom.GrowPotBlock;
import net.micaxs.smokeleaf.block.custom.ReflectorBlock;
import net.micaxs.smokeleaf.block.custom.UnidentifiedWeedCropBlock;
import net.micaxs.smokeleaf.compat.jade.DryingRackProvider;
import net.micaxs.smokeleaf.compat.jade.GrowPotProvider;
import net.micaxs.smokeleaf.compat.jade.ReflectorProvider;
import net.micaxs.smokeleaf.compat.jade.WeedCropProvider;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class JadeSmokeleafIndustriesPlugin implements IWailaPlugin {

    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(WeedCropProvider.INSTANCE, BaseWeedCropBlock.class);
        registration.registerBlockDataProvider(WeedCropProvider.INSTANCE, UnidentifiedWeedCropBlock.class);
        registration.registerBlockDataProvider(GrowPotProvider.INSTANCE, GrowPotBlock.class);
        registration.registerBlockDataProvider(DryingRackProvider.INSTANCE, DryingRackBlock.class);
        registration.registerBlockDataProvider(ReflectorProvider.INSTANCE, ReflectorBlock.class);

    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(WeedCropProvider.INSTANCE, BaseWeedCropBlock.class);
        registration.registerBlockComponent(WeedCropProvider.INSTANCE, UnidentifiedWeedCropBlock.class);
        registration.registerBlockComponent(GrowPotProvider.INSTANCE, GrowPotBlock.class);
        registration.registerBlockComponent(DryingRackProvider.INSTANCE, DryingRackBlock.class);
        registration.registerBlockComponent(ReflectorProvider.INSTANCE, ReflectorBlock.class);
    }

}
