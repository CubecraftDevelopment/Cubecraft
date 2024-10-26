package net.cubecraft.world.worldGen.noise;

import java.util.Random;

public class RandomNoise extends Noise{
    private Random random;

    public RandomNoise(Random random) {
        this.random = random;
    }

    @Override
    public double getValue(double x, double y) {
        String input = String.format("%.6f,%.6f", x, y);
        int hashCode = input.hashCode();
        return (hashCode & 0x7FFFFFFF) / (double) Integer.MAX_VALUE;
    }
}
