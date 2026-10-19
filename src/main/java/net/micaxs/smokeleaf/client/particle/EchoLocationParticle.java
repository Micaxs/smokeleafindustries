// Java
package net.micaxs.smokeleaf.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Mth;
import net.minecraft.core.particles.SimpleParticleType;

public class EchoLocationParticle extends SingleQuadParticle {

    private final SpriteSet sprites;
    private final float baseSize;

    protected EchoLocationParticle(ClientLevel level, double x, double y, double z,
                                   double vx, double vy, double vz, SpriteSet sprites) {
        super(level, x, y, z, vx, vy, vz, sprites.first());
        this.sprites = sprites;
        this.gravity = 0.0f;
        this.friction = 0.86f;
        this.lifetime = 10 + this.random.nextInt(8);
        this.baseSize = 0.08f + this.random.nextFloat() * 0.04f;
        this.quadSize = baseSize;

        // horizontal outward motion; y is very small to keep it near source
        this.xd = vx;
        this.yd = vy * 0.05;
        this.zd = vz;

        // soft cyan/teal color
        this.rCol = 0.55f + this.random.nextFloat() * 0.1f;
        this.gCol = 0.9f;
        this.bCol = 1.0f;
        this.alpha = 0.9f;

        this.setSpriteFromAge(sprites);
    }

    @Override
    public void tick() {
        super.tick();
        this.setSpriteFromAge(this.sprites);

        // Expand ring over time
        float t = (float) this.age / (float) this.lifetime;
        this.quadSize = baseSize * (1.0f + 2.2f * t);

        // Fade out
        this.alpha = 0.95f * (1.0f - t);

        // Gentle upward float
        this.yd += 0.002;

        // Slight damping to stabilize motion
        this.xd *= 0.96;
        this.zd *= 0.96;
    }

    @Override
    protected SingleQuadParticle.Layer getLayer() {
        return SingleQuadParticle.Layer.TRANSLUCENT;
    }

    // Factory
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level,
                                       double x, double y, double z, double vx, double vy, double vz, RandomSource random) {
            return new EchoLocationParticle(level, x, y, z, vx, vy, vz, sprites);
        }
    }
}
