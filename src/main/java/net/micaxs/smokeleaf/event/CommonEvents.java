package net.micaxs.smokeleaf.event;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.micaxs.smokeleaf.SmokeleafIndustries;
import net.micaxs.smokeleaf.component.ManualGrinderContents;
import net.micaxs.smokeleaf.component.ModDataComponentTypes;
import net.micaxs.smokeleaf.effect.ModEffects;
import net.micaxs.smokeleaf.fluid.ModFluids;
import net.micaxs.smokeleaf.item.ModItems;
import net.micaxs.smokeleaf.item.custom.BaseWeedItem;
import net.micaxs.smokeleaf.item.custom.ManualGrinderItem;
import net.micaxs.smokeleaf.item.custom.UnidentifiedMixtureBucketItem;
import net.micaxs.smokeleaf.strain.MixedStrainSavedData;
import net.micaxs.smokeleaf.strain.StrainData;
import net.micaxs.smokeleaf.strain.StrainRegistrySavedData;
import net.micaxs.smokeleaf.strain.StrainUtil;
import net.micaxs.smokeleaf.utils.ModTags;
import net.micaxs.smokeleaf.utils.WeedDataUtil;
import net.micaxs.smokeleaf.villager.ModVillagers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.AnvilUpdateEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.event.entity.player.AnvilRepairEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import java.util.*;

@EventBusSubscriber(modid = SmokeleafIndustries.MODID)
public class CommonEvents {



    // -------- Cat Urine Collection --------
    private static final String TAG_LAST_TIME = "SmokeleafIndustriesLastUrineCollect";
    private static final long COOLDOWN_TICKS = 5L * 60L * 20L; // 5 minutes

    @SubscribeEvent
    public static void onCatInteract(PlayerInteractEvent.EntityInteract event) {
        if (!(event.getTarget() instanceof Cat cat)) return;

        Player player = event.getEntity();
        Level level = player.level();
        ItemStack held = player.getItemInHand(event.getHand());

        if (!held.is(Items.GLASS_BOTTLE)) return;
        if (level.isClientSide()) return;

        long now = level.getGameTime();
        long last = cat.getPersistentData().getLong(TAG_LAST_TIME);

        if (now - last < COOLDOWN_TICKS) {
            long remaining = COOLDOWN_TICKS - (now - last);
            long seconds = remaining / 20;
            player.displayClientMessage(
                    Component.translatable("message.smokeleafindustries.cat_urine_cooldown", seconds),
                    true
            );
            return;
        }

        cat.getPersistentData().putLong(TAG_LAST_TIME, now);

        if (!player.isCreative()) {
            held.shrink(1);
        }

        ItemStack result = new ItemStack(ModItems.CAT_URINE_BOTTLE.get());
        if (!player.addItem(result)) {
            player.drop(result, false);
        }

        level.playSound(null, cat.blockPosition(), SoundEvents.BOTTLE_FILL, SoundSource.PLAYERS, 1f, 1f);

        event.setCancellationResult(InteractionResult.SUCCESS);
        event.setCanceled(true);
    }






    // -------- HIGH_FLYER Effect (Creative Flight) --------
    @SubscribeEvent
    public static void onHighFlyerAdded(MobEffectEvent.Added event) {
        MobEffectInstance inst = event.getEffectInstance();
        if (inst == null || inst.getEffect() != ModEffects.HIGH_FLYER) return;

        if (event.getEntity() instanceof net.minecraft.server.level.ServerPlayer sp) {
            sp.getAbilities().mayfly = true;
            sp.onUpdateAbilities();
        }
    }

    @SubscribeEvent
    public static void onHighFlyerExpired(MobEffectEvent.Expired event) {
        handleHighFlyerEnd(event.getEntity(), event.getEffectInstance());
    }

    @SubscribeEvent
    public static void onHighFlyerRemoved(MobEffectEvent.Remove event) {
        handleHighFlyerEnd(event.getEntity(), event.getEffectInstance());
    }

    private static void handleHighFlyerEnd(LivingEntity entity, MobEffectInstance inst) {
        if (inst == null || inst.getEffect() != ModEffects.HIGH_FLYER) return;
        if (!(entity instanceof net.minecraft.server.level.ServerPlayer sp)) return;

        AttributeInstance flightAttr = sp.getAttribute(NeoForgeMod.CREATIVE_FLIGHT);
        double value = flightAttr != null ? flightAttr.getValue() : 0.0D;

        if (value <= 0.0D && !sp.getAbilities().instabuild) {
            sp.getAbilities().mayfly = false;
            sp.getAbilities().flying = false;
            sp.onUpdateAbilities();
        }
    }




    // -------- ManualGrinder Crafting Recipe --------
    @SubscribeEvent
    public static void onManualGrinderCraft(PlayerEvent.ItemCraftedEvent event) {
        ItemStack result = event.getCrafting();
        if (!(result.getItem() instanceof ManualGrinderItem)) {
            return;
        }
    }





    // -------- Player Effects: Chillout, Zombified, Sticky Icky --------
    private static final int CHILL_RADIUS = 16;
    private static final int VACUUM_RADIUS = 12;
    private static final int MAX_VEIN = 128;
    private static final int MAX_TREE = 1024;

    private static final int SELF_DROP_GRACE_TICKS = 50;
    private static final double SELF_DROP_NEAR_SQR = 9.0D;

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        Level lvl = player.level();
        if (lvl.isClientSide) return;
        ServerLevel level = (ServerLevel) lvl;

