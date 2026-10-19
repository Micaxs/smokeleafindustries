package net.micaxs.smokeleaf.client.color;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.micaxs.smokeleaf.component.ModDataComponentTypes;
import net.micaxs.smokeleaf.strain.StrainData;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * Item model tint ({@code "type": "smokeleafindustries:strain"}) that colors a layer from the
 * stack's {@link StrainData}. {@code role} picks which of the strain's colors a layer uses — the
 * item model JSONs list one tint per layer, replacing the old per-item {@code ItemColor} handlers.
 */
public record StrainTintSource(Role role) implements ItemTintSource {
    public static final MapCodec<StrainTintSource> MAP_CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Role.CODEC.fieldOf("role").forGetter(StrainTintSource::role)
    ).apply(i, StrainTintSource::new));

    private static final int UNTINTED = 0xFFFFFFFF;

    public enum Role implements StringRepresentable {
        /** Bud leaf color; darkened when the bud is dried. */
        BUD_LEAF("bud_leaf"),
        /** Bud strain color; darkened when the bud is dried. */
        BUD_COLOR("bud_color"),
        WEED_LEAF("weed_leaf"),
        WEED_COLOR("weed_color"),
        SEEDS_LEAF("seeds_leaf"),
        SEEDS_COLOR("seeds_color"),
        EXTRACT_COLOR("extract_color"),
        LEAF_COLOR("leaf_color"),
        STRAIN_COLOR("strain_color"),
        /** Strain color blended 50% toward white. */
        STRAIN_COLOR_LIGHT("strain_color_light");

        public static final Codec<Role> CODEC = StringRepresentable.fromEnum(Role::values);
        private final String name;

        Role(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return name;
        }
    }

    @Override
    public int calculate(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity owner) {
        StrainData d = stack.get(ModDataComponentTypes.STRAIN_DATA.get());
        if (d == null) return UNTINTED;
        boolean dry = Boolean.TRUE.equals(stack.get(ModDataComponentTypes.DRY));
        // Dried buds keep the strain's own hue — just a bit darker/muted — instead of a hardcoded
        // flat grey that erased strain identity entirely.
        return switch (role) {
            case BUD_LEAF -> dry ? darken(d.leafColor(), 0.88f) : d.leafColor();
            case BUD_COLOR -> dry ? darken(d.colorArgb(), 0.8f) : d.colorArgb();
            case WEED_LEAF -> d.weedLeafColorEffective();
            case WEED_COLOR -> d.weedColorArgbEffective();
            case SEEDS_LEAF -> d.seedsLeafColorEffective();
            case SEEDS_COLOR -> d.seedsColorArgbEffective();
            case EXTRACT_COLOR -> d.extractColorArgbEffective();
            case LEAF_COLOR -> d.leafColor();
            case STRAIN_COLOR -> d.colorArgb();
            case STRAIN_COLOR_LIGHT -> lighten(d.colorArgb());
        };
    }

    @Override
    public MapCodec<StrainTintSource> type() {
        return MAP_CODEC;
    }

    /** Scales an ARGB color's RGB channels by {@code factor} (alpha untouched). */
    private static int darken(int argb, float factor) {
        int a = (argb >> 24) & 0xFF;
        int r = Math.round(((argb >> 16) & 0xFF) * factor);
        int g = Math.round(((argb >> 8) & 0xFF) * factor);
        int b = Math.round((argb & 0xFF) * factor);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    /** Blends each RGB channel 50% toward white, keeping original alpha. */
    private static int lighten(int argb) {
        int a = (argb >> 24) & 0xFF;
        int r = (argb >> 16) & 0xFF;
        int g = (argb >> 8) & 0xFF;
        int b = argb & 0xFF;
        r = r + (255 - r) / 2;
        g = g + (255 - g) / 2;
        b = b + (255 - b) / 2;
        return (a << 24) | (r << 16) | (g << 8) | b;
    }
}
