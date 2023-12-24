package me.gb2022.quantum3d.render.pipeline;

import me.gb2022.quantum3d.render.texture.Texture;

public final class RenderCommand implements Runnable {
    private VertexData vertexData;
    private Texture texture;
    private DrawCommand drawCommand;

    @Override
    public void run() {
        if (this.vertexData != null) {
            throw new IllegalArgumentException("vertex data source required!");
        }
        if (this.drawCommand != null) {
            throw new IllegalArgumentException("vertex data source required!");
        }
        this.vertexData.bind();
        if (this.texture != null) {
            this.texture.bind();
        }

        this.drawCommand.draw();
        if (this.texture != null) {
            this.texture.unbind();
        }

        this.vertexData.unbind();
    }

    public RenderCommand vertexData(VertexData vertex) {
        this.vertexData = vertex;
        return this;
    }

    public RenderCommand drawCommand(DrawCommand drawCommand) {
        this.drawCommand = drawCommand;
        return this;
    }

    public RenderCommand texture(Texture texture) {
        this.texture = texture;
        return this;
    }
}
