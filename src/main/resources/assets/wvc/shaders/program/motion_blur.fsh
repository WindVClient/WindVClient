#version 120

uniform sampler2D DiffuseSampler;
uniform sampler2D PrevSampler;

varying vec2 texCoord;

uniform float Weight = 1.0;

void main() {
    vec4 CurrTexel = texture2D(DiffuseSampler, texCoord);
    vec4 PrevTexel = texture2D(PrevSampler, texCoord);

    // 現在のフレームと前フレームの色を完璧に補間（Mix）して出力する
    gl_FragColor = vec4(mix(PrevTexel.rgb, CurrTexel.rgb, Weight), 1.0);
}
