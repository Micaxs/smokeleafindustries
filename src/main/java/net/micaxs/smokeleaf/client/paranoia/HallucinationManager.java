package net.micaxs.smokeleaf.client.paranoia;

import com.mojang.blaze3d.systems.RenderSystem;
import net.micaxs.smokeleaf.SmokeleafIndustries;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Mod.EventBusSubscriber(modid = SmokeleafIndustries.MODID, value = Dist.CLIENT)
public final class HallucinationManager {

    // Track lifetimes per spawned entity on the client
    private static final Map<Entity, Integer> LIFETIMES = new WeakHashMap<>();
    // Generate unique negative IDs for client-only entities
    private static final AtomicInteger NEXT_FAKE_ID = new AtomicInteger(-1);

    // Alpha for ghost rendering (0.0f..1.0f)
    private static final float GHOST_ALPHA = 0.3f;

    private HallucinationManager() {}

    public static void spawn(EntityType<?> type,
                             double x, double y, double z,
                             float yaw,
                             int lifeTicks,
                             SoundEvent soundOrNull) {
        Minecraft mc = Minecraft.getInstance();
        ClientLevel level = mc.level;
        if (level == null) return;

        Entity e = type.create(level);
        if (e == null) return;

        // Find ground Y at \[x,z] by raycasting downward (works in caves as well)
        double groundY = findGroundY(level, x, y, z);
        // Basic placement/orientation
        e.moveTo(x, groundY, z, yaw, 0.0F);
        e.setNoGravity(true);

        if (e instanceof Mob mob) {
            mob.setNoAi(true); // no AI for illusion
        }

        // Assign a client-only ID and add to the client world
        int id = NEXT_FAKE_ID.getAndDecrement();
        e.setId(id);
        level.putNonPlayerEntity(id, e); // proper client-side registration

        // Track lifetime
        LIFETIMES.put(e, Math.max(1, lifeTicks));

        // Optional sound
        if (soundOrNull != null) {
            level.playLocalSound(x, groundY, z, soundOrNull, SoundSource.AMBIENT, 1.0f, 1.0f, false);
        }
    }

    // Returns true if the entity is one of our hallucinations
    public static boolean isHallucination(Entity e) {
        return e != null && LIFETIMES.containsKey(e);
    }

    // Render ghosts with translucency
    @SubscribeEvent
    public static void onRenderLivingPre(RenderLivingEvent.Pre<?, ?> evt) {
        if (!isHallucination(evt.getEntity())) return;

        // Enable blending and set global shader alpha
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(1f, 1f, 1f, GHOST_ALPHA);
    }

    @SubscribeEvent
    public static void onRenderLivingPost(RenderLivingEvent.Post<?, ?> evt) {
        if (!isHallucination(evt.getEntity())) return;

        // Restore default state
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.disableBlend();
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent evt) {
        if (evt.phase != TickEvent.Phase.END) return;
        if (LIFETIMES.isEmpty()) return;

        Iterator<Map.Entry<Entity, Integer>> it = LIFETIMES.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Entity, Integer> entry = it.next();
            Entity e = entry.getKey();

            if (e == null || e.level() == null || !e.level().isClientSide()) {
                it.remove();
                continue;
            }

            int left = entry.getValue() - 1;
            if (left <= 0) {
                e.remove(Entity.RemovalReason.DISCARDED);
                if (e.level() instanceof ClientLevel cl) {
                    cl.removeEntity(e.getId(), Entity.RemovalReason.DISCARDED);
                }
                it.remove();
            } else {
                entry.setValue(left);
            }
        }
    }

    private static double findGroundY(ClientLevel level, double x, double startY, double z) {
        double fromY = Math.min(level.getMaxBuildHeight() - 1, startY + 32.0);
        double toY = Math.max(level.getMinBuildHeight(), startY - 64.0);
        Vec3 from = new Vec3(x, fromY, z);
        Vec3 to = new Vec3(x, toY, z);

        BlockHitResult hit = level.clip(new ClipContext(
                from, to,
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.ANY,
                null
        ));

        if (hit.getType() != HitResult.Type.MISS) {
            // Place slightly above the hit to avoid z-fighting
            return hit.getLocation().y + 0.01;
        }
        return startY;
    }
}