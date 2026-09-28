// Java
package net.micaxs.smokeleaf.compat.jade;

import net.micaxs.smokeleaf.SmokeleafIndustries;
import net.micaxs.smokeleaf.block.custom.ReflectorBlock;
import net.micaxs.smokeleaf.block.entity.ReflectorBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public enum ReflectorProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {
    INSTANCE;

    private static final ResourceLocation UID =
            new ResourceLocation(SmokeleafIndustries.MODID, "reflector");

    @Override
    public ResourceLocation getUid() {
        return UID;
    }

    @Override
    public void appendServerData(CompoundTag tag, BlockAccessor accessor) {
        if (!(accessor.getBlockEntity() instanceof ReflectorBlockEntity be)) return;

        ItemStack lamp = be.getLampStack(); // expects the BE to expose the installed lamp
        boolean hasLamp = lamp != null && !lamp.isEmpty();
        tag.putBoolean("hasLamp", hasLamp);

        if (hasLamp) {
            ResourceLocation id = BuiltInRegistries.ITEM.getKey(lamp.getItem());
            if (id != null) tag.putString("lampId", id.toString());

            int remTicks = be.getLampRemainingTicks(); // remaining ticks until break
            int remSec = (remTicks + 19) / 20; // ceil to seconds
            tag.putInt("remSec", Math.max(0, remSec));
        }
    }

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        CompoundTag data = accessor.getServerData();

        boolean hasLamp = data.getBoolean("hasLamp");
        if (!hasLamp) {
            tooltip.add(Component.literal("Lamp: ").withStyle(ChatFormatting.GRAY)
                    .append(Component.literal("None").withStyle(ChatFormatting.DARK_GRAY)));
            return;
        }

        // Lamp name
        Item item = null;
        if (data.contains("lampId")) {
            ResourceLocation id = ResourceLocation.tryParse(data.getString("lampId"));
            if (id != null) item = BuiltInRegistries.ITEM.get(id);
        }
        Component lampName = item != null
                ? Component.translatable(item.getDescriptionId()).withStyle(ChatFormatting.WHITE)
                : Component.literal("Unknown").withStyle(ChatFormatting.DARK_GRAY);

        tooltip.add(Component.literal("Lamp: ").withStyle(ChatFormatting.GRAY).append(lampName));

        // Time left
        int sec = data.getInt("remSec");
        tooltip.add(Component.literal("Time Left: ").withStyle(ChatFormatting.GRAY)
                .append(Component.literal(formatMMSS(sec)).withStyle(ChatFormatting.WHITE)));
    }

    private static String formatMMSS(int totalSeconds) {
        int m = Math.max(0, totalSeconds) / 60;
        int s = Math.max(0, totalSeconds) % 60;
        return String.format("%02d:%02d", m, s);
    }
}
