#version 150

uniform sampler2D DiffuseSampler;
uniform float Time;
uniform float Intensity;
uniform vec2 OutSize;

in vec2 texCoord;

out vec4 fragColor;

#define PI 3.14159265

void main() {
    vec2 uv = texCoord;
    vec2 centered = uv - 0.5;
    float radius = length(centered);
    float angle = atan(centered.y, centered.x) + Time * 0.15;

    float segments = 8.0;
    float segAngle = (2.0 * PI) / segments;
    angle = mod(angle, segAngle);
    angle = abs(angle - segAngle * 0.5);

    vec2 kUv = vec2(cos(angle), sin(angle)) * radius + 0.5;
    vec2 finalUv = mix(uv, kUv, clamp(Intensity, 0.0, 1.0));

    vec4 color = texture(DiffuseSampler, finalUv);

    float chromaAmt = 0.01 * Intensity;
    vec2 chromaOff = centered * chromaAmt;
    color.r = texture(DiffuseSampler, mix(uv, kUv + chromaOff, Intensity)).r;
    color.b = texture(DiffuseSampler, mix(uv, kUv - chromaOff, Intensity)).b;

    vec3 tint = vec3(0.8, 0.95, 1.05);
    color.rgb = mix(color.rgb, color.rgb * tint, 0.5 * Intensity);

    float vig = smoothstep(0.2, 1.0, radius);
    color.rgb *= (1.0 - vig * 0.35 * Intensity);

    fragColor = vec4(color.rgb, 1.0);
}
