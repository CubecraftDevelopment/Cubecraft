package net.cubecraft.client.render.chunk.status;

import net.cubecraft.client.ClientSettingRegistry;
import net.cubecraft.client.render.chunk.TerrainRenderer;

import java.util.ConcurrentModificationException;

@SuppressWarnings("BusyWait")
public final class ChunkStatusHandlerThread extends Thread {
    public static final int ROTATION_SENSITIVE_VALUE = 3;
    public static final int DELAY_INTERVAL = 100;

    private final TerrainRenderer parent;
    boolean running = true;

    private long lastX = 0;
    private long lastY = 0;
    private long lastZ = 0;

    private double lastRotX = Double.MAX_VALUE;
    private double lastRotY = Double.MAX_VALUE;
    private double lastRotZ = Double.MAX_VALUE;
    private long last = 0L;

    private ChunkStatusHandlerThread(TerrainRenderer parent) {
        this.parent = parent;
    }

    public static ChunkStatusHandlerThread create(TerrainRenderer parent) {
        ChunkStatusHandlerThread daemon = new ChunkStatusHandlerThread(parent);
        daemon.setDaemon(true);
        daemon.setName("ChunkStatusHandler#%d".formatted(parent.hashCode()));
        daemon.setPriority(1);
        return daemon;
    }

    @Override
    public void run() {
        this.checkPosition();
        this.parent.getStatusCache().processUpdate();
        while (this.running) {
            if (System.currentTimeMillis() - last < DELAY_INTERVAL) {
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                Thread.yield();
            }
            this.process();
            Thread.yield();
        }
    }

    public void process() {
        this.last = System.currentTimeMillis();
        try {
            if (!this.parent.getRequestQueue().isEmpty()) {
                var it = this.parent.getRequestQueue().iterator();
                while (it.hasNext()) {
                    var result = it.next();

                    if (result == null) {
                        it.remove();
                        continue;
                    }

                    if (this.parent.isChunkOutOfRange(result.getX(), result.getY(), result.getZ(), 0)) {
                       it.remove();
                    }
                }
            }

            if (!this.parent.getResultQueue().isEmpty()) {
                var it = this.parent.getResultQueue().iterator();
                while (it.hasNext()) {
                    var result = it.next();

                    if (result == null) {
                        it.remove();
                        continue;
                    }

                    if (!this.parent.isChunkOutOfRange(result.getPos())) {
                        continue;
                    }

                    it.remove();

                    for (var n = 0; n < result.getLayers().length; n++) {
                        result.freeLayer(n);
                    }
                }
            }
        } catch (ConcurrentModificationException ignored) {
        }
        if (this.checkPosition() || this.needRotationCheck()) {
            this.parent.getStatusCache().processUpdate();
        }
    }

    public boolean checkPosition() {
        var camera = this.parent.getCamera();

        if (camera == null) {
            return false;
        }

        var x = (int) (camera.getPosition().x) >> 4;
        var y = (int) (camera.getPosition().y) >> 4;
        var z = (int) (camera.getPosition().z) >> 4;

        if (ClientSettingRegistry.FORCE_REBUILD_NEAREST_CHUNK.getValue()) {
            //this.parent.setUpdate(x, y, z, true);
        }

        boolean check = false;

        if (x != this.lastX) {
            this.lastX = x;
            check = true;
        }
        if (y != this.lastY) {
            this.lastY = y;
            check = true;
        }
        if (z != this.lastZ) {
            this.lastZ = z;
            check = true;
        }
        if (check) {
            this.parent.getStatusCache().moveTo(x, y, z);
        }

        return check;
    }

    public boolean needRotationCheck() {
        var camera = this.parent.getCamera();

        if (camera == null) {
            return false;
        }

        double xr = camera.getRotation().x;
        double yr = camera.getRotation().y;
        double zr = camera.getRotation().z;

        if (Math.abs(xr - this.lastRotX) > ROTATION_SENSITIVE_VALUE) {
            this.lastRotX = xr;
            return true;
        }
        if (Math.abs(yr - this.lastRotY) > ROTATION_SENSITIVE_VALUE) {
            this.lastRotY = yr;
            return true;
        }
        if (Math.abs(zr - this.lastRotZ) > ROTATION_SENSITIVE_VALUE) {
            this.lastRotZ = zr;
            return true;
        }

        return false;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }
}
