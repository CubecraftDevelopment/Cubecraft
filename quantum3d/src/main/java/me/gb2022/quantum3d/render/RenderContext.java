package me.gb2022.quantum3d.render;

import ink.flybird.fcommon.container.ArrayQueue;
import ink.flybird.fcommon.memory.BufferAllocator;
import me.gb2022.quantum3d.render.command.RenderCall;
import me.gb2022.quantum3d.render.command.RenderCallAllocator;
import me.gb2022.quantum3d.render.texture.SimpleTexture2D;
import me.gb2022.quantum3d.render.texture.TilemapTexture2D;
import me.gb2022.quantum3d.render.vertex.VertexBuilder;
import me.gb2022.quantum3d.render.vertex.VertexBuilderAllocator;
import org.joml.Matrix4d;


public abstract class RenderContext {
    private final ArrayQueue<Runnable> commandQueue = new ArrayQueue<>();

    public final void uploadCommand(Runnable command) {
        this.commandQueue.add(command);
    }

    public void executeAllCommand() {
        for (Runnable cmd : this.commandQueue.pollAll(this.commandQueue.size())) {
            cmd.run();
        }
    }


    public abstract void create();

    public abstract void destroy();

    public abstract SimpleTexture2D texture2d();

    public abstract TilemapTexture2D tilemapTexture2d(int maxImageWidth, int maxImageHeight, int minSampleX, int minSampleY);

    public abstract void clearBuffer();

    public abstract void checkError(String status);

    public abstract void setBufferClearColor(double r, double g, double b);

    public abstract void clearMatrix();

    public abstract void setMatrix(Matrix4d mat);

    public abstract VertexBuilderAllocator newVertexBuilderAllocator(BufferAllocator allocator);

    public abstract void setViewport(int x, int y, int width, int height);

    public abstract void uploadVertexBuilder(VertexBuilder builder);

    public abstract VertexBuilderAllocator newVertexBuilderAllocator();

    public abstract RenderCallAllocator newRenderCallAllocator();

    public abstract RenderCall newRenderCall();

    public abstract void setDepthTest();



    public abstract void checkError();
}