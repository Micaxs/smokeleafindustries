package net.micaxs.smokeleaf.compat.jade;

import net.micaxs.smokeleaf.SmokeleafIndustries;
import net.micaxs.smokeleaf.block.entity.DryingRackBlockEntity;
import net.micaxs.smokeleaf.component.ModDataComponentTypes;
import net.micaxs.smokeleaf.item.custom.BaseBudItem;
import net.micaxs.smokeleaf.strain.StrainData;
import net.micaxs.smokeleaf.strain.StrainUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec2;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.IElement;
import snownee.jade.api.ui.IElementHelper;

import java.util.LinkedHashMap;
import java.util.Map;

public enum DryingRackProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {
    INSTANCE;

    private static final ResourceLocation UID =
            ResourceLocation.fromNamespaceAndPath(SmokeleafIndustries.MODID, "drying_rack");

    @Override
    public ResourceLocation getUid() {
        return UID;
    }

    @Override
    public void appendServerData(CompoundTag tag, BlockAccessor accessor) {
        if (!(accessor.getBlockEntity() instanceof DryingRackBlockEntity rack)) return;

        net.minecraft.core.HolderLookup.Provider registries = accessor.getLevel().registryAccess();

        for (int i = 0; i < DryingRackBlockEntity.SLOT_COUNT; i++) {
            ItemStack stack = rack.getItem(i);
            if (stack.isEmpty()) continue;

            boolean isBud = stack.getItem() instanceof BaseBudItem;
            boolean isDryBud = false;
            if (isBud) {
                Boolean dry = stack.get(ModDataComponentTypes.DRY);
                isDryBud = dry != null && dry;
            }

            int needed = rack.getTotalTimeForSlot(accessor.getLevel(), i);
            int prog = rack.getProgressForSlot(i);
            int remainTicks = 0;
            boolean active = false;

            if (isBud && isDryBud) {
                active = false;
            } else if (needed > 0) {
                remainTicks = Math.max(0, needed - prog);
                active = remainTicks > 0;
            }

            int remainSeconds = (int) Math.ceil(remainTicks / 20.0);

            // Keep payload small (Jade has size limits) — send only what's needed for icon tint + label.
            CompoundTag slot = new CompoundTag();
            slot.putString("id", BuiltInRegistries.ITEM.getKey(stack.getItem()).toString());
            slot.putString("displayName", Component.Serializer.toJson(stack.getHoverName(), registries));

            // Tint colors for the icon — just two ints instead of a full ItemStack serialization.
            StrainData sd = StrainUtil.getStrain(stack);
            if (sd != StrainData.EMPTY) {
                slot.putInt("colorArgb", sd.colorArgb());
                slot.putInt("leafColor", sd.leafColor());
            }

            slot.putBoolean("bud", isBud);
            slot.putBoolean("dry", isDryBud);
            slot.putBoolean("active", active);
            slot.putInt("sec", remainSeconds);

            tag.put("S" + i, slot);
        }
    }

    private record GroupEntry(ItemStack icon, Component name, boolean isBud, boolean isDry,
                              boolean active, int maxSec, int count) {}

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        IElementHelper elements = IElementHelper.get();
        net.minecraft.core.HolderLookup.Provider registries = accessor.getLevel().registryAccess();

        LinkedHashMap<String, GroupEntry> groups = new LinkedHashMap<>();

        for (int i = 0; i < DryingRackBlockEntity.SLOT_COUNT; i++) {
            String key = "S" + i;
            if (!accessor.getServerData().contains(key)) continue;

            CompoundTag s = accessor.getServerData().getCompound(key);

            ResourceLocation id = ResourceLocation.tryParse(s.getString("id"));
            if (id == null) continue;

            // Reconstruct a tinted icon using only the color ints — lightweight, no full stack parse.
            ItemStack iconStack = new ItemStack(BuiltInRegistries.ITEM.get(id));
            if (s.contains("colorArgb")) {
                int colorArgb = s.getInt("colorArgb");
                int leafColor = s.contains("leafColor") ? s.getInt("leafColor") : 0xFF4A7A2E;
                // Build a minimal StrainData carrying only the tint colors for the icon renderer.
                iconStack.set(ModDataComponentTypes.STRAIN_DATA.get(), new StrainData(
                        colorArgb, leafColor,
                        0, 0, 0, 0, 0,
                        java.util.List.of(), 0, 0,
                        false, "",
                        StrainData.TypeColors.NONE, "", ""));
            }
            if (s.getBoolean("dry")) {
                iconStack.set(ModDataComponentTypes.DRY, true);
            }

            boolean isBud = s.getBoolean("bud");
            boolean isDryBud = s.getBoolean("dry");
            boolean active = s.getBoolean("active");
            int seconds = s.getInt("sec");

            String displayNameJson = s.getString("displayName");
            Component name;
            if (!displayNameJson.isBlank()) {
                Component parsed = Component.Serializer.fromJson(displayNameJson, registries);
                name = (parsed != null ? parsed : iconStack.getHoverName()).copy().withStyle(ChatFormatting.WHITE);
            } else {
                name = iconStack.getHoverName().copy().withStyle(ChatFormatting.WHITE);
            }

            // Group by item id + display name + dry state
            String groupKey = s.getString("id") + "|" + displayNameJson + "|" + isDryBud;
            GroupEntry existing = groups.get(groupKey);
            if (existing == null) {
                groups.put(groupKey, new GroupEntry(iconStack, name, isBud, isDryBud, active, seconds, 1));
            } else {
                groups.put(groupKey, new GroupEntry(
                        existing.icon(), existing.name(),
                        existing.isBud(), existing.isDry(),
                        existing.active() || active,
                        Math.max(existing.maxSec(), seconds),
                        existing.count() + 1));
            }
        }

        for (Map.Entry<String, GroupEntry> entry : groups.entrySet()) {
            GroupEntry g = entry.getValue();

            IElement icon = elements.item(g.icon(), 0.5f).size(new Vec2(10, 10)).translate(new Vec2(-2, -1));

            Component nameWithCount = g.count() > 1
                    ? Component.empty().append(g.name())
                            .append(Component.literal(" x" + g.count()).withStyle(ChatFormatting.GRAY))
                    : g.name();

            Component line;
            if (g.isBud() && g.isDry()) {
                line = Component.empty()
                        .append(nameWithCount)
                        .append(Component.literal(" (").withStyle(ChatFormatting.DARK_GRAY))
                        .append(Component.literal("Dry").withStyle(ChatFormatting.GREEN))
                        .append(Component.literal(")").withStyle(ChatFormatting.DARK_GRAY));
            } else if (g.active()) {
                String mmss = formatMMSS(g.maxSec());
                line = Component.empty()
                        .append(nameWithCount)
                        .append(Component.literal(" (").withStyle(ChatFormatting.DARK_GRAY))
                        .append(Component.literal("Time Left: " + mmss).withStyle(ChatFormatting.GRAY))
                        .append(Component.literal(")").withStyle(ChatFormatting.DARK_GRAY));
            } else {
                line = nameWithCount;
            }

            tooltip.add(icon);
            tooltip.append(elements.text(line));
        }
    }

    private static String formatMMSS(int totalSeconds) {
        int m = Math.max(0, totalSeconds) / 60;
        int s = Math.max(0, totalSeconds) % 60;
        return m + ":" + (s < 10 ? "0" + s : String.valueOf(s));
    }
}
