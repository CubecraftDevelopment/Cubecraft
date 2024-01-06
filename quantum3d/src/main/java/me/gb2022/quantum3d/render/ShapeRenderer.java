package me.gb2022.quantum3d.render;

import me.gb2022.quantum3d.render.vertex.VertexBuilder;

public interface ShapeRenderer {
    static void renderBox(VertexBuilder builder, double x0, double y0, double z0, double x1, double y1, double z1) {
        builder.setColor(0.5, 0.5, 0.5);
        builder.addVertex(x0, y0, z1);
        builder.addVertex(x0, y0, z0);
        builder.addVertex(x1, y0, z0);
        builder.addVertex(x1, y0, z1);
        builder.setColor(0.6, 0.6, 0.6);
        builder.addVertex(x1, y1, z1);
        builder.addVertex(x1, y1, z0);
        builder.addVertex(x0, y1, z0);
        builder.addVertex(x0, y1, z1);
        builder.setColor(0.7, 0.7, 0.7);
        builder.addVertex(x0, y1, z0);
        builder.addVertex(x1, y1, z0);
        builder.addVertex(x1, y0, z0);
        builder.addVertex(x0, y0, z0);
        builder.setColor(0.8, 0.8, 0.8);
        builder.addVertex(x0, y1, z1);
        builder.addVertex(x0, y0, z1);
        builder.addVertex(x1, y0, z1);
        builder.addVertex(x1, y1, z1);
        builder.setColor(0.9, 0.9, 0.9);
        builder.addVertex(x0, y1, z1);
        builder.addVertex(x0, y1, z0);
        builder.addVertex(x0, y0, z0);
        builder.addVertex(x0, y0, z1);
        builder.setColor(1, 1, 1);
        builder.addVertex(x1, y0, z1);
        builder.addVertex(x1, y0, z0);
        builder.addVertex(x1, y1, z0);
        builder.addVertex(x1, y1, z1);
    }
}
