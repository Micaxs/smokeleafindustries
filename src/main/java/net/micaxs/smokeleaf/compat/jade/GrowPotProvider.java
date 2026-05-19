package net.micaxs.smokeleaf.compat.jade;

import net.micaxs.smokeleaf.SmokeleafIndustries;
import net.micaxs.smokeleaf.block.entity.GrowPotBlockEntity;
import net.micaxs.smokeleaf.strain.StrainData;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.network.chat.Component;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public enum GrowPotProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {
    INSTANCE;

    private static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(SmokeleafIndustries.MODID, "grow_pot_jade");

    @Override
    public ResourceLocation getUid() {
        return UID;
    }

    @Override
    public void appendServerData(CompoundTag tag, BlockAccessor accessor) {
        if (!(accessor.getBlockEntity() instanceof GrowPotBlockEntity pot)) return;

        // Current NPK
        tag.putInt("n", pot.getNitrogen());
        tag.putInt("p", pot.getPhosphorus());
        tag.putInt("k", pot.getPotassium());

        // Target/optimal NPK for color logic
        var target = pot.getOptimalNutrientsLevels();
        tag.putInt("tn", target.n);
        tag.putInt("tp", target.p);
        tag.putInt("tk", target.k);

        // Soil RL if present
        BlockState soil = pot.getSoilState();
        if (soil != null) {
            ResourceLocation soilId = BuiltInRegistries.BLOCK.getKey(soil.getBlock());
            if (soilId != null) tag.putString("soil", soilId.toString());
        }

        // Crop RL if present
        BlockState cropState = pot.getBottomCropStateForRender();
        if (cropState != null) {
            ResourceLocation cropId = BuiltInRegistries.BLOCK.getKey(cropState.getBlock());
            if (cropId != null) tag.putString("crop", cropId.toString());
        }

        // Custom strain name and base stats (so tooltip can show strain name + THC/CBD)
        if (pot.hasCustomStrain()) {
            StrainData strain = pot.getCustomStrain();
            if (strain != null && strain != StrainData.EMPTY) {
                String name = strain.displayName();
                if (name != null && !name.isBlank()) {
                    tag.putString("strain_name", name);
                }
                tag.putInt("thc", strain.thc());
                tag.putInt("cbd", strain.cbd());
            }
        }

        // Growth data (age/maxAge)
        int age = pot.getCropAge();
        int maxAge = pot.getCropMaxAge();
        tag.putInt("age", age);
        tag.putInt("maxAge", maxAge);
    }

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        CompoundTag data = accessor.getServerData();

        // Soil line
        if (data.contains("soil")) {
            ResourceLocation soilId = ResourceLocation.tryParse(data.getString("soil"));
            Block soilBlock = soilId != null ? BuiltInRegistries.BLOCK.get(soilId) : null;
            Component soilName = soilBlock != null
                    ? Component.translatable(soilBlock.getDescriptionId())
                    : Component.literal("Unknown");
            tooltip.add(Component.literal("Soil: ").append(soilName));
        } else {
            tooltip.add(Component.literal("Soil: Empty"));
        }

        // Crop line
        if (data.contains("crop")) {
            Component cropName;
            if (data.contains("strain_name")) {
                cropName = Component.literal(data.getString("strain_name")).withStyle(ChatFormatting.LIGHT_PURPLE);
            } else {
                ResourceLocation cropId = ResourceLocation.tryParse(data.getString("crop"));
                Block cropBlock = cropId != null ? BuiltInRegistries.BLOCK.get(cropId) : null;
                cropName = cropBlock != null
                        ? Component.translatable(cropBlock.getDescriptionId())
                        : Component.literal("Unknown");
            }
            tooltip.add(Component.literal("Crop: ").append(cropName));

            if (data.contains("thc") && data.contains("cbd")) {
                tooltip.add(Component.literal("THC: ")
                        .withStyle(ChatFormatting.GRAY)
                        .append(Component.literal(data.getInt("thc") + "%").withStyle(ChatFormatting.GREEN))
                        .append(Component.literal("  CBD: ").withStyle(ChatFormatting.GRAY))
                        .append(Component.literal(data.getInt("cbd") + "%").withStyle(ChatFormatting.AQUA)));
            }
        } else {
            tooltip.add(Component.literal("Crop: None"));
        }

        // Growth line (like Jade crops)
        if (data.contains("age") && data.contains("maxAge")) {
            int age = data.getInt("age");
            int maxAge = data.getInt("maxAge");
            if (maxAge > 0) {
                if (age >= maxAge) {
                    tooltip.add(
                            Component.literal("Growth: ").withStyle(ChatFormatting.GRAY)
                                    .append(Component.literal("Mature").withStyle(ChatFormatting.GREEN)));
                } else {
                    if (age < maxAge / 2) {
                        int pct = Math.max(0, Math.min(100, (int) Math.round(age * 100.0 / maxAge)));
                        tooltip.add(Component.literal("Growth: ").withStyle(ChatFormatting.GRAY)
                                .append(Component.literal(pct + "%").withStyle(ChatFormatting.WHITE)));
                    } else {
                        int pct = Math.max(0, Math.min(100, (int) Math.round(age * 100.0 / maxAge)));
                        tooltip.add(Component.literal("Growth: ").withStyle(ChatFormatting.GRAY)
                                .append(Component.literal(pct + "%").withStyle(ChatFormatting.YELLOW)));
                    }
                }
            } else {
                tooltip.add(Component.literal("Growth: None").withStyle(ChatFormatting.DARK_GRAY));
            }
        }

        // Separate N, P, K lines with Analyzer color logic
        if (hasAll(data, "n", "p", "k", "tn", "tp", "tk")) {
            int n = data.getInt("n"), p = data.getInt("p"), k = data.getInt("k");
            int tn = data.getInt("tn"), tp = data.getInt("tp"), tk = data.getInt("tk");

            if (tn == 0 && tp == 0 && tk == 0) return;

            tooltip.add(Component.literal("Nitrogen (N): ")
                    .append(Component.literal(String.valueOf(n)).withStyle(colorForDiff(Math.abs(n - tn))))
                    .append(Component.literal("/").withStyle(ChatFormatting.GRAY))
                    .append(Component.literal(String.valueOf(tn)).withStyle(ChatFormatting.GREEN)));

            tooltip.add(Component.literal("Phosphorus (P): ")
                    .append(Component.literal(String.valueOf(p)).withStyle(colorForDiff(Math.abs(p - tp))))
                    .append(Component.literal("/").withStyle(ChatFormatting.GRAY))
                    .append(Component.literal(String.valueOf(tp)).withStyle(ChatFormatting.GREEN)));

            tooltip.add(Component.literal("Potassium (K): ")
                    .append(Component.literal(String.valueOf(k)).withStyle(colorForDiff(Math.abs(k - tk))))
                    .append(Component.literal("/").withStyle(ChatFormatting.GRAY))
                    .append(Component.literal(String.valueOf(tk)).withStyle(ChatFormatting.GREEN)));
        }
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