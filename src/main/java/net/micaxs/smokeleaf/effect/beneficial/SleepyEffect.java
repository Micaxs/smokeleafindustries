package net.micaxs.smokeleaf.effect.beneficial;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

public class SleepyEffect extends MobEffect {
    // Lateral position targets (in blocks)
    private static final double MIN_LEFT = 0.5;
    private static final double MAX_LEFT = 1.0;
    private static final double MIN_RIGHT = 0.5;
    private static final double MAX_RIGHT = 1.5;

    // Dynamics
    private static final double FREQUENCY_HZ = 1.1;  // cycles per second
    private static final double POS_GAIN = 0.18;     // blocks/tick per block error (P on position)
    private static final double SMOOTHING = 0.35;    // 0..1, higher snaps faster
    private static final double MAX_LAT_VEL = 0.24;  // hard cap on lateral speed (blocks/tick)
    private static final double AMP_PER_LEVEL = 0.12; // scales target offsets per amplifier

    // Persistent data keys
    private static final String KEY_BASE = "smokeleaf.sleepy.";
    private static final String KEY_AX = KEY_BASE + "ax";
    private static final String KEY_AZ = KEY_BASE + "az";
    private static final String KEY_LS = KEY_BASE + "lastSign";
    private static final String KEY_AMP = KEY_BASE + "amp";

    public SleepyEffect(MobEffectCategory category, int color) {
        super(category, color);
        this.addAttributeModifier(
                Attributes.MOVEMENT_SPEED,
                "6a1f5c3e-2b7d-4e9a-9c41-5d8f0e3b7a21",
                -0.1D,
                AttributeModifier.Operation.MULTIPLY_TOTAL
        );
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity instanceof Player player) {
            CompoundTag tag = player.getPersistentData();

            // Only sway when intentionally moving forward
            if (player.zza > 0) {
                double yawRad = Math.toRadians(player.getYRot());

                // Correct unit vectors
                double fwdX = -Math.sin(yawRad);
                double fwdZ =  Math.cos(yawRad);
                double rightX =  Math.cos(yawRad);
                double rightZ =  Math.sin(yawRad);

                // Anchor follows only forward component of current velocity
                double dt = 1.0 / 20.0;
                Vec3 v = player.getDeltaMovement();
                double forwardSpeed = v.x * fwdX + v.z * fwdZ;

                double anchorX = tag.contains(KEY_AX) ? tag.getDouble(KEY_AX) : player.getX();
                double anchorZ = tag.contains(KEY_AZ) ? tag.getDouble(KEY_AZ) : player.getZ();
                anchorX += fwdX * forwardSpeed * dt;
                anchorZ += fwdZ * forwardSpeed * dt;
                tag.putDouble(KEY_AX, anchorX);
                tag.putDouble(KEY_AZ, anchorZ);

                // Current lateral offset vs. anchor
                double lateralOffset = (player.getX() - anchorX) * rightX + (player.getZ() - anchorZ) * rightZ;

                // Sinusoidal desired offset with per-half-cycle amplitude
                double omega = 2.0 * Math.PI * FREQUENCY_HZ / 20.0;
                double phase = ((player.getId() & 255) / 255.0) * (2.0 * Math.PI);
                double s = Math.sin(omega * player.tickCount + phase);
                int sign = s >= 0.0 ? 1 : -1;

                int lastSign = tag.contains(KEY_LS) ? tag.getInt(KEY_LS) : sign;
                double ampScale = 1.0 + AMP_PER_LEVEL * Math.max(0, amplifier);

                double amp = tag.contains(KEY_AMP)
                        ? tag.getDouble(KEY_AMP)
                        : pickAmplitude(player, sign >= 0, ampScale);

                if (sign != lastSign) {
                    // New half-cycle -> pick a fresh amplitude within the side's range
                    amp = pickAmplitude(player, sign >= 0, ampScale);
                    tag.putInt(KEY_LS, sign);
                    tag.putDouble(KEY_AMP, amp);
                }

                double desiredOffset = s * amp;

                // Position -> velocity control (smooth and bounded)
                double error = desiredOffset - lateralOffset;
                double desiredLatVel = clamp(error * POS_GAIN, -MAX_LAT_VEL, MAX_LAT_VEL);
                if (player.isShiftKeyDown()) desiredLatVel *= 0.5;

                double currentLatVel = v.x * rightX + v.z * rightZ;
                double delta = (desiredLatVel - currentLatVel) * SMOOTHING;

                player.setDeltaMovement(v.add(rightX * delta, 0.0, rightZ * delta));
            } else {
                // Reset anchor when not moving forward to avoid drift
                tag.putDouble(KEY_AX, entity.getX());
                tag.putDouble(KEY_AZ, entity.getZ());
                tag.putInt(KEY_LS, 0);
            }
        }
        return;
    }

    private static double pickAmplitude(Player p, boolean rightSide, double scale) {
        double min = rightSide ? MIN_RIGHT : MIN_LEFT;
        double max = rightSide ? MAX_RIGHT : MAX_LEFT;
        double a = min + p.getRandom().nextDouble() * (max - min);
        return a * scale;
    }

    private static double clamp(double v, double lo, double hi) {
        return Math.max(lo, Math.min(hi, v));
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }
}
