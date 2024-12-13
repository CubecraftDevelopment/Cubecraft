#version 330 core

layout(position = 0) in vec3 position;
layout(position = 1) in vec3 color;
layout(position = 2) in vec2 texure;

uniform mat4 _cam_proj;
uniform mat4 _cam_view_local;

uniform vec3 _view;

out vec3 fragColor;
out vec2 fragTexCoord;

void main() {
    gl_Position = _cam_view_local * _cam_proj * vec4(position, 1.0);

    fragColor = color;
    fragTexCoord = texCoord;
}