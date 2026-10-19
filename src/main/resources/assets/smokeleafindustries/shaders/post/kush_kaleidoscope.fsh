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
    float angle = atan(centered.y, centered.x) + Time * 0.15;

    float segments = 8.0;
    float segAngle = (2.0 * PI) / segments;
    angle = mod(angle, segAngle);
    angle = abs(angle - segAngle * 0.5);

    vec2 kUv = vec2(cos(angle), sin(angle)) * radius + 0.5;
    vec2 finalUv = mix(uv, kUv, clamp(Intensity, 0.0, 1.0));

    vec4 color = texture(InSampler, finalUv);

    float chromaAmt = 0.01 * Intensity;
    vec2 chromaOff = centered * chromaAmt;
    color.r = texture(InSampler, mix(uv, kUv + chromaOff, Intensity)).r;
    color.b = texture(InSampler, mix(uv, kUv - chromaOff, Intensity)).b;

    vec3 tint = vec3(0.8, 0.95, 1.05);
    color.rgb = mix(color.rgb, color.rgb * tint, 0.5 * Intensity);

    float vig = smoothstep(0.2, 1.0, radius);
    color.rgb *= (1.0 - vig * 0.35 * Intensity);

    fragColor = vec4(color.rgb, 1.0);
}
