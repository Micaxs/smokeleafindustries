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

    vec4 base = texture(InSampler, uv);

    // Bright-pass bloom: ring-sample around this pixel, keep only bright contributions.
    vec3 bloom = vec3(0.0);
    for (int i = 0; i < 8; i++) {
        float a = float(i) / 8.0 * 6.2831853;
        vec2 offset = vec2(cos(a), sin(a)) * 0.02 * Intensity;
        vec3 s = texture(InSampler, uv + offset).rgb;
        float bright = max(0.0, dot(s, vec3(0.299, 0.587, 0.114)) - 0.55);
        bloom += s * bright;
    }
    bloom /= 8.0;

    float pulse = sin(secs * 0.8) * 0.15 + 0.85;
    vec3 color = base.rgb + bloom * 1.8 * Intensity * pulse;

    // Warm overexposed tint.
    vec3 tint = vec3(1.08, 1.0, 0.85);
    color = mix(color, color * tint, 0.4 * Intensity);

    float vig = smoothstep(0.3, 1.0, dist);
    color *= (1.0 - vig * 0.3 * Intensity);

    fragColor = vec4(color, 1.0);
}
