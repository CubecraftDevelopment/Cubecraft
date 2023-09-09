#version 120

void main() {
    const vec3 fullscreen_triangle_positions[3] =
    vec3[3](vec3(3.0, 1.0, 0.5), vec3(-1.0, 1.0, 0.5), vec3(-1.0, -3.0, 0.5));

    // 计算每个顶点对应屏幕空间下的 UV 坐标
    out_uv = 0.5 * (fullscreen_triangle_positions[gl_VertexIndex].xy + vec2(1.0, 1.0));
    // 该三角形在经过裁剪后会成为两个覆盖屏幕空间的直角三角形
    gl_Position = vec4(fullscreen_triangle_positions[gl_VertexIndex], 1.0);
}
