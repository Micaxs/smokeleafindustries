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

    float blurAmt = smoothstep(0.05, 0.7, dist) * 0.07 * Intensity;
    vec4 color = vec4(0.0);
    float wSum = 0.0;
    for (int i = 0; i < 8; i++) {
        float t = float(i) / 7.0;
        float w = exp(-t * 3.0);
        vec2 sampleUv = uv - centered * blurAmt * (t + 0.25);
        color += texture(InSampler, sampleUv) * w;
        wSum += w;
    }
    color /= wSum;

    float tintCycle = sin(secs * 0.5) * 0.5 + 0.5;
    vec3 tintA = vec3(0.88, 1.0, 0.7);
    vec3 tintB = vec3(0.68, 1.0, 0.5);
    vec3 tint = mix(tintA, tintB, tintCycle);
    float tintFade = (1.0 - smoothstep(0.0, 0.7, dist)) * Intensity;
    color.rgb = mix(color.rgb, color.rgb * tint, tintFade * 0.85);

    float vig = smoothstep(0.08, 0.9, dist);
    vig = pow(vig, 0.7);
    float breathing = sin(secs * 0.6) * 0.08 + 0.92;
    color.rgb *= (1.0 - vig * 0.9 * Intensity * breathing);

    fragColor = vec4(color.rgb, 1.0);
}
