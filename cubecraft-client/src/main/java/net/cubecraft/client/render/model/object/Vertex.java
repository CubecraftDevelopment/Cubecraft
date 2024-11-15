package net.cubecraft.client.render.model.object;

import me.gb2022.commons.ColorUtil;
import me.gb2022.quantum3d.legacy.draw.LegacyVertexBuilder;
import me.gb2022.quantum3d.render.vertex.VertexBuilder;
import org.joml.Vector2d;
import org.joml.Vector3d;
import org.joml.Vector4d;

public record Vertex(
        Vector3d pos,
        Vector2d texture,
        Vector4d color,
        Vector3d normal
) {


    public static void draw(VertexBuilder builder, float x, float y, float z, float u, float v, int color) {
        draw(builder, x, y, z, u, v, color, 1);
    }

    public static void draw(VertexBuilder builder, float x, float y, float z, float u, float v, int color, double mod) {
        builder.setTextureCoordinate(u, v);
        var c = ColorUtil.int1ToFloat3(color);
        builder.setColor(c[0] * mod, c[1] * mod, c[2] * mod, 1);
        builder.addVertex(x, y, z);
    }

    public static Vertex create(Vector3d pos, Vector2d tex, Vector4d color) {
        return new Vertex(pos, tex, color, new Vector3d(1));
    }

    public static Vertex create(Vector3d pos, Vector2d tex) {
        return new Vertex(pos, tex, new Vector4d(1), new Vector3d(1));
    }

    public static Vertex create(Vector3d pos, Vector4d color) {
        return new Vertex(pos, new Vector2d(0), color, new Vector3d(1));
    }

    public static Vertex create(Vector3d pos, Vector2d tex, Vector3d color) {
        return create(pos, tex, new Vector4d(color, 1));
    }


    public Vertex multiplyColor(Vector4d col) {
        this.color.mul(col);
        return this;
    }

    public void multiplyColor(double col) {
        multiplyColor(new Vector3d(col, col, col));
    }

    public void multiplyColor(Vector3d col) {
        this.color.mul(new Vector4d(col.x, col.y, col.z, 1d));
    }

    public void draw(LegacyVertexBuilder builder) {
        builder.tex(texture);
        builder.color(color);
        builder.normal(normal);
        builder.vertex(pos);
    }

    public void draw(VertexBuilder builder) {
        builder.setTextureCoordinate(this.texture().x, this.texture().y);
        builder.setColor(this.color.x, this.color.y, this.color.z, 1);
        builder.addVertex(this.pos.x(), this.pos.y(), this.pos.z());
    }
}
