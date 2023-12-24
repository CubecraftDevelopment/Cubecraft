package me.gb2022.quantum3d.render.command;

public interface RenderCall {
    void call();

    void upload(Runnable command);

    void allocate();

    void free();
}
