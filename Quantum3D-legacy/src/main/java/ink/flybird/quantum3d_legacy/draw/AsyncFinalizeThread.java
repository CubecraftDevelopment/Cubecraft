package ink.flybird.quantum3d_legacy.draw;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public final class AsyncFinalizeThread extends Thread {
    private final List<VertexBuilder> builders = new ArrayList<>();
    private final AtomicBoolean dumping = new AtomicBoolean(false);

    public AsyncFinalizeThread() {
        setDaemon(true);
        setName("AsyncVBFinalizeThread");
    }

    public void append(VertexBuilder builder) {
        builder.free();

        /*
        while (this.dumping.get()) {
            Thread.onSpinWait();
            Thread.yield();
        }
        this.builders.add(builder);

         */
    }

    @Override
    public void run() {
        while (true) {
            if (this.builders.isEmpty()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                continue;
            }

            this.dumping.set(true);
            List<VertexBuilder> builders = new ArrayList<>(this.builders);
            this.builders.clear();
            this.dumping.set(false);

            for (VertexBuilder builder : builders) {
                builder.clear();
                builder.free();
            }
        }
    }
}
