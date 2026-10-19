package net.micaxs.smokeleaf.effect.beneficial;

import net.micaxs.smokeleaf.SmokeleafIndustries;
import net.micaxs.smokeleaf.effect.ModEffects;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.neoforged.neoforge.common.NeoForgeMod;

import java.util.UUID;

public class HighFlyerEffect extends MobEffect {
    public HighFlyerEffect(MobEffectCategory category, int color) {
        super(category, color);

        this.addAttributeModifier(
                NeoForgeMod.CREATIVE_FLIGHT,
                Identifier.fromNamespaceAndPath(SmokeleafIndustries.MODID, "high_flyer_effect"),
                1.0D,
                AttributeModifier.Operation.ADD_VALUE
        );
    }
}