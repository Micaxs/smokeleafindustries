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

    vec4 color = texture(DiffuseSampler, uv);

    // Gentle warm amber tint, barely-there — the lightest tier.
    vec3 tint = vec3(1.05, 1.0, 0.88);
    color.rgb = mix(color.rgb, color.rgb * tint, 0.35 * Intensity);

    // Soft breathing vignette.
    float vig = smoothstep(0.35, 1.0, dist);
    float breathing = sin(secs * 0.4) * 0.05 + 0.95;
    color.rgb *= (1.0 - vig * 0.25 * Intensity * breathing);

    fragColor = vec4(color.rgb, 1.0);
}
