package net.cubecraft.util.thread;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public final class EventLoop extends Thread implements Executor {
    private final BlockingQueue<Runnable> commandQueue = new LinkedBlockingQueue<>();
    private final Runnable startupListener;
    private final AtomicBoolean running = new AtomicBoolean(true);

    public EventLoop(Runnable startupListener) {
        this.startupListener = startupListener;
    }

    @Override
    public void execute(@NotNull Runnable runnable) {
        this.commandQueue.add(runnable);
    }

    @Override
    public void run() {
        if(this.startupListener != null) {
            this.startupListener.run();
        }
        while (this.running.get()) {
            try {
                this.commandQueue.take().run();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }catch (IllegalMonitorStateException ignored) {
            }
        }
    }

    public void shutdown() {
        this.running.set(false);
        this.stop();
    }
}
