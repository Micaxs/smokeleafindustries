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

    // Thick smoke blur — heavier and un-warped, feels like a hazy hotboxed room.
    float blurAmt = 0.09 * Intensity;
    vec4 color = vec4(0.0);
    float wSum = 0.0;
    for (int i = 0; i < 10; i++) {
        float t = float(i) / 9.0;
        float w = exp(-t * 2.2);
        vec2 sampleUv = uv - centered * blurAmt * (t + 0.3);
        color += texture(InSampler, sampleUv) * w;
        wSum += w;
    }
    color /= wSum;

    // Slow-drifting smoke haze layer.
    float smoke = sin(uv.x * 6.0 + secs * 0.5) * 0.5 + sin(uv.y * 5.0 - secs * 0.4) * 0.5;
    smoke = smoke * 0.5 + 0.5;
    vec3 smokeColor = vec3(0.55, 0.48, 0.4);
    color.rgb = mix(color.rgb, smokeColor, smoke * 0.28 * Intensity);

    float vig = smoothstep(0.2, 1.0, dist);
    color.rgb *= (1.0 - vig * 0.4 * Intensity);

    fragColor = vec4(color.rgb, 1.0);
}
