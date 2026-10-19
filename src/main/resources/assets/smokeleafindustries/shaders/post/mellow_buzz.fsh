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
    float dist = length(centered);
    float secs = Time;

    vec4 color = texture(InSampler, uv);

    // Gentle warm amber tint, barely-there — the lightest tier.
    vec3 tint = vec3(1.05, 1.0, 0.88);
    color.rgb = mix(color.rgb, color.rgb * tint, 0.35 * Intensity);

    // Soft breathing vignette.
    float vig = smoothstep(0.35, 1.0, dist);
    float breathing = sin(secs * 0.4) * 0.05 + 0.95;
    color.rgb *= (1.0 - vig * 0.25 * Intensity * breathing);

    fragColor = vec4(color.rgb, 1.0);
}
