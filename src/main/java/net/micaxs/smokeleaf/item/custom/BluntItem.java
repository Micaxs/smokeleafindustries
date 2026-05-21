package net.micaxs.smokeleaf.item.custom;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.micaxs.smokeleaf.component.ModDataComponentTypes;
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

        for (int i = 0; i < arr.size(); i++) {
            JsonObject obj = arr.get(i).getAsJsonObject();
            ResourceLocation rl = ResourceLocation.tryParse(obj.get("id").getAsString());
            if (rl == null) continue;

            int addDuration = obj.get("duration").getAsInt();
            int addAmplifier = obj.get("amp").getAsInt();

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
    }

    public static void storeWeeds(ItemStack joint, List<ItemStack> weeds) {
        JsonArray effectArray = new JsonArray();
        Map<String, JsonObject> merged = new LinkedHashMap<>();
        ItemStack firstWeed = null;

        for (ItemStack w : weeds) {
            if (!(w.getItem() instanceof BaseWeedItem weed)) continue;
            if (firstWeed == null) firstWeed = w;
            weed.initializeStack(w);

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

        int distinctEffects = merged.size();
        if (distinctEffects == 0) {
            joint.remove(DataComponents.CUSTOM_NAME);
        } else if (distinctEffects > 1) {
            joint.set(DataComponents.CUSTOM_NAME,
                    Component.translatable("item.smokeleafindustries.blunt.mixed"));
        } else { // exactly 1
            if (firstWeed != null) {
                Component weedName = firstWeed.getHoverName();
                joint.set(DataComponents.CUSTOM_NAME,
                        Component.translatable("item.smokeleafindustries.blunt.format", weedName));
            }
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
