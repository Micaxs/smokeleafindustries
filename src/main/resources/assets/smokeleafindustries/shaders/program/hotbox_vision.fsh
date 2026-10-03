#version 150

uniform sampler2D DiffuseSampler;
uniform float Time;
uniform float Intensity;
uniform vec2 OutSize;

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
        color += texture(DiffuseSampler, sampleUv) * w;
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
