package net.micaxs.smokeleaf.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.micaxs.smokeleaf.block.entity.BaseWeedCropBlockEntity;
import net.micaxs.smokeleaf.component.ModDataComponentTypes;
import net.micaxs.smokeleaf.strain.StrainData;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.List;

public class ApplyBudStats extends LootItemConditionalFunction {


    protected ApplyBudStats(LootItemCondition[] conditions) {
        super(conditions);
    }

    public static LootItemConditionalFunction.Builder<?> apply() {
        return simpleBuilder(ApplyBudStats::new);
    }

    @Override
    public LootItemFunctionType getType() {
        return ModLootItemFunctions.APPLY_BUD_STATS.get();
    }

    @Override
    protected ItemStack run(ItemStack stack, LootContext ctx) {
        BlockEntity be = ctx.getParamOrNull(LootContextParams.BLOCK_ENTITY);
        if (be instanceof BaseWeedCropBlockEntity crop) {
            int buds = Mth.clamp(crop.getBudCount(), 1, 3);
            stack.setCount(buds);
            StrainData existing = ModDataComponentTypes.STRAIN_DATA.get(stack);
            if (existing != null) {
                // Patch actual grown THC/CBD into STRAIN_DATA, preserving all other fields
                ModDataComponentTypes.STRAIN_DATA.set(stack, new StrainData(
                        existing.colorArgb(), existing.leafColor(), crop.getThc(), crop.getCbd(),
                        existing.nitrogen(), existing.phosphorus(), existing.potassium(),
                        existing.effects(), existing.amplifier(), existing.durationTicks(),
                    existing.identified(), existing.displayName(),
                    existing.typeColors(),
                    "", ""
            ));
            } else {
                ModDataComponentTypes.THC.set(stack, crop.getThc());
                ModDataComponentTypes.CBD.set(stack, crop.getCbd());
            }
        }
        return stack;
    }

    public static class Serializer extends LootItemConditionalFunction.Serializer<ApplyBudStats> {
        @Override
        public ApplyBudStats deserialize(JsonObject json, JsonDeserializationContext context, LootItemCondition[] conditions) {
            return new ApplyBudStats(conditions);
        }
    }
}
