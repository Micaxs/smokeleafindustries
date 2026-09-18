#version 150

uniform sampler2D DiffuseSampler;
uniform float Time;
uniform float Intensity;
uniform vec2 OutSize;

in vec2 texCoord;

out vec4 fragColor;

float hash(vec2 p) {
    return fract(sin(dot(p, vec2(12.9898, 78.233))) * 43758.5453);
}

void main() {
    vec2 uv = texCoord;
    vec2 centered = uv - 0.5;
    float dist = length(centered);
    float secs = Time;

    // Occasional horizontal glitch-line displacement.
    float lineNoise = hash(vec2(floor(uv.y * 40.0), floor(secs * 6.0)));
    float glitch = step(0.92, lineNoise) * (hash(vec2(secs, uv.y)) - 0.5) * 0.06 * Intensity;
    vec2 gUv = vec2(uv.x + glitch, uv.y);

    vec4 color = texture(DiffuseSampler, gUv);

    // Static noise speckle overlay.
    float n = hash(uv * OutSize.xy * 0.5 + secs * 60.0);
    color.rgb += (n - 0.5) * 0.12 * Intensity;

    // Slight RGB jitter.
    float chromaAmt = 0.006 * Intensity;
    color.r = mix(color.r, texture(DiffuseSampler, gUv + vec2(chromaAmt, 0.0)).r, 0.5);
    color.b = mix(color.b, texture(DiffuseSampler, gUv - vec2(chromaAmt, 0.0)).b, 0.5);

    float vig = smoothstep(0.3, 1.0, dist);
    color.rgb *= (1.0 - vig * 0.35 * Intensity);

    fragColor = vec4(color.rgb, 1.0);
}
