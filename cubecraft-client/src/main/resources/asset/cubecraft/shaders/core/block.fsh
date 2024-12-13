#version 330 core




layout(position = 0) in vec3 position;
layout(position = 1) in vec3 color;
layout(position = 2) in vec2 texure;


uniform sampler2D texture;

in vec2 textureCoord;

out vec4 fragColor;

uniform mat4 _cam_proj;
uniform mat4 _cam_view_local;

uniform float _vx;
uniform float _vy;
uniform float _vz;


void main() {
    fragColor = texture();

}