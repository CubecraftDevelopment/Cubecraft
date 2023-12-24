package me.gb2022.quantum3d.lwjgl.vertex;

import me.gb2022.quantum3d.render.vertex.DataFormat;
import me.gb2022.quantum3d.render.vertex.DrawMode;
import me.gb2022.quantum3d.render.vertex.VertexBuilder;

import java.util.Arrays;

public abstract class LocalVertexBuilder extends VertexBuilder {
    protected double[] colorCache;
    protected double[] textureCoordCache;
    protected double[] normalCache;

    public LocalVertexBuilder(
            int capacity,
            DrawMode drawMode,
            DataFormat vertexFormat,
            DataFormat textureFormat,
            DataFormat colorFormat,
            DataFormat normalFormat
    ) {
        super(
                capacity,
                drawMode,
                vertexFormat,
                textureFormat,
                colorFormat,
                normalFormat
        );
        if (textureFormat != null) {
            this.textureCoordCache = new double[textureFormat.getSize()];
            Arrays.fill(this.textureCoordCache, 0);
        }
        if (colorFormat != null) {
            this.colorCache = new double[colorFormat.getSize()];
            Arrays.fill(this.colorCache, 1);
        }
        if (normalFormat != null) {
            this.normalCache = new double[normalFormat.getSize()];
            Arrays.fill(this.normalCache, 1);
        }
    }

    @Override
    public void color(double... data) {
        this.colorCache = data;
    }

    @Override
    public void textureCoord(double... data) {
        this.textureCoordCache = data;
    }

    @Override
    public void normal(double... data) {
        this.normalCache = data;
    }
}
