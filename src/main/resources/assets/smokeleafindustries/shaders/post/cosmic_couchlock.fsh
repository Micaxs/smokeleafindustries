#version 330

#moj_import <minecraft:globals.glsl>

uniform sampler2D InSampler;

layout(std140) uniform SamplerInfo {
    vec2 OutSize;
    vec2 InSize;
};

layout(std140) uniform TripConfig {
    float BaseIntensity;
};

// Post-effect uniforms can no longer be set from code each frame, so the animation clock and the
// intensity pulse (formerly driven by StonedShaderManager) are derived from the global game time.
// GameTime is the day fraction (0-1 over 24000 ticks); Time keeps its old meaning of ~1 per second.
#define GAME_TICKS (GameTime * 24000.0)
#define Time (GameTime * 1200.0)
#define Intensity (BaseIntensity * (0.70 + 0.25 * sin(GAME_TICKS * 0.12)))

in vec2 texCoord;

out vec4 fragColor;

#define PI 3.14159265

void main() {
    vec2 uv = texCoord;
    vec2 centered = uv - 0.5;
    float radius = length(centered);
    float angle = atan(centered.y, centered.x);

    // Kaleidoscope fold.
    float segments = 6.0;
    float segAngle = (2.0 * PI) / segments;
    float foldedAngle = abs(mod(angle, segAngle) - segAngle * 0.5);

    // Add a spinning spiral on top of the fold — the most intense tier.
    foldedAngle += radius * 8.0 * Intensity + Time * 0.9;

    vec2 wUv = vec2(cos(foldedAngle), sin(foldedAngle)) * radius + 0.5;
    vec2 finalUv = mix(uv, wUv, clamp(Intensity, 0.0, 1.0));

    float chromaAmt = 0.028 * Intensity;
    vec2 chromaOff = centered * chromaAmt;
    vec4 color;
    color.r = texture(InSampler, finalUv + chromaOff).r;
    color.g = texture(InSampler, finalUv).g;
    color.b = texture(InSampler, finalUv - chromaOff).b;
    color.a = 1.0;

    float cycle = Time * 1.4;
    vec3 tint = vec3(
        0.6 + 0.4 * sin(cycle),
        0.6 + 0.4 * sin(cycle + 2.1),
        0.6 + 0.4 * sin(cycle + 4.2)
    );
    color.rgb = mix(color.rgb, color.rgb * tint, 0.7 * Intensity);

    float vig = smoothstep(0.1, 1.0, radius);
    float strobe = sin(Time * 2.6) * 0.2 + 0.8;
    color.rgb *= (1.0 - vig * 0.65 * Intensity * strobe);

    fragColor = vec4(color.rgb, 1.0);
}
