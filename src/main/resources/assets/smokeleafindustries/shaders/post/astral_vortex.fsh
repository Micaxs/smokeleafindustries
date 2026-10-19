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

void main() {
    vec2 uv = texCoord;
    vec2 centered = uv - 0.5;
    float radius = length(centered);
    float angle = atan(centered.y, centered.x);

    angle += radius * 7.0 * Intensity + Time * 0.6;
    vec2 sUv = vec2(cos(angle), sin(angle)) * radius + 0.5;
    vec2 finalUv = mix(uv, sUv, clamp(Intensity, 0.0, 1.0));

    vec4 color = texture(InSampler, finalUv);

    float chromaAmt = 0.018 * Intensity;
    vec2 chromaOff = centered * chromaAmt;
    color.r = texture(InSampler, finalUv + chromaOff).r;
    color.b = texture(InSampler, finalUv - chromaOff).b;

    float cycle = sin(Time * 0.9) * 0.5 + 0.5;
    vec3 tintA = vec3(0.6, 0.3, 1.0);
    vec3 tintB = vec3(0.3, 1.0, 0.6);
    vec3 tint = mix(tintA, tintB, cycle);
    color.rgb = mix(color.rgb, color.rgb * tint, 0.6 * Intensity);

    float vig = smoothstep(0.15, 1.0, radius);
    float pulse = sin(Time * 1.4) * 0.15 + 0.85;
    color.rgb *= (1.0 - vig * 0.55 * Intensity * pulse);

    fragColor = vec4(color.rgb, 1.0);
}
