//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package me.gb2022.quantum3d.util;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

public interface Sync {
    Logger LOGGER = LogManager.getLogger("Sync");
    SyncTimer INSTANCE = new SyncTimer();
    long NANOS_IN_SECOND = 1000000000L;

    static void sync(int fps) {
        INSTANCE.sync(fps);
    }

    private static long getTime() {
        return INSTANCE.getTime();
    }

    class RunningAvg {
        private final long[] slots;
        private int offset;

        public RunningAvg(int slotCount) {
            this.slots = new long[slotCount];
            this.offset = 0;
        }

        public void init(long value) {
            while (this.offset < this.slots.length) {
                this.slots[this.offset++] = value;
            }

        }

        public void add(long value) {
            this.slots[this.offset++ % this.slots.length] = value;
            this.offset %= this.slots.length;
        }

        public long avg() {
            long sum = 0L;

            for (long slot : this.slots) {
                sum += slot;
            }

            return sum / (long) this.slots.length;
        }

        public void dampenForLowResTicker() {
            if (this.avg() > 10000000L) {
                for (int i = 0; i < this.slots.length; ++i) {
                    long[] var10000 = this.slots;
                    var10000[i] = (long) ((float) var10000[i] * 0.9F);
                }
            }

        }
    }

    class SyncTimer {
        private final RunningAvg sleepDurations = new RunningAvg(10);
        private final RunningAvg yieldDurations = new RunningAvg(10);
        private long nextFrame = 0L;
        private boolean initialised = false;

        private SyncTimer() {
        }

        public void sync(int fps) {
            if (fps > 0) {
                if (!this.initialised) {
                    initialise();
                }

                try {
                    long t0;
                    long t1;
                    for (t0 = getTime(); this.nextFrame - t0 > this.sleepDurations.avg(); t0 = t1) {
                        Thread.sleep(1L);
                        this.sleepDurations.add((t1 = getTime()) - t0);
                    }

                    this.sleepDurations.dampenForLowResTicker();

                    for (t0 = getTime(); this.nextFrame - t0 > this.yieldDurations.avg(); t0 = t1) {
                        Thread.yield();
                        Thread.sleep(1L);
                        this.yieldDurations.add((t1 = getTime()) - t0);
                    }
                } catch (InterruptedException e) {
                    LOGGER.catching(e);
                }

                this.nextFrame = Math.max(this.nextFrame + NANOS_IN_SECOND / (long) fps, getTime());
            }
        }

        private void initialise() {
            this.initialised = true;
            this.sleepDurations.init(1000000L);
            this.yieldDurations.init((int) ((double) (-(getTime() - getTime())) * 1.333));
            this.nextFrame = getTime();

            if (System.getProperty("os.name").startsWith("Win")) {
                LOGGER.info("starting TimerAccuracyThread on WindowsPlatform.");

                var thread = new Thread(() -> {
                    try {
                        Thread.sleep(Long.MAX_VALUE);
                    } catch (Exception ignored) {
                    }
                });
                thread.setName("Quantum3D-LWJGLTimer");
                thread.setDaemon(true);
                thread.start();
            }
        }

        private long getTime() {
            return (long) (GLFW.glfwGetTime() * 1000 * NANOS_IN_SECOND / 1000L);
        }
    }
}
