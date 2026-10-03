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

    vec4 base = texture(DiffuseSampler, uv);

    // Bright-pass bloom: ring-sample around this pixel, keep only bright contributions.
    vec3 bloom = vec3(0.0);
    for (int i = 0; i < 8; i++) {
        float a = float(i) / 8.0 * 6.2831853;
        vec2 offset = vec2(cos(a), sin(a)) * 0.02 * Intensity;
        vec3 s = texture(DiffuseSampler, uv + offset).rgb;
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
