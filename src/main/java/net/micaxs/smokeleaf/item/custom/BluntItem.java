package net.micaxs.smokeleaf.item.custom;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.micaxs.smokeleaf.component.ModDataComponentTypes;
import net.micaxs.smokeleaf.effect.ModEffects;
import net.minecraft.ChatFormatting;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BluntItem extends Item {

    public static final int BASE_USE_TICKS = 40;

    public BluntItem(Properties props) {
        super(props);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return BASE_USE_TICKS;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int remainingUseDuration) {
        if (!(entity instanceof Player player)) return;

        int elapsed = BASE_USE_TICKS - remainingUseDuration;
        if (!level.isClientSide) {
            if (elapsed == 1) {
                // play sound if registered
                // level.playSound(null, player.getX(), player.getY(), player.getZ(), ModSounds.SMOKING.get(), SoundSource.PLAYERS, 0.7f, 1.0f);
            }
            return;
        }

        if (remainingUseDuration % 4 == 0) {
            RandomSource r = level.getRandom();
            double dx = entity.getRandomX(0.3);
            double dy = entity.getY() + entity.getBbHeight() * 0.7;
            double dz = entity.getRandomZ(0.3);
            level.addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, dx, dy, dz,
                    (r.nextDouble() - 0.5) * 0.01, 0.02, (r.nextDouble() - 0.5) * 0.01);
        }
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (!level.isClientSide && entity instanceof Player player) {
            applyStoredEffects(stack, player);
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
        }
        return stack;
    }

    private void applyStoredEffects(ItemStack stack, Player player) {
        JsonArray arr = stack.get(ModDataComponentTypes.ACTIVE_INGREDIENTS.get());
        if (arr == null) return;

        HolderLookup.RegistryLookup<MobEffect> effectLookup =
                player.level().registryAccess().lookupOrThrow(Registries.MOB_EFFECT);

        int maxDuration = 0;
        for (int i = 0; i < arr.size(); i++) {
            JsonObject obj = arr.get(i).getAsJsonObject();
            ResourceLocation rl = ResourceLocation.tryParse(obj.get("id").getAsString());
            if (rl == null) continue;
            if (rl.equals(BuiltInRegistries.MOB_EFFECT.getKey(MobEffects.CONFUSION.value()))) continue;

            int addDuration = obj.get("duration").getAsInt();
            int addAmplifier = obj.get("amp").getAsInt();
            maxDuration = Math.max(maxDuration, addDuration);

            ResourceKey<MobEffect> key = ResourceKey.create(Registries.MOB_EFFECT, rl);
            effectLookup.get(key).ifPresent(effectHolder -> {
                MobEffectInstance existing = player.getEffect(effectHolder);

                if (existing != null) {
                    int newDuration = existing.getDuration() + addDuration;
                    int newAmplifier = Math.max(existing.getAmplifier(), addAmplifier);
                    player.removeEffect(effectHolder);
                    player.addEffect(new MobEffectInstance(effectHolder, newDuration, newAmplifier));
                } else {
                    player.addEffect(new MobEffectInstance(effectHolder, addDuration, addAmplifier));
                }
            });
        }

        // Grant STONED at a quarter of the longest stored weed effect's duration — blunts double
        // their ingredient effect durations (see storeWeeds), so using the full duration here made
        // 3 stacked hits balloon to ~3 minutes of STONED. The trip shader tier is picked from the
        // strongest THC among the weeds rolled into this blunt; high CBD (27-30) shortens the
        // duration further. The trip only actually shows once the same blunt profile has been
        // smoked 3 times in a row (see TripStreakTracker) — it still grants STONED either way.
        if (maxDuration > 0) {
            MobEffectInstance existing = player.getEffect(ModEffects.STONED);
            Integer tripThc = stack.get(ModDataComponentTypes.TRIP_THC.get());
            Integer tripCbd = stack.get(ModDataComponentTypes.TRIP_CBD.get());
            int thcVal = tripThc != null ? tripThc : 0;
            int cbdVal = tripCbd != null ? tripCbd : 0;
            int tier = net.micaxs.smokeleaf.effect.TripTier.forThc(thcVal).ordinal();
            float cbdMult = net.micaxs.smokeleaf.effect.TripTier.cbdDurationMultiplier(cbdVal);
            int adjustedDuration = Math.max(1, Math.round(maxDuration * 0.25f * cbdMult));
            int stonedDuration = existing != null ? existing.getDuration() + adjustedDuration : adjustedDuration;
            String streakKey = net.micaxs.smokeleaf.effect.TripStreakTracker.keyForTripStats(thcVal, cbdVal);
            boolean confirmed = net.micaxs.smokeleaf.effect.TripStreakTracker.registerUseAndGetStreak(player, streakKey)
                    >= net.micaxs.smokeleaf.effect.TripStreakTracker.REQUIRED_STREAK;
            player.addEffect(new MobEffectInstance(ModEffects.STONED, stonedDuration, tier, false, confirmed));
        }
    }

    public static void storeWeeds(ItemStack joint, List<ItemStack> weeds) {
        JsonArray effectArray = new JsonArray();
        Map<String, JsonObject> merged = new LinkedHashMap<>();
        ItemStack firstWeed = null;
        java.util.Set<Object> strainKeys = new java.util.LinkedHashSet<>();
        int maxThc = 0, maxCbd = 0;

        for (ItemStack w : weeds) {
            if (!(w.getItem() instanceof BaseWeedItem weed)) continue;
            if (firstWeed == null) firstWeed = w;
            weed.initializeStack(w);

            // Track which strain(s) went into this craft — by STRAIN_ID when present (the
            // authoritative lineage identifier used everywhere else in the mod), else by full
            // StrainData equality, else by base item. Naming below must use THIS, not the number
            // of distinct effects: a single strain can carry several effects on its own, which
            // previously caused a genuinely single-strain craft to be misnamed "Mixed".
            {
                net.micaxs.smokeleaf.strain.StrainData sd = net.micaxs.smokeleaf.strain.StrainUtil.getStrain(w);
                String sid = w.get(ModDataComponentTypes.STRAIN_ID.get());
                Object key = (sid != null && !sid.isBlank()) ? sid
                        : (sd != net.micaxs.smokeleaf.strain.StrainData.EMPTY ? sd : w.getItem());
                strainKeys.add(key);
                if (sd != net.micaxs.smokeleaf.strain.StrainData.EMPTY) {
                    maxThc = Math.max(maxThc, sd.thc());
                    maxCbd = Math.max(maxCbd, sd.cbd());
                }
            }

            int baseDuration = weed.getDuration(w);
            int doubledDuration = baseDuration * 2;
            int amp = weed.getEffectAmplifier();

            // Collect all effects from strain data first; fall back to single base effect
            net.micaxs.smokeleaf.strain.StrainData d = net.micaxs.smokeleaf.strain.StrainUtil.getStrain(w);
            List<net.minecraft.resources.ResourceLocation> effectIds =
                    (d != net.micaxs.smokeleaf.strain.StrainData.EMPTY && !d.effects().isEmpty())
                            ? d.effects()
                            : java.util.Collections.emptyList();

            if (!effectIds.isEmpty()) {
                for (net.minecraft.resources.ResourceLocation rl : effectIds) {
                    if (rl == null) continue;
                    if (rl.equals(BuiltInRegistries.MOB_EFFECT.getKey(MobEffects.CONFUSION.value()))) continue;
                    String id = rl.toString();
                    if (merged.containsKey(id)) {
                        JsonObject existing = merged.get(id);
                        existing.addProperty("duration", existing.get("duration").getAsInt() + doubledDuration);
                    } else {
                        JsonObject obj = new JsonObject();
                        obj.addProperty("id", id);
                        obj.addProperty("duration", doubledDuration);
                        obj.addProperty("amp", amp);
                        merged.put(id, obj);
                    }
                }
            } else {
                MobEffect eff = weed.getEffect(w);
                if (eff == null) continue;
                String id = BuiltInRegistries.MOB_EFFECT.getKey(eff).toString();
                if (merged.containsKey(id)) {
                    JsonObject existing = merged.get(id);
                    existing.addProperty("duration", existing.get("duration").getAsInt() + doubledDuration);
                } else {
                    JsonObject obj = new JsonObject();
                    obj.addProperty("id", id);
                    obj.addProperty("duration", doubledDuration);
                    obj.addProperty("amp", amp);
                    merged.put(id, obj);
                }
            }
        }

        merged.values().forEach(effectArray::add);
        joint.set(ModDataComponentTypes.ACTIVE_INGREDIENTS.get(), effectArray);
        joint.set(ModDataComponentTypes.TRIP_THC.get(), maxThc);
        joint.set(ModDataComponentTypes.TRIP_CBD.get(), maxCbd);

        if (merged.isEmpty()) {
            joint.remove(DataComponents.CUSTOM_NAME);
        } else if (strainKeys.size() > 1) {
            joint.set(DataComponents.CUSTOM_NAME,
                    Component.translatable("item.smokeleafindustries.blunt.mixed"));
        } else if (firstWeed != null) {
            net.micaxs.smokeleaf.strain.StrainData firstStrain = net.micaxs.smokeleaf.strain.StrainUtil.getStrain(firstWeed);
            Component weedName = (firstStrain != net.micaxs.smokeleaf.strain.StrainData.EMPTY
                    && firstStrain.displayName() != null && !firstStrain.displayName().isBlank())
                    ? Component.literal(firstStrain.displayName())
                    : firstWeed.getHoverName();
            joint.set(DataComponents.CUSTOM_NAME,
                    Component.translatable("item.smokeleafindustries.blunt.format", weedName));
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context,
                                List<Component> tooltip, TooltipFlag flag) {
        JsonArray arr = stack.get(ModDataComponentTypes.ACTIVE_INGREDIENTS.get());
        if (arr == null || arr.isEmpty()) {
            tooltip.add(Component.translatable("tooltip.smokeleafindustries.blunt.empty")
                    .withStyle(ChatFormatting.GRAY));
            return;
        }
        tooltip.add(Component.literal("Blunt Effects:").withStyle(ChatFormatting.GOLD));
        for (int i = 0; i < arr.size(); i++) {
            JsonObject obj = arr.get(i).getAsJsonObject();
            ResourceLocation rl = ResourceLocation.tryParse(obj.get("id").getAsString());
            int dur = obj.get("duration").getAsInt();
            int amp = obj.get("amp").getAsInt();

            Component nameComp;
            if (rl != null) {
                MobEffect eff = BuiltInRegistries.MOB_EFFECT.get(rl);
                nameComp = (eff != null)
                        ? Component.translatable(eff.getDescriptionId())
                        : Component.literal(rl.getPath());
            } else {
                nameComp = Component.literal("unknown");
            }

            tooltip.add(Component.literal(" - ")
                    .append(nameComp.copy().withStyle(ChatFormatting.DARK_GREEN))
                    .append(Component.literal(" " + (amp + 1) + " (" + (dur / 20) + "s)")
                            .withStyle(ChatFormatting.DARK_GREEN)));
        }
    }
}
