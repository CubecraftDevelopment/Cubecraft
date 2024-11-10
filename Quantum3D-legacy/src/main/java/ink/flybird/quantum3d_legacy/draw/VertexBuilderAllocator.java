package ink.flybird.quantum3d_legacy.draw;

import java.util.concurrent.atomic.AtomicInteger;

public interface VertexBuilderAllocator {
    int ARRAY_BUILDER = 0;
    int OFF_HEAP_BUILDER = 1;
    AtomicInteger PREFER_MODE = new AtomicInteger(OFF_HEAP_BUILDER);
    AtomicInteger ALLOCATED_COUNT = new AtomicInteger(0);

    static OffHeapVertexBuilder createOffHeap(int capacity, DrawMode drawMode) {
        return new OffHeapVertexBuilder(capacity, drawMode);
    }

    static OffHeapVertexBuilder createOffHeap(int capacity) {
        return createOffHeap(capacity, DrawMode.QUADS);
    }

    static LegacyVertexBuilder createByType(int capacity, DrawMode drawMode, int type) {
        return createOffHeap(capacity, drawMode);
    }

    static LegacyVertexBuilder createByPrefer(int capacity, DrawMode drawMode) {
        return createByType(capacity, drawMode, PREFER_MODE.get());
    }

    static LegacyVertexBuilder createByPrefer(int capacity) {
        return createByType(capacity, DrawMode.QUADS, OFF_HEAP_BUILDER);
    }
}
