package me.gb2022.quantum3d.render.command;

import me.gb2022.quantum3d.render.vertex.VertexBuilder;

public abstract class VertexBuilderUploader<I extends VertexBuilder> implements RenderCall {
    private final I buffer;

    protected VertexBuilderUploader(I buffer) {
        this.buffer = buffer;
    }

    public abstract void upload(I builder);

    @Override
    public void call() {
        this.upload(this.buffer);
    }
}
