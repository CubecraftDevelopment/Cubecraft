package me.gb2022.quantum3d.render.command;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * The abstract class RenderCallAllocator provides a mechanism for allocating and freeing RenderCall instances.
 * Subclasses of this class should implement the create method to instantiate specific RenderCall implementations.
 */
public abstract class RenderCallAllocator {

    /**
     * An AtomicInteger to keep track of the number of allocated RenderCalls.
     */
    public final AtomicInteger counter = new AtomicInteger();

    /**
     * Allocates a new RenderCall instance with specified parameters.
     *
     * @return The allocated RenderCall instance.
     */
    public final RenderCall allocate() {
        this.counter.incrementAndGet();
        RenderCall call = this.create();
        call.allocate();
        return call;
    }

    /**
     * Frees the allocated resources of a RenderCall instance.
     *
     * @param builder The RenderCall instance to free.
     */
    public final void free(RenderCall builder) {
        this.counter.decrementAndGet();
        builder.free();
    }

    /**
     * Creates a new RenderCall instance with specified parameters. Subclasses should implement this method.
     *
     * @return The created RenderCall instance.
     */
    public abstract RenderCall create();
}
