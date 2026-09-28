package net.micaxs.smokeleaf.component;

import com.mojang.serialization.Codec;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * A typed value stored in an ItemStack / FluidStack NBT tag. Stand-in for the 1.21 data component
 * API: {@code stack.get(TYPE)} becomes {@code TYPE.get(stack)}, {@code stack.set(TYPE, v)} becomes
 * {@code TYPE.set(stack, v)}. Setting {@code null} removes the value, and empty tags are cleared so
 * stacks without data keep stacking with fresh ones.
 */
public final class DataKey<T> {

    private final String key;
    private final Codec<T> codec;

    public DataKey(String key, Codec<T> codec) {
        this.key = key;
        this.codec = codec;
    }

    public String key() {
        return key;
    }

    public Codec<T> codec() {
        return codec;
    }

    // ---- ItemStack ----

    @Nullable
    public T get(ItemStack stack) {
        if (stack.isEmpty()) return null;
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(key)) {
            T value = decode(tag.get(key));
            if (value != null) return value;
        }
        return itemDefault(stack);
    }

    public T getOrDefault(ItemStack stack, T fallback) {
        T value = get(stack);
        return value != null ? value : fallback;
    }

    public boolean has(ItemStack stack) {
        if (stack.isEmpty()) return false;
        CompoundTag tag = stack.getTag();
        return (tag != null && tag.contains(key)) || itemDefault(stack) != null;
    }

    public void set(ItemStack stack, @Nullable T value) {
        if (stack.isEmpty()) return;
        if (value == null) {
            remove(stack);
            return;
        }
        Tag encoded = encode(value);
        if (encoded != null) stack.getOrCreateTag().put(key, encoded);
    }

    public void remove(ItemStack stack) {
        if (stack.hasTag()) stack.removeTagKey(key);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    private T itemDefault(ItemStack stack) {
        if (stack.getItem() instanceof DefaultDataProvider provider) {
            return (T) provider.getDefaultData(this);
        }
        return null;
    }

    // ---- FluidStack ----

    @Nullable
    public T get(FluidStack stack) {
        if (stack.isEmpty()) return null;
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(key)) return decode(tag.get(key));
        return null;
    }

    public T getOrDefault(FluidStack stack, T fallback) {
        T value = get(stack);
        return value != null ? value : fallback;
    }

    public boolean has(FluidStack stack) {
        CompoundTag tag = stack.getTag();
        return tag != null && tag.contains(key);
    }

    public void set(FluidStack stack, @Nullable T value) {
        if (value == null) {
            remove(stack);
            return;
        }
        Tag encoded = encode(value);
        if (encoded != null) stack.getOrCreateTag().put(key, encoded);
    }

    public void remove(FluidStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null) return;
        tag.remove(key);
        if (tag.isEmpty()) stack.setTag(null);
    }

    // ---- codec plumbing ----

    @Nullable
    private T decode(@Nullable Tag tag) {
        if (tag == null) return null;
        return codec.parse(NbtOps.INSTANCE, tag).result().orElse(null);
    }

    @Nullable
    private Tag encode(T value) {
        return codec.encodeStart(NbtOps.INSTANCE, value).result().orElse(null);
    }

    @Override
    public String toString() {
        return "DataKey[" + key + "]";
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof DataKey<?> other && Objects.equals(key, other.key);
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }
}
