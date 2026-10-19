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

    // Motion-trail smear radiating from screen centre.
    vec3 trailSum = vec3(0.0);
    float wSum = 0.0;
    for (int i = 1; i <= 5; i++) {
        float t = float(i);
        vec2 offset = centered * 0.01 * t * Intensity;
        float w = 1.0 / t;
        trailSum += texture(InSampler, uv - offset).rgb * w;
        wSum += w;
    }
    trailSum /= wSum;
    color.rgb = mix(color.rgb, trailSum, 0.55 * Intensity);

    // Slowly cycling colourful tint on the trailing edges.
    float cyc = secs * 0.6;
    vec3 tint = vec3(0.6 + 0.4 * sin(cyc), 0.6 + 0.4 * sin(cyc + 2.0), 0.6 + 0.4 * sin(cyc + 4.0));
    color.rgb = mix(color.rgb, color.rgb * tint, 0.35 * Intensity);

    float vig = smoothstep(0.25, 1.0, dist);
    color.rgb *= (1.0 - vig * 0.4 * Intensity);

    fragColor = vec4(color.rgb, 1.0);
}
