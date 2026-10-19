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
    float edge = min(min(uv.x, 1.0 - uv.x), min(uv.y, 1.0 - uv.y));
    float edgeMask = 1.0 - smoothstep(0.0, 0.72, edge);
    float secs = Time;

    // Stronger blur in the outer screen to sell the trippy, sensory effect.
    float blurAmt = smoothstep(0.08, 0.78, dist) * 0.048 * Intensity;
    vec4 color = vec4(0.0);
    float wSum = 0.0;
    for (int i = 0; i < 10; i++) {
        float t = float(i) / 9.0;
        float w = exp(-t * 3.2);
        vec2 sampleUv = uv - centered * blurAmt * (t + 0.22);
        color += texture(InSampler, sampleUv) * w;
        wSum += w;
    }
    color /= wSum;

    // Subtle chromatic drift toward green/yellow periphery.
    float chromaAmt = max(dist - 0.04, 0.0) * 0.032 * Intensity;
    vec2 chromaOff = centered * chromaAmt;
    float rCh = texture(InSampler, uv + chromaOff).r;
    float bCh = texture(InSampler, uv - chromaOff).b;
    color.r = mix(color.r, rCh, 0.80 * Intensity);
    color.b = mix(color.b, bCh, 0.80 * Intensity);

    // Strong green-yellow tint with a slow cycling shift.
    float tintCycle = sin(secs * 0.4) * 0.5 + 0.5;
    vec3 tintA = vec3(0.92, 1.0, 0.75);
    vec3 tintB = vec3(0.72, 1.0, 0.55);
    vec3 tint = mix(tintA, tintB, tintCycle);
    float tintFade = (1.0 - smoothstep(0.0, 0.7, dist)) * Intensity;
    color.rgb = mix(color.rgb, color.rgb * tint, tintFade * 0.75);

    // Full-window dark vignette with heavier border coverage.
    float vig = smoothstep(0.10, 0.95, dist + edgeMask * 0.35);
    vig = pow(vig, 0.72);
    float breathing = sin(secs * 0.52) * 0.06 + 0.94;
    color.rgb *= (1.0 - vig * 0.92 * Intensity * breathing);

    // Extra dark edge strips at the top/bottom and rounded screen borders.
    float vertEdge = abs(uv.y - 0.5) * 2.0;
    float lid = smoothstep(0.32, 1.0, vertEdge);
    lid *= (sin(secs * 0.7 + 1.6) * 0.08 + 0.92);
    color.rgb *= (1.0 - lid * 0.64 * Intensity);

    // Keep a faint screen haze so the whole effect reads as a real overlay instead of just a vignette.
    float haze = smoothstep(0.95, 0.0, dist) * 0.18 * Intensity;
    color.rgb += vec3(0.08, 0.16, 0.04) * haze;

    fragColor = vec4(color.rgb, 1.0);
}
