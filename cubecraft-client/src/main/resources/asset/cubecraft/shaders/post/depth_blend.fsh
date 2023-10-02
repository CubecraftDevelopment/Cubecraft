#version 450 core

vec4 blendColor(vec4 inputColor,sampler2D frameBuffer){
    float depth = texture(frameBuffer, gl_FragCoord).r;
    float currentDepth = gl_FragCoord.z / gl_FragCoord.w;

    vec4 texColor = texture(texture0, TexCoord);
    if (currentDepth > depth) {
        return mix(inputColor, texColor, inputColor.a);
    } else if (currentDepth > depth) {
        return inputColor;
    } else {
        return mix(texColor, blendColor, textureColor.a);
    }
}