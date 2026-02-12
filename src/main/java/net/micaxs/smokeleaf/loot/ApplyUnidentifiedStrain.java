package net.micaxs.smokeleaf.loot;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
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

    public static final MapCodec<ApplyUnidentifiedStrain> CODEC = RecordCodecBuilder.mapCodec(instance ->
            LootItemConditionalFunction.commonFields(instance).apply(instance, ApplyUnidentifiedStrain::new)
    );

    protected ApplyUnidentifiedStrain(List<LootItemCondition> conditions) {
        super(conditions);
    }

    public static LootItemConditionalFunction.Builder<?> applyFromCrop() {
        return simpleBuilder(ApplyUnidentifiedStrain::new);
    }

    @Override
    public LootItemFunctionType<ApplyUnidentifiedStrain> getType() {
        return ModLootItemFunctions.APPLY_UNIDENTIFIED_STRAIN.get();
    }

    @Override
    protected ItemStack run(ItemStack stack, LootContext ctx) {
        BlockEntity be = ctx.getParamOrNull(LootContextParams.BLOCK_ENTITY);
        if (be instanceof UnidentifiedWeedCropBlockEntity crop) {
            StrainData d = crop.getStrain();
            if (d != null && d != StrainData.EMPTY) {
                stack.set(ModDataComponentTypes.STRAIN_DATA.get(), d);
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
}
