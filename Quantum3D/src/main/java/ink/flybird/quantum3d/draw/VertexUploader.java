package ink.flybird.quantum3d.draw;

import ink.flybird.quantum3d.BufferAllocation;

import java.nio.DoubleBuffer;
import java.util.concurrent.atomic.AtomicInteger;

public interface VertexUploader {
    DoubleBuffer VERTEX_ARRAY_SWAP = BufferAllocation.allocDoubleBuffer(131072);
    DoubleBuffer TEX_COORD_ARRAY_SWAP = BufferAllocation.allocDoubleBuffer(131072);
    DoubleBuffer COLOR_ARRAY_SWAP = BufferAllocation.allocDoubleBuffer(131072);
    DoubleBuffer NORMAL_ARRAY_SWAP = BufferAllocation.allocDoubleBuffer(131072);
    DoubleBuffer RAW_ARRAY_SWAP = BufferAllocation.allocDoubleBuffer(524288);

    AtomicInteger UPLOAD_COUNTER = new AtomicInteger(0);


    static int getUploadedCount() {
        return UPLOAD_COUNTER.get();
    }

    static void resetUploadCount() {
        UPLOAD_COUNTER.set(0);
    }
}