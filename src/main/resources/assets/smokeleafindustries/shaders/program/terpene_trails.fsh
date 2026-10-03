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

    // Motion-trail smear radiating from screen centre.
    vec3 trailSum = vec3(0.0);
    float wSum = 0.0;
    for (int i = 1; i <= 5; i++) {
        float t = float(i);
        vec2 offset = centered * 0.01 * t * Intensity;
        float w = 1.0 / t;
        trailSum += texture(DiffuseSampler, uv - offset).rgb * w;
        wSum += w;
    }
    trailSum /= wSum;
    color.rgb = mix(color.rgb, trailSum, 0.55 * Intensity);

    // Slowly cycling colourful tint on the trailing edges.
    float cyc = secs * 0.6;
    vec3 tint = vec3(0.6 + 0.4 * sin(cyc), 0.6 + 0.4 * sin(cyc + 2.0), 0.6 + 0.4 * sin(cyc + 4.0));
    color.rgb = mix(color.rgb, color.rgb * tint, 0.35 * Intensity);

    float vig = smoothstep(0.25, 1.0, dist);
    color.rgb *= (1.0 - vig * 0.4 * Intensity);

    fragColor = vec4(color.rgb, 1.0);
}
