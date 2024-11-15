package net.cubecraft.util;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class TaskFutureWrapper extends CompletableFuture<Void> {
    private final Runnable task;
    private boolean done = false;

    public TaskFutureWrapper(Runnable task) {
        this.task = task;
    }

    @Override
    public Void get() throws InterruptedException, ExecutionException {
        if (this.done) {
            throw new IllegalStateException("resource has already been loaded");
        }
        this.task.run();
        this.done = true;
        return null;
    }
}
