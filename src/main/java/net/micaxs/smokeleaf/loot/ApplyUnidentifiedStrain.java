package net.micaxs.smokeleaf.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.micaxs.smokeleaf.block.entity.UnidentifiedWeedCropBlockEntity;
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

/**
 * Loot function that copies STRAIN_DATA from an {@link UnidentifiedWeedCropBlockEntity} onto the dropped item.
 *
 * Also adjusts weed yield based on crop nutrient match (bud count logic).
 */
public class ApplyUnidentifiedStrain extends LootItemConditionalFunction {


    protected ApplyUnidentifiedStrain(LootItemCondition[] conditions) {
        super(conditions);
    }

    public static LootItemConditionalFunction.Builder<?> applyFromCrop() {
        return simpleBuilder(ApplyUnidentifiedStrain::new);
    }

    @Override
    public LootItemFunctionType getType() {
        return ModLootItemFunctions.APPLY_UNIDENTIFIED_STRAIN.get();
    }

    @Override
    protected ItemStack run(ItemStack stack, LootContext ctx) {
        BlockEntity be = ctx.getParamOrNull(LootContextParams.BLOCK_ENTITY);
        if (be instanceof UnidentifiedWeedCropBlockEntity crop) {
            StrainData d = crop.getStrain();
            if (d != null && d != StrainData.EMPTY) {
                ModDataComponentTypes.STRAIN_DATA.set(stack, d);
            }
            String sid = crop.getStrainId();
            if (sid != null && !sid.isBlank()) {
                ModDataComponentTypes.STRAIN_ID.set(stack, sid);
            }
            String creator = crop.getStrainCreator();
            if (creator != null && !creator.isBlank()) {
                ModDataComponentTypes.STRAIN_CREATOR.set(stack, creator);
            }

            // If this is the weed drop, make yield follow bud count rules.
            // (Seeds stay at 1, leaf stays at 1.)
            // Heuristic: only change count if stack count is 1 and item can stack.
            if (stack.getCount() == 1 && stack.getMaxStackSize() > 1) {
                int buds = Mth.clamp(crop.getBudCount(), 1, 3);
                stack.setCount(buds);
            }
        }
        return stack;
    }

    public static class Serializer extends LootItemConditionalFunction.Serializer<ApplyUnidentifiedStrain> {
        @Override
        public ApplyUnidentifiedStrain deserialize(JsonObject json, JsonDeserializationContext context, LootItemCondition[] conditions) {
            return new ApplyUnidentifiedStrain(conditions);
        }
    }
}
