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
    float radius = length(centered);
    float angle = atan(centered.y, centered.x);

    angle += radius * 7.0 * Intensity + Time * 0.6;
    vec2 sUv = vec2(cos(angle), sin(angle)) * radius + 0.5;
    vec2 finalUv = mix(uv, sUv, clamp(Intensity, 0.0, 1.0));

    vec4 color = texture(DiffuseSampler, finalUv);

    float chromaAmt = 0.018 * Intensity;
    vec2 chromaOff = centered * chromaAmt;
    color.r = texture(DiffuseSampler, finalUv + chromaOff).r;
    color.b = texture(DiffuseSampler, finalUv - chromaOff).b;

    float cycle = sin(Time * 0.9) * 0.5 + 0.5;
    vec3 tintA = vec3(0.6, 0.3, 1.0);
    vec3 tintB = vec3(0.3, 1.0, 0.6);
    vec3 tint = mix(tintA, tintB, cycle);
    color.rgb = mix(color.rgb, color.rgb * tint, 0.6 * Intensity);

    float vig = smoothstep(0.15, 1.0, radius);
    float pulse = sin(Time * 1.4) * 0.15 + 0.85;
    color.rgb *= (1.0 - vig * 0.55 * Intensity * pulse);

    fragColor = vec4(color.rgb, 1.0);
}
