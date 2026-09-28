package net.micaxs.smokeleaf.compat.jade;

import net.micaxs.smokeleaf.SmokeleafIndustries;
import net.micaxs.smokeleaf.block.custom.UnidentifiedWeedCropBlock;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.Identifiers;
import snownee.jade.api.config.IPluginConfig;

public enum UnidentifiedCropNameProvider implements IBlockComponentProvider {
    INSTANCE;

    private static final ResourceLocation UID = new ResourceLocation(SmokeleafIndustries.MODID, "unidentified_crop_name");

    @Override
    public ResourceLocation getUid() {
        return UID;
    }

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        if (!(accessor.getBlockState().getBlock() instanceof UnidentifiedWeedCropBlock)) return;

        CompoundTag data = accessor.getServerData();
        String trimmed = (data.contains("strain_name") ? data.getString("strain_name") : "").trim();

        if (!trimmed.isEmpty()) {
            Component title = Component.literal(trimmed + " Plant");
            // Jade 11 has no replace(): drop the core name line and put ours in its place.
            tooltip.remove(Identifiers.CORE_OBJECT_NAME);
            tooltip.add(0, snownee.jade.api.theme.IThemeHelper.get().title(title), Identifiers.CORE_OBJECT_NAME);
        }
    }
}
