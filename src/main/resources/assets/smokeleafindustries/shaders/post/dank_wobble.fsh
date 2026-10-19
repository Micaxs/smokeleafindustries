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

    vec2 warp = uv + vec2(
        sin(uv.y * 14.0 + secs * 2.2),
        cos(uv.x * 14.0 + secs * 1.8)
    ) * 0.012 * Intensity;

    vec4 color = texture(InSampler, warp);

    float chromaAmt = 0.01 * Intensity;
    vec2 chromaOff = centered * chromaAmt;
    color.r = mix(color.r, texture(InSampler, warp + chromaOff).r, 0.6 * Intensity);
    color.b = mix(color.b, texture(InSampler, warp - chromaOff).b, 0.6 * Intensity);

    vec3 tint = vec3(0.85, 1.0, 0.8);
    color.rgb = mix(color.rgb, color.rgb * tint, 0.45 * Intensity);

    float vig = smoothstep(0.25, 1.0, dist);
    color.rgb *= (1.0 - vig * 0.45 * Intensity);

    fragColor = vec4(color.rgb, 1.0);
}
