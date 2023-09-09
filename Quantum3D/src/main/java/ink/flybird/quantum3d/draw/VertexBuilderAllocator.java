package ink.flybird.quantum3d.draw;

import java.util.concurrent.atomic.AtomicInteger;

public interface VertexBuilderAllocator {
    int ARRAY_BUILDER = 0;
    int OFF_HEAP_BUILDER = 1;
    AtomicInteger PREFER_MODE = new AtomicInteger(ARRAY_BUILDER);
    AtomicInteger ALLOCATED_COUNT = new AtomicInteger(0);

    static OffHeapVertexBuilder createOffHeap(int capacity, DrawMode drawMode) {
        ALLOCATED_COUNT.addAndGet(1);
        return new OffHeapVertexBuilder(capacity, drawMode);
    }

    static ArrayVertexBuilder createArray(int capacity, DrawMode drawMode) {
        ALLOCATED_COUNT.addAndGet(1);
        return new ArrayVertexBuilder(capacity, drawMode);
    }


    static OffHeapVertexBuilder createOffHeap(int capacity) {
        return createOffHeap(capacity, DrawMode.QUADS);
    }

    static ArrayVertexBuilder createArray(int capacity) {
        return createArray(capacity, DrawMode.QUADS);
    }


    static VertexBuilder createByType(int capacity, DrawMode drawMode, int type) {
        if (type == OFF_HEAP_BUILDER) {
            return createOffHeap(capacity, drawMode);
        }
        return createArray(capacity, drawMode);
    }

    static VertexBuilder createByType(int capacity, int type) {
        return createByType(capacity, DrawMode.QUADS, type);
    }

    static VertexBuilder createByPrefer(int capacity, DrawMode drawMode) {
        return createByType(capacity, drawMode, PREFER_MODE.get());
    }

    static VertexBuilder createByPrefer(int capacity) {
        return createByType(capacity, DrawMode.QUADS, PREFER_MODE.get());
    }
}
