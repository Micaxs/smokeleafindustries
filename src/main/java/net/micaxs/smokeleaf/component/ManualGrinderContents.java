package net.micaxs.smokeleaf.component;

import com.mojang.serialization.Codec;
import net.minecraft.world.item.ItemStack;

public record ManualGrinderContents(ItemStack stack) {
    public static ManualGrinderContents fromStack(ItemStack s) {
        return new ManualGrinderContents(s.copy());
    }

    public ItemStack toStack() {
        return stack.copy();
    }

    public static final Codec<ManualGrinderContents> CODEC =
            ItemStack.CODEC.xmap(ManualGrinderContents::new, ManualGrinderContents::stack);

}