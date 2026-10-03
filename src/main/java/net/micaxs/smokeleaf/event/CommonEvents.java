package net.micaxs.smokeleaf.event;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.micaxs.smokeleaf.SmokeleafIndustries;
import net.micaxs.smokeleaf.component.ManualGrinderContents;
import net.micaxs.smokeleaf.component.ModDataComponentTypes;
import net.micaxs.smokeleaf.effect.ModEffects;
import net.micaxs.smokeleaf.fluid.ModFluids;
import net.micaxs.smokeleaf.item.ModItems;
import net.micaxs.smokeleaf.item.custom.StrainBookItem;
import net.micaxs.smokeleaf.item.custom.BaseWeedItem;
import net.micaxs.smokeleaf.item.custom.ManualGrinderItem;
import net.micaxs.smokeleaf.item.custom.UnidentifiedMixtureBucketItem;
import net.micaxs.smokeleaf.strain.MixedStrainSavedData;
import net.micaxs.smokeleaf.strain.StrainData;
import net.micaxs.smokeleaf.strain.StrainRegistry;
import net.micaxs.smokeleaf.strain.StrainRegistrySavedData;
import net.micaxs.smokeleaf.strain.StrainUtil;
import net.micaxs.smokeleaf.utils.ModTags;
import net.micaxs.smokeleaf.utils.WeedDataUtil;
import net.micaxs.smokeleaf.villager.ModVillagers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.component.DataComponents;
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
import net.minecraft.world.item.Item;
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
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;
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
    // LoadManualGrinderRecipe lets 1-3 grind-worths of the same item load a Manual Grinder,
    // spread across up to 3 grid slots in any stack size. The vanilla crafting grid always
    // removes exactly 1 item from every contributing slot when the result is taken, no matter
    // how many are actually needed — fine when the player used 3 separate one-count slots, but
    // insufficient when (say) a single stack of 8 supplies all 3. This event fires before that
    // vanilla removal, so it tops up the shortfall by shrinking the surplus out of whichever
    // ingredient slot(s) have more than 1, leaving the vanilla -1-per-slot pass to finish the job.
    @SubscribeEvent
    public static void onManualGrinderCraft(PlayerEvent.ItemCraftedEvent event) {
        ItemStack result = event.getCrafting();
        if (!(result.getItem() instanceof ManualGrinderItem)) {
            return;
        }

        ManualGrinderContents contents = result.get(ModDataComponentTypes.MANUAL_GRINDER_CONTENTS.get());
        if (contents == null) return;

        ItemStack stored = contents.stack();
        int storedCount = stored.getCount();

        Container matrix = event.getInventory();
        List<Integer> ingredientSlots = new ArrayList<>();
        for (int i = 0; i < matrix.getContainerSize(); i++) {
            ItemStack slotStack = matrix.getItem(i);
            if (!slotStack.isEmpty() && !(slotStack.getItem() instanceof ManualGrinderItem)
                    && ItemStack.isSameItemSameComponents(slotStack, stored)) {
                ingredientSlots.add(i);
            }
        }

        int deficit = storedCount - ingredientSlots.size();
        for (int slot : ingredientSlots) {
            if (deficit <= 0) break;
            int surplus = matrix.getItem(slot).getCount() - 1;
            if (surplus <= 0) continue;
            int take = Math.min(surplus, deficit);
            matrix.removeItem(slot, take);
            deficit -= take;
        }
    }





    // -------- Baja Hoodie: full set negates the Stoned effect --------
    private static boolean wearsFullSet(Player player, Item helmet, Item chest, Item legs, Item boots) {
        return player.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.HEAD).is(helmet)
                && player.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.CHEST).is(chest)
                && player.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.LEGS).is(legs)
                && player.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.FEET).is(boots);
    }

    private static boolean wearsFullBajaSet(Player player) {
        return wearsFullSet(player, ModItems.BAJA_HOODIE_HELMET.get(), ModItems.BAJA_HOODIE_CHESTPLATE.get(),
                ModItems.BAJA_HOODIE_LEGGINGS.get(), ModItems.BAJA_HOODIE_BOOTS.get())
                || wearsFullSet(player, ModItems.REINFORCED_BAJA_HOODIE_HELMET.get(), ModItems.REINFORCED_BAJA_HOODIE_CHESTPLATE.get(),
                ModItems.REINFORCED_BAJA_HOODIE_LEGGINGS.get(), ModItems.REINFORCED_BAJA_HOODIE_BOOTS.get());
    }

    @SubscribeEvent
    public static void onStonedApplicable(MobEffectEvent.Applicable event) {
        if (event.getEffectInstance() == null || event.getEffectInstance().getEffect() != ModEffects.STONED) return;
        if (!(event.getEntity() instanceof Player player)) return;
        if (wearsFullBajaSet(player)) {
            event.setResult(MobEffectEvent.Applicable.Result.DO_NOT_APPLY);
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

        // Baja Hoodie: cure Stoned immediately if the full set gets equipped while already high,
        // rather than just blocking future re-applications.
        if (player.hasEffect(ModEffects.STONED) && wearsFullBajaSet(player)) {
            player.removeEffect(ModEffects.STONED);
        }

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
                            src.has(ModDataComponentTypes.CBD.get()) ||
                            src.has(ModDataComponentTypes.STRAIN_DATA.get());

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
                    (pTrader, pRandom) -> weedOffer(4, "white_widow"),
                    (pTrader, pRandom) -> weedOffer(4, "bubble_kush"),
                    (pTrader, pRandom) -> weedOffer(5, "lemon_haze"),
                    (pTrader, pRandom) -> weedOffer(4, "sour_diesel"),
                    (pTrader, pRandom) -> weedOffer(4, "blue_ice"),
                    (pTrader, pRandom) -> weedOffer(5, "bubblegum"),
                    (pTrader, pRandom) -> weedOffer(6, "purple_haze"),
                    (pTrader, pRandom) -> weedOffer(3, "og_kush"),
                    (pTrader, pRandom) -> weedOffer(4, "jack_herer"),
                    (pTrader, pRandom) -> weedOffer(5, "gary_peyton"),
                    (pTrader, pRandom) -> weedOffer(6, "amnesia_haze"),
                    (pTrader, pRandom) -> weedOffer(5, "ak47"),
                    (pTrader, pRandom) -> weedOffer(4, "ghost_train"),
                    (pTrader, pRandom) -> weedOffer(6, "grape_ape"),
                    (pTrader, pRandom) -> weedOffer(6, "cotton_candy"),
                    (pTrader, pRandom) -> weedOffer(5, "banana_kush"),
                    (pTrader, pRandom) -> weedOffer(4, "carbon_fiber"),
                    (pTrader, pRandom) -> weedOffer(6, "birthday_cake"),
                    (pTrader, pRandom) -> weedOffer(5, "blue_cookies"),
                    (pTrader, pRandom) -> weedOffer(6, "afghani"),
                    (pTrader, pRandom) -> weedOffer(4, "moonbow"),
                    (pTrader, pRandom) -> weedOffer(6, "lava_cake"),
                    (pTrader, pRandom) -> weedOffer(5, "jelly_rancher"),
                    (pTrader, pRandom) -> weedOffer(6, "strawberry_shortcake"),
                    (pTrader, pRandom) -> weedOffer(5, "pink_kush")
            );
            addRandomTrades(trades, 2, 1,
                    (pTrader, pRandom) -> new MerchantOffer(new ItemCost(ModItems.TOBACCO, 10), new ItemStack(Items.EMERALD, 1), 6, 5, 0.01f),
                    (pTrader, pRandom) -> new MerchantOffer(new ItemCost(ModItems.BIO_COMPOSITE, 1), new ItemStack(Items.EMERALD, 3), 8, 5, 0.01f)
            );

            addRandomTrades(trades, 3, 2,
                    (pTrader, pRandom) -> new MerchantOffer(new ItemCost(ModFluids.HASH_OIL_BUCKET, 1), new ItemStack(Items.EMERALD, 3), 4, 10, 0.01f),
                    (pTrader, pRandom) -> new MerchantOffer(new ItemCost(ModFluids.HASH_OIL_BUCKET, 1), new ItemStack(Items.EMERALD, 3), 4, 10, 0.01f),
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
                    (pTrader, pRandom) -> weedOffer(4, "white_widow"),
                    (pTrader, pRandom) -> weedOffer(4, "bubble_kush"),
                    (pTrader, pRandom) -> weedOffer(5, "lemon_haze"),
                    (pTrader, pRandom) -> weedOffer(4, "sour_diesel"),
                    (pTrader, pRandom) -> weedOffer(4, "blue_ice"),
                    (pTrader, pRandom) -> weedOffer(5, "bubblegum"),
                    (pTrader, pRandom) -> weedOffer(6, "purple_haze"),
                    (pTrader, pRandom) -> weedOffer(3, "og_kush"),
                    (pTrader, pRandom) -> weedOffer(4, "jack_herer"),
                    (pTrader, pRandom) -> weedOffer(5, "gary_peyton"),
                    (pTrader, pRandom) -> weedOffer(6, "amnesia_haze"),
                    (pTrader, pRandom) -> weedOffer(5, "ak47"),
                    (pTrader, pRandom) -> weedOffer(4, "ghost_train"),
                    (pTrader, pRandom) -> weedOffer(6, "grape_ape"),
                    (pTrader, pRandom) -> weedOffer(6, "cotton_candy"),
                    (pTrader, pRandom) -> weedOffer(5, "banana_kush"),
                    (pTrader, pRandom) -> weedOffer(4, "carbon_fiber"),
                    (pTrader, pRandom) -> weedOffer(6, "birthday_cake"),
                    (pTrader, pRandom) -> weedOffer(5, "blue_cookies"),
                    (pTrader, pRandom) -> weedOffer(6, "afghani"),
                    (pTrader, pRandom) -> weedOffer(4, "moonbow"),
                    (pTrader, pRandom) -> weedOffer(6, "lava_cake"),
                    (pTrader, pRandom) -> weedOffer(5, "jelly_rancher"),
                    (pTrader, pRandom) -> weedOffer(6, "strawberry_shortcake"),
                    (pTrader, pRandom) -> weedOffer(5, "pink_kush")
            );
            addRandomTrades(trades, 2, 1,
                    (pTrader, pRandom) -> gummyOffer(4, "white_widow"),
                    (pTrader, pRandom) -> gummyOffer(4, "bubble_kush"),
                    (pTrader, pRandom) -> gummyOffer(5, "lemon_haze"),
                    (pTrader, pRandom) -> gummyOffer(4, "sour_diesel"),
                    (pTrader, pRandom) -> gummyOffer(4, "blue_ice"),
                    (pTrader, pRandom) -> gummyOffer(5, "bubblegum"),
                    (pTrader, pRandom) -> gummyOffer(6, "purple_haze"),
                    (pTrader, pRandom) -> gummyOffer(3, "og_kush"),
                    (pTrader, pRandom) -> gummyOffer(4, "jack_herer"),
                    (pTrader, pRandom) -> gummyOffer(5, "gary_peyton"),
                    (pTrader, pRandom) -> gummyOffer(6, "amnesia_haze"),
                    (pTrader, pRandom) -> gummyOffer(5, "ak47"),
                    (pTrader, pRandom) -> gummyOffer(4, "ghost_train"),
                    (pTrader, pRandom) -> gummyOffer(6, "grape_ape"),
                    (pTrader, pRandom) -> gummyOffer(6, "cotton_candy"),
                    (pTrader, pRandom) -> gummyOffer(5, "banana_kush"),
                    (pTrader, pRandom) -> gummyOffer(4, "carbon_fiber"),
                    (pTrader, pRandom) -> gummyOffer(6, "birthday_cake"),
                    (pTrader, pRandom) -> gummyOffer(5, "blue_cookies"),
                    (pTrader, pRandom) -> gummyOffer(6, "afghani"),
                    (pTrader, pRandom) -> gummyOffer(4, "moonbow"),
                    (pTrader, pRandom) -> gummyOffer(6, "lava_cake"),
                    (pTrader, pRandom) -> gummyOffer(5, "jelly_rancher"),
                    (pTrader, pRandom) -> gummyOffer(6, "strawberry_shortcake"),
                    (pTrader, pRandom) -> gummyOffer(5, "pink_kush")
            );

            addRandomTrades(trades, 3, 2,
                    (pTrader, pRandom) -> new MerchantOffer(new ItemCost(Items.EMERALD, 5), new ItemStack(ModItems.BASE_EXTRACT.get(), 1), 8, 10, 0.01f),
                    (pTrader, pRandom) -> new MerchantOffer(new ItemCost(Items.EMERALD, 8), new ItemStack(ModFluids.HASH_OIL_BUCKET.get(), 1), 4, 10, 0.01f),
                    (pTrader, pRandom) -> new MerchantOffer(new ItemCost(Items.EMERALD, 7), new ItemStack(ModFluids.HASH_OIL_BUCKET.get(), 1), 4, 10, 0.01f),
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

    private static ItemStack strainedStack(Item item, String strainId) {
        ItemStack stack = new ItemStack(item);
        StrainRegistry.get(strainId).ifPresent(data -> {
            stack.set(ModDataComponentTypes.STRAIN_DATA.get(), data);
            stack.set(ModDataComponentTypes.STRAIN_ID.get(), strainId);
        });
        return stack;
    }

    private static MerchantOffer weedOffer(int emeraldCost, String strainId) {
        return new MerchantOffer(new ItemCost(Items.EMERALD, emeraldCost), strainedStack(ModItems.GENERIC_WEED.get(), strainId), 6, 5, 0.01f);
    }

    private static MerchantOffer gummyOffer(int emeraldCost, String strainId) {
        return new MerchantOffer(new ItemCost(Items.EMERALD, emeraldCost), strainedStack(ModItems.GENERIC_GUMMY.get(), strainId), 4, 5, 0.01f);
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
     * The anvil naming flow is intentionally disabled; custom strain names are handled by the
     * dedicated Strain Modifier machine instead.
     */
    @SubscribeEvent
    public static void onAnvilUpdate(AnvilUpdateEvent event) {
        // intentionally disabled
    }

    /**
     * The anvil naming flow is intentionally disabled; custom strain names are handled by the
     * dedicated Strain Modifier machine instead.
     */
    @SubscribeEvent
    public static void onAnvilRepair(AnvilRepairEvent event) {
        // intentionally disabled
    }

    // -------- Strain Discovery Tracking --------

    @SubscribeEvent
    public static void onItemPickup(ItemEntityPickupEvent.Post event) {
        Player player = event.getPlayer();
        if (player.level().isClientSide) return;
        recordStrainDiscovery(player, event.getOriginalStack());
    }

    private static void recordStrainDiscovery(Player player, ItemStack stack) {
        if (stack == null || stack.isEmpty()) return;
        String strainId = stack.get(ModDataComponentTypes.STRAIN_ID.get());
        if (strainId == null) {
            StrainData sd = StrainUtil.getStrain(stack);
            if (sd != StrainData.EMPTY) {
                strainId = StrainUtil.strainContentId(sd);
            }
        }
        if (strainId != null && !strainId.isBlank()) {
            StrainBookItem.addDiscovery(player, strainId);
        }
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
