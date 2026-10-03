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

    vec2 ghostOff = vec2(sin(secs * 1.3), cos(secs * 1.1)) * 0.02 * Intensity;
    vec4 base = texture(DiffuseSampler, uv);
    vec4 ghost = texture(DiffuseSampler, uv + ghostOff);
    vec4 color = mix(base, ghost, 0.4 * Intensity);

    vec3 tint = vec3(0.75, 1.0, 0.68);
    color.rgb = mix(color.rgb, color.rgb * tint, 0.55 * Intensity);

    float vig = smoothstep(0.25, 1.0, dist);
    color.rgb *= (1.0 - vig * 0.4 * Intensity);

    fragColor = vec4(color.rgb, 1.0);
}
