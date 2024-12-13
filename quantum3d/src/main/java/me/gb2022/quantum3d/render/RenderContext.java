package me.gb2022.quantum3d.render;

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

    public abstract void clearBuffer();

    public abstract void checkError(String status);

    public abstract void checkError();
}