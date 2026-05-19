// Java
package net.micaxs.smokeleaf.compat.jade;

import net.micaxs.smokeleaf.SmokeleafIndustries;
import net.micaxs.smokeleaf.block.custom.BaseWeedCropBlock;
import net.micaxs.smokeleaf.block.custom.UnidentifiedWeedCropBlock;
import net.micaxs.smokeleaf.block.entity.BaseWeedCropBlockEntity;
import net.micaxs.smokeleaf.block.entity.UnidentifiedWeedCropBlockEntity;
import net.micaxs.smokeleaf.strain.StrainData;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.theme.IThemeHelper;

public enum WeedCropProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {
    INSTANCE;

    private static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(SmokeleafIndustries.MODID, "weed_crop_jade");

    @Override
    public ResourceLocation getUid() {
        return UID;
    }

    // Server -> client data
    @Override
    public void appendServerData(CompoundTag tag, BlockAccessor accessor) {
        BlockState state = accessor.getBlockState();
        BlockPos pos = accessor.getPosition();

        // Always grab the bottom block as that's the Entity.
        if ((state.hasProperty(BaseWeedCropBlock.TOP) && state.getValue(BaseWeedCropBlock.TOP))
                || (state.hasProperty(UnidentifiedWeedCropBlock.TOP) && state.getValue(UnidentifiedWeedCropBlock.TOP))) {
            pos = pos.below();
        }

        BlockEntity be = accessor.getLevel().getBlockEntity(pos);
        if (be instanceof BaseWeedCropBlockEntity crop) {
            // Current NPK
            tag.putInt("n", crop.getNitrogen());
            tag.putInt("p", crop.getPhosphorus());
            tag.putInt("k", crop.getPotassium());

            // Target/optimal NPK for color logic
            var target = crop.getOptimalNutrientsLevels();
            tag.putInt("tn", target.n);
            tag.putInt("tp", target.p);
            tag.putInt("tk", target.k);
        }

        // Unidentified strain name support (server -> client)
        if (be instanceof UnidentifiedWeedCropBlockEntity uCrop) {
            StrainData d = uCrop.getStrain();
            if (d != null && d != StrainData.EMPTY) {
                tag.putString("strain_name", d.displayName() == null ? "" : d.displayName());
            }
        }
    }

    // Client tooltip
    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        CompoundTag data = accessor.getServerData();

        BlockState state = accessor.getBlockState();

        if (!hasAll(data, "n", "p", "k", "tn", "tp", "tk")) return;

        int n = data.getInt("n");
        int p = data.getInt("p");
        int k = data.getInt("k");
        int tn = data.getInt("tn");
        int tp = data.getInt("tp");
        int tk = data.getInt("tk");

        // Don't add the NPK if the plant doesn't have a target (e.g. non-weed crops)
        if (tn == 0 && tp == 0 && tk == 0) return;
        
        // Nitrogen
        tooltip.add(Component.literal("Nitrogen (N): ")
                .append(Component.literal(String.valueOf(n)).withStyle(colorForDiff(Math.abs(n - tn))))
                .append(Component.literal("/").withStyle(ChatFormatting.GRAY))
                .append(Component.literal(String.valueOf(tn)).withStyle(ChatFormatting.GREEN)));

        // Phosphorus
        tooltip.add(Component.literal("Phosphorus (P): ")
                .append(Component.literal(String.valueOf(p)).withStyle(colorForDiff(Math.abs(p - tp))))
                .append(Component.literal("/").withStyle(ChatFormatting.GRAY))
                .append(Component.literal(String.valueOf(tp)).withStyle(ChatFormatting.GREEN)));

        // Potassium
        tooltip.add(Component.literal("Potassium (K): ")
                .append(Component.literal(String.valueOf(k)).withStyle(colorForDiff(Math.abs(k - tk))))
                .append(Component.literal("/").withStyle(ChatFormatting.GRAY))
                .append(Component.literal(String.valueOf(tk)).withStyle(ChatFormatting.GREEN)));
    }

    private static boolean hasAll(CompoundTag t, String... keys) {
        for (String k : keys) if (!t.contains(k)) return false;
        return true;
    }

    private static ChatFormatting colorForDiff(int diff) {
        if (diff == 0) return ChatFormatting.GREEN;
        if (diff == 1) return ChatFormatting.GOLD;
        if (diff == 2) return ChatFormatting.RED;
        return ChatFormatting.DARK_RED;
    }
}
