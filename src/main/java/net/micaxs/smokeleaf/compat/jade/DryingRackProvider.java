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
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec2;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.Element;
import snownee.jade.api.ui.JadeUI;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.RegistryOps;

import java.util.LinkedHashMap;
import java.util.Map;

public enum DryingRackProvider implements IBlockComponentProvider {
    INSTANCE;

    /**
     * Server half of this provider. Since 1.21.6 Jade requires the server data provider and the
     * client tooltip provider to be separate objects; this one just forwards to the shared logic.
     */
    public enum Data implements IServerDataProvider<BlockAccessor> {
        INSTANCE;

        @Override
        public Identifier getUid() {
            return DryingRackProvider.INSTANCE.getUid();
        }

        @Override
        public void appendServerData(CompoundTag tag, BlockAccessor accessor) {
            DryingRackProvider.INSTANCE.appendServerData(tag, accessor);
        }
    }

    private static final Identifier UID =
            Identifier.fromNamespaceAndPath(SmokeleafIndustries.MODID, "drying_rack");

    @Override
    public Identifier getUid() {
        return UID;
    }

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
            slot.putString("displayName", toJson(stack.getHoverName(), registries));

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
        net.minecraft.core.HolderLookup.Provider registries = accessor.getLevel().registryAccess();

        LinkedHashMap<String, GroupEntry> groups = new LinkedHashMap<>();

        for (int i = 0; i < DryingRackBlockEntity.SLOT_COUNT; i++) {
            String key = "S" + i;
            if (!accessor.getServerData().contains(key)) continue;

            CompoundTag s = accessor.getServerData().getCompoundOrEmpty(key);

            Identifier id = Identifier.tryParse(s.getStringOr("id", ""));
            if (id == null) continue;

            // Reconstruct a tinted icon using only the color ints — lightweight, no full stack parse.
            ItemStack iconStack = new ItemStack(BuiltInRegistries.ITEM.getValue(id));
            if (s.contains("colorArgb")) {
                int colorArgb = s.getIntOr("colorArgb", 0);
                int leafColor = s.getIntOr("leafColor", 0xFF4A7A2E);
                // Build a minimal StrainData carrying only the tint colors for the icon renderer.
                iconStack.set(ModDataComponentTypes.STRAIN_DATA.get(), new StrainData(
                        colorArgb, leafColor,
                        0, 0, 0, 0, 0,
                        java.util.List.of(), 0, 0,
                        false, "",
                        StrainData.TypeColors.NONE, "", ""));
            }
            if (s.getBooleanOr("dry", false)) {
                iconStack.set(ModDataComponentTypes.DRY, true);
            }

            boolean isBud = s.getBooleanOr("bud", false);
            boolean isDryBud = s.getBooleanOr("dry", false);
            boolean active = s.getBooleanOr("active", false);
            int seconds = s.getIntOr("sec", 0);

            String displayNameJson = s.getStringOr("displayName", "");
            Component name;
            if (!displayNameJson.isBlank()) {
                Component parsed = fromJson(displayNameJson, registries);
                name = (parsed != null ? parsed : iconStack.getHoverName()).copy().withStyle(ChatFormatting.WHITE);
            } else {
                name = iconStack.getHoverName().copy().withStyle(ChatFormatting.WHITE);
            }

            // Group by item id + display name + dry state
            String groupKey = s.getStringOr("id", "") + "|" + displayNameJson + "|" + isDryBud;
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

            Element icon = JadeUI.item(g.icon(), 0.5f).size(10, 10).offset(-2, -1);

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
            tooltip.append(JadeUI.text(line));
        }
    }

    private static String toJson(Component component, net.minecraft.core.HolderLookup.Provider registries) {
        return ComponentSerialization.CODEC.encodeStart(RegistryOps.create(JsonOps.INSTANCE, registries), component)
                .result().map(JsonElement::toString).orElse("");
    }

    @org.jetbrains.annotations.Nullable
    private static Component fromJson(String json, net.minecraft.core.HolderLookup.Provider registries) {
        try {
            return ComponentSerialization.CODEC.parse(RegistryOps.create(JsonOps.INSTANCE, registries), JsonParser.parseString(json))
                    .result().orElse(null);
        } catch (RuntimeException e) {
            return null;
        }
    }

    private static String formatMMSS(int totalSeconds) {
        int m = Math.max(0, totalSeconds) / 60;
        int s = Math.max(0, totalSeconds) % 60;
        return m + ":" + (s < 10 ? "0" + s : String.valueOf(s));
    }
}