        // Chillout: pacify nearby zombies + particles
        if (player.hasEffect(ModEffects.CHILLOUT)) {
            AABB box = player.getBoundingBox().inflate(CHILL_RADIUS);
            for (LivingEntity le : level.getEntitiesOfClass(LivingEntity.class, box)) {
                if (le instanceof Zombie zombie) {
                    if (zombie.getTarget() != null) zombie.setTarget(null);
                    if (zombie.getLastHurtByMob() != null) zombie.setLastHurtByMob(null);
                    level.sendParticles(ParticleTypes.SMOKE, zombie.getX(), zombie.getY() + 1.0, zombie.getZ(), 2, 0.2, 0.2, 0.2, 0.01);
                }
            }
        }

        // Zombified: nearby hostiles ignore you + Burn in sunlight.
        if (player.hasEffect(ModEffects.ZOMBIFIED)) {
            AABB box = player.getBoundingBox().inflate(24);
            for (Monster mob : level.getEntitiesOfClass(Monster.class, box)) {
                if (mob.getTarget() instanceof Player) {
                    mob.setTarget(null);
                    mob.setLastHurtByMob(null);
                }
            }
        }

        // Sticky Icky: vacuum items/xp (Magnet Like)
        if (player.hasEffect(ModEffects.STICKY_ICKY)) {
            AABB box = player.getBoundingBox().inflate(VACUUM_RADIUS);

            for (ItemEntity item : level.getEntitiesOfClass(ItemEntity.class, box)) {
                if (item.isRemoved()) continue;

                int age = item.getAge();
                boolean freshNear = age < SELF_DROP_GRACE_TICKS && item.distanceToSqr(player) < SELF_DROP_NEAR_SQR;

                if (freshNear) {
                    int remaining = SELF_DROP_GRACE_TICKS - age;
                    if (remaining > 0) item.setPickUpDelay(remaining);
                    continue;
                }

                if (item.hasPickUpDelay()) continue;
                pullToward(item, player, 0.35f);
            }

            for (ExperienceOrb xp : level.getEntitiesOfClass(ExperienceOrb.class, box)) {
                if (xp.isRemoved()) continue;
                pullToward(xp, player, 0.35f);
            }
        }
    }

    private static void pullToward(net.minecraft.world.entity.Entity e, Player player, float strength) {
        double dx = player.getX() - e.getX();
        double dy = (player.getY() + player.getEyeHeight() * 0.4) - e.getY();
        double dz = player.getZ() - e.getZ();
        double d = Math.max(0.25, Math.sqrt(dx * dx + dy * dy + dz * dz));
        double s = strength / d;
        e.setDeltaMovement(e.getDeltaMovement().add(dx * s, dy * s, dz * s));
        e.hurtMarked = true;
    }




    // -------- Villager interaction hooks --------
    @SubscribeEvent
    public static void onVillagerInteract(PlayerInteractEvent.EntityInteract event) {
        Player player = event.getEntity();
        if (player.level().isClientSide) return;

        if (event.getTarget() instanceof Villager villager) {
            if (player.hasEffect(ModEffects.CHILLOUT)) {
                villager.playSound(SoundEvents.VILLAGER_NO, 1.0f, 1.0f);
            }
            if (player.hasEffect(ModEffects.LINGUISTS_HIGH)) {
                villager.addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.HERO_OF_THE_VILLAGE, 200, 0, false, false));
            }
        }
    }




    // -------- Tree Cutting & Veinmining Effects --------
    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (!(event.getLevel() instanceof ServerLevel level)) return;
        Player player = event.getPlayer();
        if (player == null) return;

        BlockPos pos = event.getPos();
        BlockState state = level.getBlockState(pos);

        if (player.hasEffect(ModEffects.R_TREES) && state.is(BlockTags.LOGS)) {
            breakConnectedLogs(level, pos, player);
        }

        if (player.hasEffect(ModEffects.VEIN_HIGH) && state.is(Tags.Blocks.ORES)) {
            veinMine(level, pos, state, player);
        }
    }

    private static void breakConnectedLogs(ServerLevel level, BlockPos start, Player player) {
        Set<BlockPos> visited = new HashSet<>();
        Queue<BlockPos> q = new ArrayDeque<>();
        q.add(start);
        int broken = 0;

        while (!q.isEmpty() && broken < MAX_TREE) {
            BlockPos p = q.poll();
            if (!visited.add(p)) continue;
            BlockState s = level.getBlockState(p);
            if (!s.is(BlockTags.LOGS)) continue;

            level.destroyBlock(p, true, player);
            broken++;

            for (BlockPos n : BlockPos.betweenClosedStream(p.offset(-1, -1, -1), p.offset(1, 1, 1)).map(BlockPos::immutable).toList()) {
                if (!visited.contains(n)) q.add(n);
            }
        }
    }

    private static void veinMine(ServerLevel level, BlockPos start, BlockState target, Player player) {
        Set<BlockPos> visited = new HashSet<>();
        Queue<BlockPos> q = new ArrayDeque<>();
        q.add(start);
        int broken = 0;

        while (!q.isEmpty() && broken < MAX_VEIN) {
            BlockPos p = q.poll();
            if (!visited.add(p)) continue;
            BlockState s = level.getBlockState(p);
            if (!s.is(target.getBlock())) continue;

            level.destroyBlock(p, true, player);
            broken++;

            for (BlockPos n : new BlockPos[]{p.north(), p.south(), p.east(), p.west(), p.above(), p.below()}) {
                if (!visited.contains(n)) q.add(n);
            }
        }
    }




    // -------- Furnace Fuel: Hemp Coal --------
    @SubscribeEvent
    public static void onFuelBurnTime(FurnaceFuelBurnTimeEvent event) {
        if (event.getItemStack().is(ModItems.HEMP_COAL.get())) {
            event.setBurnTime(3200); // 16 items
        }
    }



    // -------- CraftingDataCopy

    @SubscribeEvent
    public static void onCraftingPreview(PlayerEvent.ItemCraftedEvent event) {
        // Copy weed data components from any ingredient that has them to the crafted result
        ItemStack result = event.getCrafting();
        if (result.isEmpty()) return;

        Container inv = event.getInventory();
        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack src = inv.getItem(i);
            if (src.isEmpty()) continue;

            boolean hasWeedData =
                    src.has(ModDataComponentTypes.ACTIVE_INGREDIENT.get()) ||
                            src.has(ModDataComponentTypes.THC.get()) ||
                            src.has(ModDataComponentTypes.CBD.get());

            if (hasWeedData) {
                WeedDataUtil.copyWeedComponents(src, result);
                break;
            }
        }
    }

    private static ItemStack findWeedComponentSource(CraftingContainer grid) {
        for (int i = 0; i < grid.getContainerSize(); i++) {
            ItemStack s = grid.getItem(i);
            if (s.isEmpty()) continue;

            boolean hasAny =
                    s.get(ModDataComponentTypes.ACTIVE_INGREDIENT.get()) != null ||
                            s.get(ModDataComponentTypes.EFFECT_DURATION.get()) != null ||
                            s.get(ModDataComponentTypes.THC.get()) != null ||
                            s.get(ModDataComponentTypes.CBD.get()) != null;

            if (hasAny) return s;
        }
        return ItemStack.EMPTY;
    }

    private static boolean contains(CraftingContainer grid, ItemStack needle) {
        for (int i = 0; i < grid.getContainerSize(); i++) {
            ItemStack s = grid.getItem(i);
            if (!s.isEmpty() && ItemStack.isSameItemSameComponents(s, needle)) return true;
        }
        return false;
    }

    private static ItemStack firstMatching(CraftingContainer grid, boolean weedsOnly) {
        for (int i = 0; i < grid.getContainerSize(); i++) {
            ItemStack s = grid.getItem(i);
            if (s.isEmpty()) continue;
            if (!weedsOnly || s.is(ModTags.WEEDS)) return s;
        }
        return ItemStack.EMPTY;
    }

    private static boolean anySlotMatches(CraftingContainer grid, java.util.function.Predicate<ItemStack> pred) {
        for (int i = 0; i < grid.getContainerSize(); i++) {
            ItemStack s = grid.getItem(i);
            if (!s.isEmpty() && pred.test(s)) return true;
        }
        return false;
    }

    private static ItemStack firstBag(CraftingContainer grid) {
        for (int i = 0; i < grid.getContainerSize(); i++) {
            ItemStack s = grid.getItem(i);
            if (!s.isEmpty() && looksLikeBag(s)) return s;
        }
        return ItemStack.EMPTY;
    }

    private static boolean looksLikeBag(ItemStack stack) {
        var key = BuiltInRegistries.ITEM.getKey(stack.getItem());
        return key != null && key.getPath().endsWith("_bag");
    }



    // -------- Villager Trades --------
    @SubscribeEvent
    public static void addCustomTrades(net.neoforged.neoforge.event.village.VillagerTradesEvent event) {
        if (event.getType() == ModVillagers.STONER.value()) {
            Int2ObjectMap<List<net.minecraft.world.entity.npc.VillagerTrades.ItemListing>> trades = event.getTrades();

            addRandomTrades(trades, 1, 2,
                    (pTrader, pRandom) -> new MerchantOffer(new ItemCost(ModItems.HEMP_FIBERS, 18), new ItemStack(Items.EMERALD, 1), 16, 2, 0.01f),
                    (pTrader, pRandom) -> new MerchantOffer(new ItemCost(ModItems.HEMP_COAL, 5), new ItemStack(Items.EMERALD, 1), 16, 2, 0.01f),
                    (pTrader, pRandom) -> new MerchantOffer(new ItemCost(ModItems.DRIED_TOBACCO_LEAF, 8), new ItemStack(Items.EMERALD, 1), 12, 2, 0.01f),
                    (pTrader, pRandom) -> new MerchantOffer(new ItemCost(ModItems.HEMP_LEAF, 5), new ItemStack(Items.EMERALD, 1), 10, 2, 0.01f)
            );

            addRandomTrades(trades, 2, 1,
                    (pTrader, pRandom) -> new MerchantOffer(new ItemCost(ModItems.WHITE_WIDOW_BAG, 1), new ItemStack(Items.EMERALD, 1), 10, 5, 0.01f),
                    (pTrader, pRandom) -> new MerchantOffer(new ItemCost(ModItems.BUBBLE_KUSH_BAG, 1), new ItemStack(Items.EMERALD, 1), 10, 5, 0.01f),
                    (pTrader, pRandom) -> new MerchantOffer(new ItemCost(ModItems.LEMON_HAZE_BAG, 1), new ItemStack(Items.EMERALD, 1), 10, 5, 0.01f),
                    (pTrader, pRandom) -> new MerchantOffer(new ItemCost(ModItems.SOUR_DIESEL_BAG, 1), new ItemStack(Items.EMERALD, 1), 10, 5, 0.01f),
                    (pTrader, pRandom) -> new MerchantOffer(new ItemCost(ModItems.BLUE_ICE_BAG, 1), new ItemStack(Items.EMERALD, 1), 10, 5, 0.01f),
                    (pTrader, pRandom) -> new MerchantOffer(new ItemCost(ModItems.BUBBLEGUM_BAG, 1), new ItemStack(Items.EMERALD, 1), 10, 5, 0.01f),
                    (pTrader, pRandom) -> new MerchantOffer(new ItemCost(ModItems.PURPLE_HAZE_BAG, 1), new ItemStack(Items.EMERALD, 1), 10, 5, 0.01f),
                    (pTrader, pRandom) -> new MerchantOffer(new ItemCost(ModItems.OG_KUSH_BAG, 1), new ItemStack(Items.EMERALD, 1), 10, 5, 0.01f),
                    (pTrader, pRandom) -> new MerchantOffer(new ItemCost(ModItems.JACK_HERER_BAG, 1), new ItemStack(Items.EMERALD, 1), 10, 5, 0.01f),
                    (pTrader, pRandom) -> new MerchantOffer(new ItemCost(ModItems.GARY_PEYTON_BAG, 1), new ItemStack(Items.EMERALD, 1), 10, 5, 0.01f),
                    (pTrader, pRandom) -> new MerchantOffer(new ItemCost(ModItems.AMNESIA_HAZE_BAG, 1), new ItemStack(Items.EMERALD, 1), 10, 5, 0.01f),
                    (pTrader, pRandom) -> new MerchantOffer(new ItemCost(ModItems.AK47_BAG, 1), new ItemStack(Items.EMERALD, 1), 10, 5, 0.01f),
                    (pTrader, pRandom) -> new MerchantOffer(new ItemCost(ModItems.GHOST_TRAIN_BAG, 1), new ItemStack(Items.EMERALD, 1), 10, 5, 0.01f),
                    (pTrader, pRandom) -> new MerchantOffer(new ItemCost(ModItems.GRAPE_APE_BAG, 1), new ItemStack(Items.EMERALD, 1), 10, 5, 0.01f),
                    (pTrader, pRandom) -> new MerchantOffer(new ItemCost(ModItems.COTTON_CANDY_BAG, 1), new ItemStack(Items.EMERALD, 1), 10, 5, 0.01f),
                    (pTrader, pRandom) -> new MerchantOffer(new ItemCost(ModItems.BANANA_KUSH_BAG, 1), new ItemStack(Items.EMERALD, 1), 10, 5, 0.01f),
                    (pTrader, pRandom) -> new MerchantOffer(new ItemCost(ModItems.CARBON_FIBER_BAG, 1), new ItemStack(Items.EMERALD, 1), 10, 5, 0.01f),
                    (pTrader, pRandom) -> new MerchantOffer(new ItemCost(ModItems.BIRTHDAY_CAKE_BAG, 1), new ItemStack(Items.EMERALD, 1), 10, 5, 0.01f),
                    (pTrader, pRandom) -> new MerchantOffer(new ItemCost(ModItems.BLUE_COOKIES_BAG, 1), new ItemStack(Items.EMERALD, 1), 10, 5, 0.01f),
                    (pTrader, pRandom) -> new MerchantOffer(new ItemCost(ModItems.AFGHANI_BAG, 1), new ItemStack(Items.EMERALD, 1), 10, 5, 0.01f),
                    (pTrader, pRandom) -> new MerchantOffer(new ItemCost(ModItems.MOONBOW_BAG, 1), new ItemStack(Items.EMERALD, 1), 10, 5, 0.01f),
                    (pTrader, pRandom) -> new MerchantOffer(new ItemCost(ModItems.LAVA_CAKE_BAG, 1), new ItemStack(Items.EMERALD, 1), 10, 5, 0.01f),
                    (pTrader, pRandom) -> new MerchantOffer(new ItemCost(ModItems.JELLY_RANCHER_BAG, 1), new ItemStack(Items.EMERALD, 1), 10, 5, 0.01f),
                    (pTrader, pRandom) -> new MerchantOffer(new ItemCost(ModItems.STRAWBERRY_SHORTCAKE_BAG, 1), new ItemStack(Items.EMERALD, 1), 10, 5, 0.01f),
                    (pTrader, pRandom) -> new MerchantOffer(new ItemCost(ModItems.PINK_KUSH_BAG, 1), new ItemStack(Items.EMERALD, 1), 10, 5, 0.01f)
            );
            addRandomTrades(trades, 2, 1,
                    (pTrader, pRandom) -> new MerchantOffer(new ItemCost(ModItems.TOBACCO, 10), new ItemStack(Items.EMERALD, 1), 6, 5, 0.01f),
                    (pTrader, pRandom) -> new MerchantOffer(new ItemCost(ModItems.BIO_COMPOSITE, 1), new ItemStack(Items.EMERALD, 3), 8, 5, 0.01f)
            );

            addRandomTrades(trades, 3, 2,
                    (pTrader, pRandom) -> new MerchantOffer(new ItemCost(ModFluids.HASH_OIL_BUCKET, 1), new ItemStack(Items.EMERALD, 3), 4, 10, 0.01f),
                    (pTrader, pRandom) -> new MerchantOffer(new ItemCost(ModFluids.HEMP_OIL_BUCKET, 1), new ItemStack(Items.EMERALD, 3), 4, 10, 0.01f),
                    (pTrader, pRandom) -> new MerchantOffer(new ItemCost(ModItems.EMPTY_TINCTURE, 4), new ItemStack(Items.EMERALD, 1), 4, 10, 0.01f),
                    (pTrader, pRandom) -> new MerchantOffer(new ItemCost(ModItems.INFUSED_BUTTER, 3), new ItemStack(Items.EMERALD, 1), 7, 10, 0.01f)
            );

            addRandomTrades(trades, 4, 2,
                    (pTrader, pRandom) -> new MerchantOffer(new ItemCost(ModItems.WEED_COOKIE, 1), new ItemStack(Items.EMERALD, 2), 8, 15, 0.01f),
                    (pTrader, pRandom) -> new MerchantOffer(new ItemCost(ModItems.HASH_BROWNIE, 1), new ItemStack(Items.EMERALD, 3), 6, 15, 0.01f),
                    (pTrader, pRandom) -> new MerchantOffer(new ItemCost(ModItems.DAB_RIG, 1), new ItemStack(Items.EMERALD, 3), 1, 15, 0.01f),
                    (pTrader, pRandom) -> new MerchantOffer(new ItemCost(ModItems.BONG, 1), new ItemStack(Items.EMERALD, 4), 1, 15, 0.01f)
            );

            addRandomTrades(trades, 5, 2,
                    (pTrader, pRandom) -> new MerchantOffer(new ItemCost(ModItems.HERB_CAKE, 1), new ItemStack(Items.EMERALD, 6), 4, 20, 0.01f),
                    (pTrader, pRandom) -> new MerchantOffer(new ItemCost(ModItems.HASH_OIL_TINCTURE, 1), new ItemStack(Items.EMERALD, 5), 4, 20, 0.01f),
                    (pTrader, pRandom) -> new MerchantOffer(new ItemCost(ModItems.BLUNT, 2), new ItemStack(Items.EMERALD, 3), 6, 20, 0.01f),
                    (pTrader, pRandom) -> new MerchantOffer(new ItemCost(ModItems.JOINT, 3), new ItemStack(Items.EMERALD, 2), 8, 20, 0.01f)
            );
        }

        if (event.getType() == ModVillagers.DEALER.value()) {
            Int2ObjectMap<List<net.minecraft.world.entity.npc.VillagerTrades.ItemListing>> trades = event.getTrades();

            trades.get(1).add((pTrader, pRandom) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 1),
                    new ItemStack(ModItems.HEMP_SEEDS.get(), 1), 16, 2, 0.01f)
            );
            trades.get(1).add((pTrader, pRandom) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 1),
                    new ItemStack(ModItems.TOBACCO_SEEDS.get(), 1), 16, 2, 0.01f)
            );

            addRandomTrades(trades, 2, 1,
                    (pTrader, pRandom) -> new MerchantOffer(new ItemCost(Items.EMERALD, 4), new ItemStack(ModItems.WHITE_WIDOW_WEED.get(), 1), 6, 5, 0.01f),
                    (pTrader, pRandom) -> new MerchantOffer(new ItemCost(Items.EMERALD, 4), new ItemStack(ModItems.BUBBLE_KUSH_WEED.get(), 1), 6, 5, 0.01f),
                    (pTrader, pRandom) -> new MerchantOffer(new ItemCost(Items.EMERALD, 5), new ItemStack(ModItems.LEMON_HAZE_WEED.get(), 1), 6, 5, 0.01f),
                    (pTrader, pRandom) -> new MerchantOffer(new ItemCost(Items.EMERALD, 4), new ItemStack(ModItems.SOUR_DIESEL_WEED.get(), 1), 6, 5, 0.01f),
                    (pTrader, pRandom) -> new MerchantOffer(new ItemCost(Items.EMERALD, 4), new ItemStack(ModItems.BLUE_ICE_WEED.get(), 1), 6, 5, 0.01f),
                    (pTrader, pRandom) -> new MerchantOffer(new ItemCost(Items.EMERALD, 5), new ItemStack(ModItems.BUBBLEGUM_WEED.get(), 1), 6, 5, 0.01f),
                    (pTrader, pRandom) -> new MerchantOffer(new ItemCost(Items.EMERALD, 6), new ItemStack(ModItems.PURPLE_HAZE_WEED.get(), 1), 6, 5, 0.01f),
                    (pTrader, pRandom) -> new MerchantOffer(new ItemCost(Items.EMERALD, 3), new ItemStack(ModItems.OG_KUSH_WEED.get(), 1), 6, 5, 0.01f),
                    (pTrader, pRandom) -> new MerchantOffer(new ItemCost(Items.EMERALD, 4), new ItemStack(ModItems.JACK_HERER_WEED.get(), 1), 6, 5, 0.01f),
                    (pTrader, pRandom) -> new MerchantOffer(new ItemCost(Items.EMERALD, 5), new ItemStack(ModItems.GARY_PEYTON_WEED.get(), 1), 6, 5, 0.01f),
                    (pTrader, pRandom) -> new MerchantOffer(new ItemCost(Items.EMERALD, 6), new ItemStack(ModItems.AMNESIA_HAZE_WEED.get(), 1), 6, 5, 0.01f),
                    (pTrader, pRandom) -> new MerchantOffer(new ItemCost(Items.EMERALD, 5), new ItemStack(ModItems.AK47_WEED.get(), 1), 6, 5, 0.01f),
                    (pTrader, pRandom) -> new MerchantOffer(new ItemCost(Items.EMERALD, 4), new ItemStack(ModItems.GHOST_TRAIN_WEED.get(), 1), 6, 5, 0.01f),
                    (pTrader, pRandom) -> new MerchantOffer(new ItemCost(Items.EMERALD, 6), new ItemStack(ModItems.GRAPE_APE_WEED.get(), 1), 6, 5, 0.01f),
                    (pTrader, pRandom) -> new MerchantOffer(new ItemCost(Items.EMERALD, 6), new ItemStack(ModItems.COTTON_CANDY_WEED.get(), 1), 6, 5, 0.01f),
                    (pTrader, pRandom) -> new MerchantOffer(new ItemCost(Items.EMERALD, 5), new ItemStack(ModItems.BANANA_KUSH_WEED.get(), 1), 6, 5, 0.01f),
                    (pTrader, pRandom) -> new MerchantOffer(new ItemCost(Items.EMERALD, 4), new ItemStack(ModItems.CARBON_FIBER_WEED.get(), 1), 6, 5, 0.01f),
                    (pTrader, pRandom) -> new MerchantOffer(new ItemCost(Items.EMERALD, 6), new ItemStack(ModItems.BIRTHDAY_CAKE_WEED.get(), 1), 6, 5, 0.01f),
                    (pTrader, pRandom) -> new MerchantOffer(new ItemCost(Items.EMERALD, 5), new ItemStack(ModItems.BLUE_COOKIES_WEED.get(), 1), 6, 5, 0.01f),
                    (pTrader, pRandom) -> new MerchantOffer(new ItemCost(Items.EMERALD, 6), new ItemStack(ModItems.AFGHANI_WEED.get(), 1), 6, 5, 0.01f),
                    (pTrader, pRandom) -> new MerchantOffer(new ItemCost(Items.EMERALD, 4), new ItemStack(ModItems.MOONBOW_WEED.get(), 1), 6, 5, 0.01f),
                    (pTrader, pRandom) -> new MerchantOffer(new ItemCost(Items.EMERALD, 6), new ItemStack(ModItems.LAVA_CAKE_WEED.get(), 1), 6, 5, 0.01f),
                    (pTrader, pRandom) -> new MerchantOffer(new ItemCost(Items.EMERALD, 5), new ItemStack(ModItems.JELLY_RANCHER_WEED.get(), 1), 6, 5, 0.01f),
                    (pTrader, pRandom) -> new MerchantOffer(new ItemCost(Items.EMERALD, 6), new ItemStack(ModItems.STRAWBERRY_SHORTCAKE_WEED.get(), 1), 6, 5, 0.01f),
                    (pTrader, pRandom) -> new MerchantOffer(new ItemCost(Items.EMERALD, 5), new ItemStack(ModItems.PINK_KUSH_WEED.get(), 1), 6, 5, 0.01f)
            );
            addRandomTrades(trades, 2, 1,
                    (pTrader, pRandom) -> new MerchantOffer(new ItemCost(Items.EMERALD, 4), new ItemStack(ModItems.WHITE_WIDOW_GUMMY.get(), 1), 4, 5, 0.01f),
                    (pTrader, pRandom) -> new MerchantOffer(new ItemCost(Items.EMERALD, 4), new ItemStack(ModItems.BUBBLE_KUSH_GUMMY.get(), 1), 4, 5, 0.01f),
                    (pTrader, pRandom) -> new MerchantOffer(new ItemCost(Items.EMERALD, 5), new ItemStack(ModItems.LEMON_HAZE_GUMMY.get(), 1), 4, 5, 0.01f),
                    (pTrader, pRandom) -> new MerchantOffer(new ItemCost(Items.EMERALD, 4), new ItemStack(ModItems.SOUR_DIESEL_GUMMY.get(), 1), 4, 5, 0.01f),
                    (pTrader, pRandom) -> new MerchantOffer(new ItemCost(Items.EMERALD, 4), new ItemStack(ModItems.BLUE_ICE_GUMMY.get(), 1), 4, 5, 0.01f),
                    (pTrader, pRandom) -> new MerchantOffer(new ItemCost(Items.EMERALD, 5), new ItemStack(ModItems.BUBBLEGUM_GUMMY.get(), 1), 4, 5, 0.01f),
                    (pTrader, pRandom) -> new MerchantOffer(new ItemCost(Items.EMERALD, 6), new ItemStack(ModItems.PURPLE_HAZE_GUMMY.get(), 1), 4, 5, 0.01f),
                    (pTrader, pRandom) -> new MerchantOffer(new ItemCost(Items.EMERALD, 3), new ItemStack(ModItems.OG_KUSH_GUMMY.get(), 1), 4, 5, 0.01f),
                    (pTrader, pRandom) -> new MerchantOffer(new ItemCost(Items.EMERALD, 4), new ItemStack(ModItems.JACK_HERER_GUMMY.get(), 1), 4, 5, 0.01f),
                    (pTrader, pRandom) -> new MerchantOffer(new ItemCost(Items.EMERALD, 5), new ItemStack(ModItems.GARY_PEYTON_GUMMY.get(), 1), 4, 5, 0.01f),
                    (pTrader, pRandom) -> new MerchantOffer(new ItemCost(Items.EMERALD, 6), new ItemStack(ModItems.AMNESIA_HAZE_GUMMY.get(), 1), 4, 5, 0.01f),
                    (pTrader, pRandom) -> new MerchantOffer(new ItemCost(Items.EMERALD, 5), new ItemStack(ModItems.AK47_GUMMY.get(), 1), 4, 5, 0.01f),
                    (pTrader, pRandom) -> new MerchantOffer(new ItemCost(Items.EMERALD, 4), new ItemStack(ModItems.GHOST_TRAIN_GUMMY.get(), 1), 4, 5, 0.01f),
                    (pTrader, pRandom) -> new MerchantOffer(new ItemCost(Items.EMERALD, 6), new ItemStack(ModItems.GRAPE_APE_GUMMY.get(), 1), 4, 5, 0.01f),
                    (pTrader, pRandom) -> new MerchantOffer(new ItemCost(Items.EMERALD, 6), new ItemStack(ModItems.COTTON_CANDY_GUMMY.get(), 1), 4, 5, 0.01f),
                    (pTrader, pRandom) -> new MerchantOffer(new ItemCost(Items.EMERALD, 5), new ItemStack(ModItems.BANANA_KUSH_GUMMY.get(), 1), 4, 5, 0.01f),
                    (pTrader, pRandom) -> new MerchantOffer(new ItemCost(Items.EMERALD, 4), new ItemStack(ModItems.CARBON_FIBER_GUMMY.get(), 1), 4, 5, 0.01f),
                    (pTrader, pRandom) -> new MerchantOffer(new ItemCost(Items.EMERALD, 6), new ItemStack(ModItems.BIRTHDAY_CAKE_GUMMY.get(), 1), 4, 5, 0.01f),
                    (pTrader, pRandom) -> new MerchantOffer(new ItemCost(Items.EMERALD, 5), new ItemStack(ModItems.BLUE_COOKIES_GUMMY.get(), 1), 4, 5, 0.01f),
                    (pTrader, pRandom) -> new MerchantOffer(new ItemCost(Items.EMERALD, 6), new ItemStack(ModItems.AFGHANI_GUMMY.get(), 1), 4, 5, 0.01f),
                    (pTrader, pRandom) -> new MerchantOffer(new ItemCost(Items.EMERALD, 4), new ItemStack(ModItems.MOONBOW_GUMMY.get(), 1), 4, 5, 0.01f),
                    (pTrader, pRandom) -> new MerchantOffer(new ItemCost(Items.EMERALD, 6), new ItemStack(ModItems.LAVA_CAKE_GUMMY.get(), 1), 4, 5, 0.01f),
                    (pTrader, pRandom) -> new MerchantOffer(new ItemCost(Items.EMERALD, 5), new ItemStack(ModItems.JELLY_RANCHER_GUMMY.get(), 1), 4, 5, 0.01f),
                    (pTrader, pRandom) -> new MerchantOffer(new ItemCost(Items.EMERALD, 6), new ItemStack(ModItems.STRAWBERRY_SHORTCAKE_GUMMY.get(), 1), 4, 5, 0.01f),
                    (pTrader, pRandom) -> new MerchantOffer(new ItemCost(Items.EMERALD, 5), new ItemStack(ModItems.PINK_KUSH_GUMMY.get(), 1), 4, 5, 0.01f)
            );

            addRandomTrades(trades, 3, 2,
                    (pTrader, pRandom) -> new MerchantOffer(new ItemCost(Items.EMERALD, 5), new ItemStack(ModItems.BASE_EXTRACT.get(), 1), 8, 10, 0.01f),
                    (pTrader, pRandom) -> new MerchantOffer(new ItemCost(Items.EMERALD, 8), new ItemStack(ModFluids.HASH_OIL_BUCKET.get(), 1), 4, 10, 0.01f),
                    (pTrader, pRandom) -> new MerchantOffer(new ItemCost(Items.EMERALD, 7), new ItemStack(ModFluids.HEMP_OIL_BUCKET.get(), 1), 4, 10, 0.01f),
                    (pTrader, pRandom) -> new MerchantOffer(new ItemCost(Items.EMERALD, 4), new ItemStack(ModItems.BUTTER.get(), 1), 6, 10, 0.01f),
                    (pTrader, pRandom) -> new MerchantOffer(new ItemCost(Items.EMERALD, 14), new ItemStack(ModItems.DNA_STRAND.get(), 1), 6, 10, 0.01f)
            );

            addRandomTrades(trades, 4, 2,
                    (pTrader, pRandom) -> new MerchantOffer(new ItemCost(Items.EMERALD, 5), new ItemStack(ModItems.BIO_COMPOSITE.get(), 1), 10, 10, 0.01f),
                    (pTrader, pRandom) -> new MerchantOffer(new ItemCost(Items.EMERALD, 10), new ItemStack(ModItems.DUAL_ARC_LAMP.get(), 1), 2, 10, 0.01f),
                    (pTrader, pRandom) -> new MerchantOffer(new ItemCost(Items.EMERALD, 8), new ItemStack(ModItems.HEMP_PLASTIC.get(), 1), 6, 10, 0.01f),
                    (pTrader, pRandom) -> new MerchantOffer(new ItemCost(Items.EMERALD, 7), new ItemStack(ModItems.UNFINISHED_HEMP_CORE.get(), 1), 2, 10, 0.01f),
                    (pTrader, pRandom) -> new MerchantOffer(new ItemCost(Items.EMERALD, 4), new ItemStack(ModItems.INFUSED_BUTTER.get(), 1), 4, 10, 0.01f),
                    (pTrader, pRandom) -> new MerchantOffer(new ItemCost(Items.EMERALD, 14), new ItemStack(ModItems.CAT_URINE_BOTTLE.get(), 1), 2, 10, 0.01f)
            );

            addRandomTrades(trades, 5, 2,
                    (pTrader, pRandom) -> new MerchantOffer(new ItemCost(Items.EMERALD, 10), new ItemStack(ModItems.DNA_STRAND.get(), 1), 4, 20, 0.01f),
                    (pTrader, pRandom) -> new MerchantOffer(new ItemCost(Items.EMERALD, 14), new ItemStack(ModItems.HEMP_CORE.get(), 1), 2, 20, 0.01f),
                    (pTrader, pRandom) -> new MerchantOffer(new ItemCost(Items.EMERALD, 8), new ItemStack(ModItems.HASH_OIL_TINCTURE.get(), 1), 2, 20, 0.01f),
                    (pTrader, pRandom) -> new MerchantOffer(new ItemCost(Items.EMERALD, 13), new ItemStack(ModItems.MANUAL_GRINDER.get(), 1), 1, 20, 0.01f),
                    (pTrader, pRandom) -> new MerchantOffer(new ItemCost(Items.EMERALD, 16), new ItemStack(ModItems.HEMP_HAMMER.get(), 1), 1, 20, 0.01f)
            );
        }
    }

    private static void addRandomTrades(Int2ObjectMap<List<net.minecraft.world.entity.npc.VillagerTrades.ItemListing>> trades, int level, int pick, net.minecraft.world.entity.npc.VillagerTrades.ItemListing... candidates) {
        List<net.minecraft.world.entity.npc.VillagerTrades.ItemListing> pool = new ArrayList<>(List.of(candidates));
        Collections.shuffle(pool);
        List<net.minecraft.world.entity.npc.VillagerTrades.ItemListing> levelList = trades.get(level);
        levelList.addAll(pool.subList(0, Math.min(pick, pool.size())));
    }

    // -----------------------------------------------------------------------
    // Mixed-strain naming via Anvil
    // -----------------------------------------------------------------------

    /**
     * Handles renaming any strain-bearing item (seeds, buds, weeds, extracts, oil buckets) in an
     * anvil. Two paths:
     * <ol>
     *   <li>Item has {@code STRAIN_ID} → generic lineage rename for any custom strain.</li>
     *   <li>Item is a bucket with {@code MIX_KEY} but no {@code STRAIN_ID} (legacy) → bucket-only rename.</li>
     * </ol>
     */
    @SubscribeEvent
    public static void onAnvilUpdate(AnvilUpdateEvent event) {
        String newName = event.getName();
        if (newName == null || newName.isBlank()) return;

        ItemStack left = event.getLeft();

        // Generic path: any item with STRAIN_ID
        String strainId = left.get(ModDataComponentTypes.STRAIN_ID.get());
        if (strainId != null) {
            StrainData d = StrainUtil.getStrain(left);
            if (d == StrainData.EMPTY) return;

            StrainData namedData = new StrainData(
                    d.colorArgb(), d.leafColor(), d.thc(), d.cbd(),
                    d.nitrogen(), d.phosphorus(), d.potassium(),
                    d.effects(), d.amplifier(), d.durationTicks(),
                    true, newName, d.typeColors()
            );
            ItemStack output = left.copy();
            StrainUtil.setStrain(output, namedData);

            event.setOutput(output);
            event.setCost(0);
            event.setMaterialCost(0);
            return;
        }

        // Fallback: any item with STRAIN_DATA (preset strains without a lineage ID)
        StrainData anyStrain = StrainUtil.getStrain(left);
        if (anyStrain != StrainData.EMPTY) {
            StrainData namedData = new StrainData(
                    anyStrain.colorArgb(), anyStrain.leafColor(), anyStrain.thc(), anyStrain.cbd(),
                    anyStrain.nitrogen(), anyStrain.phosphorus(), anyStrain.potassium(),
                    anyStrain.effects(), anyStrain.amplifier(), anyStrain.durationTicks(),
                    true, newName, anyStrain.typeColors()
            );
            ItemStack output = left.copy();
            StrainUtil.setStrain(output, namedData);
            event.setOutput(output);
            event.setCost(0);
            event.setMaterialCost(0);
            return;
        }

        // Legacy bucket-only path (no STRAIN_ID yet)
        if (!(left.getItem() instanceof UnidentifiedMixtureBucketItem)) return;
        StrainData d = StrainUtil.getStrain(left);
        if (d == StrainData.EMPTY || d.identified()) return;

        String mixKey = left.get(ModDataComponentTypes.MIX_KEY.get());
        if (mixKey == null || mixKey.isBlank()) return;

        StrainData namedData = new StrainData(
                d.colorArgb(), d.leafColor(), d.thc(), d.cbd(),
                d.nitrogen(), d.phosphorus(), d.potassium(),
                d.effects(), d.amplifier(), d.durationTicks(),
                true, newName, d.typeColors()
        );
        ItemStack output = left.copy();
        StrainUtil.setStrain(output, namedData);
        output.set(ModDataComponentTypes.MIX_KEY.get(), mixKey);

        event.setOutput(output);
        event.setCost(0);
        event.setMaterialCost(0);
    }

    /**
     * When the player takes the renamed item from the anvil, persist the name server-wide and
     * propagate to all online players carrying items with the same strain ID.
     */
    @SubscribeEvent
    public static void onAnvilRepair(AnvilRepairEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer sp)) return;
        ItemStack output = event.getOutput();

        StrainData d = StrainUtil.getStrain(output);
        if (d == StrainData.EMPTY || !d.identified() || d.displayName().isBlank()) return;

        // Generic path: item has STRAIN_ID
        String strainId = output.get(ModDataComponentTypes.STRAIN_ID.get());
        if (strainId != null) {
            StrainRegistrySavedData.get(sp.server).propagateRename(sp.server, strainId, d.displayName(), sp.getName().getString());
            // If this is also a mixer-blend, keep MixedStrainSavedData in sync for re-identification
            if (output.getItem() instanceof UnidentifiedMixtureBucketItem) {
                String mixKey = output.get(ModDataComponentTypes.MIX_KEY.get());
                if (mixKey != null) MixedStrainSavedData.get(sp.server).register(mixKey, d.displayName());
            }
            return;
        }

        // Legacy bucket path
        if (!(output.getItem() instanceof UnidentifiedMixtureBucketItem)) return;
        String mixKey = output.get(ModDataComponentTypes.MIX_KEY.get());
        if (mixKey == null || mixKey.isBlank()) return;
        MixedStrainSavedData.get(sp.server).register(mixKey, d.displayName());
    }

    /**
     * On player login, sync embedded strain names from the server registry so offline-obtained
     * items reflect any renames that happened while the player was away.
     */
    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer sp)) return;
        StrainRegistrySavedData.get(sp.server).syncPlayerInventory(sp);
    }
}
