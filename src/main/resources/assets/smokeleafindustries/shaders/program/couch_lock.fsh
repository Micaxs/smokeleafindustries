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

    // Slow zoom pulse toward the centre.
    float zoom = 1.0 + sin(secs * 0.35) * 0.012 * Intensity;
    vec2 zUv = centered * zoom + 0.5;

    float chromaAmt = 0.006 * Intensity;
    vec2 chromaOff = normalize(centered + 0.0001) * chromaAmt;
    vec4 color;
    color.r = texture(DiffuseSampler, zUv + chromaOff).r;
    color.g = texture(DiffuseSampler, zUv).g;
    color.b = texture(DiffuseSampler, zUv - chromaOff).b;
    color.a = 1.0;

    vec3 tint = vec3(0.95, 1.0, 0.9);
    color.rgb = mix(color.rgb, color.rgb * tint, 0.4 * Intensity);

    float vig = smoothstep(0.3, 1.0, dist);
    color.rgb *= (1.0 - vig * 0.35 * Intensity);

    fragColor = vec4(color.rgb, 1.0);
}
