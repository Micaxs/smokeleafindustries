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

    vec2 warp = uv + vec2(
        sin(uv.y * 14.0 + secs * 2.2),
        cos(uv.x * 14.0 + secs * 1.8)
    ) * 0.012 * Intensity;

    vec4 color = texture(DiffuseSampler, warp);

    float chromaAmt = 0.01 * Intensity;
    vec2 chromaOff = centered * chromaAmt;
    color.r = mix(color.r, texture(DiffuseSampler, warp + chromaOff).r, 0.6 * Intensity);
    color.b = mix(color.b, texture(DiffuseSampler, warp - chromaOff).b, 0.6 * Intensity);

    vec3 tint = vec3(0.85, 1.0, 0.8);
    color.rgb = mix(color.rgb, color.rgb * tint, 0.45 * Intensity);

    float vig = smoothstep(0.25, 1.0, dist);
    color.rgb *= (1.0 - vig * 0.45 * Intensity);

    fragColor = vec4(color.rgb, 1.0);
}
