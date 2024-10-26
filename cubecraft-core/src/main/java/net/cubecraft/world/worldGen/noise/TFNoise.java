package net.cubecraft.world.worldGen.noise;

public class TFNoise extends Noise {
    private final int seed;
    private final com.terraforged.noise.Noise wrapped;

    public TFNoise(long seed, com.terraforged.noise.Noise wrapped) {
        this.seed = Long.hashCode(seed);
        this.wrapped = wrapped;
    }

    public static Noise wrap(long seed, com.terraforged.noise.Noise n) {
        return new TFNoise(seed, n);
    }

    @Override
    public double getValue(double x, double y) {
        return this.wrapped.getValue(seed, (float) x, (float) y);
    }
}
