package net.cubecraft.util;

import me.gb2022.commons.math.MathHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public interface WorldRandom {
    Map<Thread, Random> TR = new HashMap<>();

    static double xz(long x, long z) {
        synchronized ("a") {
            var r = new Random();

            r.setSeed(MathHelper._rand2(x, z));

            return r.nextDouble();
        }
    }
}
