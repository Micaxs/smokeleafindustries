package net.micaxs.smokeleaf.item.custom;

import net.minecraft.world.item.Item;

/**
 * A plain marker item — all of its connect/disconnect/import-export logic lives in
 * {@code event.PipeWrenchEvents}, which intercepts right-clicks on a pipe the same way Modern
 * Industrialization dispatches its wrench (a global {@code PlayerInteractEvent.RightClickBlock}
 * listener checking the held item, MIT licensed), rather than overriding {@code useOn} here.
 */
public class PipeWrenchItem extends Item {
    public PipeWrenchItem(Properties properties) {
        super(properties);
    }
}
