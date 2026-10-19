package net.micaxs.smokeleaf.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.micaxs.smokeleaf.component.ModDataComponentTypes;
import net.micaxs.smokeleaf.strain.StrainRegistry;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.List;

/**
 * Loot function that stamps one of the preset strains (looked up in {@link StrainRegistry} at the
 * time the loot is rolled, so datapack overrides apply) onto the item. Used by the Stoner/Dealer
 * villager trades, which are data-driven since 26.1.
 */
public class SetPresetStrain extends LootItemConditionalFunction {

    public static final MapCodec<SetPresetStrain> CODEC = RecordCodecBuilder.mapCodec(instance ->
            LootItemConditionalFunction.commonFields(instance)
                    .and(Codec.STRING.fieldOf("strain").forGetter(f -> f.strainId))
                    .apply(instance, SetPresetStrain::new)
    );

    private final String strainId;

    protected SetPresetStrain(List<LootItemCondition> conditions, String strainId) {
        super(conditions);
        this.strainId = strainId;
    }

    public static LootItemConditionalFunction.Builder<?> strain(String strainId) {
        return simpleBuilder(conditions -> new SetPresetStrain(conditions, strainId));
    }

    @Override
    public MapCodec<SetPresetStrain> codec() {
        return CODEC;
    }

    @Override
    protected ItemStack run(ItemStack stack, LootContext ctx) {
        StrainRegistry.get(strainId).ifPresent(data -> {
            stack.set(ModDataComponentTypes.STRAIN_DATA.get(), data);
            stack.set(ModDataComponentTypes.STRAIN_ID.get(), strainId);
        });
        return stack;
    }
}
