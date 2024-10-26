package me.gb2022.quantum3d.render;

import me.gb2022.quantum3d.render.command.RenderCall;
import me.gb2022.quantum3d.render.texture.SimpleTexture2D;
import me.gb2022.quantum3d.render.texture.TilemapTexture2D;
import me.gb2022.quantum3d.render.vertex.VertexBuilder;
import org.joml.Matrix4d;

import java.util.ArrayDeque;


public abstract class RenderContext {
    private final ArrayDeque<Runnable> commandQueue = new ArrayDeque<>();

    public final void uploadCommand(Runnable command) {
        this.commandQueue.add(command);
    }

    public void executeAllCommand() {
        for (Runnable cmd : this.commandQueue) {
            cmd.run();
        }
        this.commandQueue.clear();
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

    public abstract void setViewport(int x, int y, int width, int height);

    public abstract void uploadVertexBuilder(VertexBuilder builder);

    public abstract RenderCall newRenderCall();

    public abstract void setDepthTest();



    public abstract void checkError();
}