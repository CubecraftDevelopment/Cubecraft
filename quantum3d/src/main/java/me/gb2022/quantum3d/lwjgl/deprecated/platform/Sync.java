//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package me.gb2022.quantum3d.lwjgl.deprecated.platform;


import me.gb2022.quantum3d.lwjgl.deprecated.platform.RunningAvg;
import org.lwjgl.glfw.GLFW;

public class Sync {
    private static final long NANOS_IN_SECOND = 1000000000L;
    private static final RunningAvg sleepDurations = new RunningAvg(10);
    private static final RunningAvg yieldDurations = new RunningAvg(10);
    private static long nextFrame = 0L;
    private static boolean initialised = false;

    Sync() {
    }

    public static void sync(int fps) {
        if (fps > 0) {
            if (!initialised) {
                initialise();
            }

            try {
                long t0;
                long t1;
                for (t0 = getTime(); nextFrame - t0 > sleepDurations.avg(); t0 = t1) {
                    Thread.sleep(1L);
                    sleepDurations.add((t1 = getTime()) - t0);
                }

                sleepDurations.dampenForLowResTicker();

                for (t0 = getTime(); nextFrame - t0 > yieldDurations.avg(); t0 = t1) {
                    Thread.yield();
                    yieldDurations.add((t1 = getTime()) - t0);
                }
            } catch (InterruptedException ignored) {
            }

            nextFrame = Math.max(nextFrame + 1000000000L / (long) fps, getTime());
        }
    }

    private static void initialise() {
        initialised = true;
        sleepDurations.init(1000000L);
        yieldDurations.init((int) ((double) (-(getTime() - getTime())) * 1.333));
        nextFrame = getTime();
        String osName = System.getProperty("os.name");
        if (osName.startsWith("Win")) {
            Thread timerAccuracyThread = new Thread(() -> {
                try {
                    Thread.sleep(Long.MAX_VALUE);
                } catch (Exception ignored) {
                }
            });
            timerAccuracyThread.setName("LWJGL Timer");
            timerAccuracyThread.setDaemon(true);
            timerAccuracyThread.start();
        }

    }

    private static long getTime() {
        return (long) (GLFW.glfwGetTime() * 1000 * 1000000000L / 1000L);
    }
}
