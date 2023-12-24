package me.gb2022.quantum3d.render;

import me.gb2022.quantum3d.render.vertex.VertexBuilder;

public interface ShapeRenderer {
    static void renderBox(VertexBuilder builder, double x0, double y0, double z0, double x1, double y1, double z1) {
        builder.color(0.5, 0.5, 0.5);
        builder.vertex(x0, y0, z1);
        builder.vertex(x0, y0, z0);
        builder.vertex(x1, y0, z0);
        builder.vertex(x1, y0, z1);
        builder.color(0.6, 0.6, 0.6);
        builder.vertex(x1, y1, z1);
        builder.vertex(x1, y1, z0);
        builder.vertex(x0, y1, z0);
        builder.vertex(x0, y1, z1);
        builder.color(0.7, 0.7, 0.7);
        builder.vertex(x0, y1, z0);
        builder.vertex(x1, y1, z0);
        builder.vertex(x1, y0, z0);
        builder.vertex(x0, y0, z0);
        builder.color(0.8, 0.8, 0.8);
        builder.vertex(x0, y1, z1);
        builder.vertex(x0, y0, z1);
        builder.vertex(x1, y0, z1);
        builder.vertex(x1, y1, z1);
        builder.color(0.9, 0.9, 0.9);
        builder.vertex(x0, y1, z1);
        builder.vertex(x0, y1, z0);
        builder.vertex(x0, y0, z0);
        builder.vertex(x0, y0, z1);
        builder.color(1, 1, 1);
        builder.vertex(x1, y0, z1);
        builder.vertex(x1, y0, z0);
        builder.vertex(x1, y1, z0);
        builder.vertex(x1, y1, z1);
    }
}
