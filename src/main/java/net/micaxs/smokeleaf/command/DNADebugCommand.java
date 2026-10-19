package net.micaxs.smokeleaf.command;

import com.mojang.brigadier.CommandDispatcher;
import net.micaxs.smokeleaf.item.custom.DNAStrandItem;
import net.micaxs.smokeleaf.component.DNAContents;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public final class DNADebugCommand {

    private DNADebugCommand() {}

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("dnadebug")
                        .executes(ctx -> {
                            var player = ctx.getSource().getPlayer();
                            if (player == null) return 0;

                            ItemStack held = player.getMainHandItem();
                            if (!(held.getItem() instanceof DNAStrandItem)) {
                                ctx.getSource().sendSuccess(() -> Component.literal("Hold a DNA strand."), false);
                                return 0;
                            }

                            DNAContents contents = DNAStrandItem.getContents(held);
                            StringBuilder sb = new StringBuilder("DNA Slots: ");
                            for (int i = 0; i < 3; i++) {
                                ItemStack slot = contents.get(i);
                                sb.append(i).append("=")
                                        .append(slot.isEmpty() ? "empty" : slot.getItem().toString())
                                        .append(i < 2 ? " " : "");
                            }
                            ctx.getSource().sendSuccess(() -> Component.literal(sb.toString()), false);
                            return 1;
                        })
        );
    }
}