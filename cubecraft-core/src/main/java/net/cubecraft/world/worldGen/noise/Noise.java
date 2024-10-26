package net.cubecraft.world.worldGen.noise;

public abstract class Noise implements com.terraforged.noise.Noise {
    @Override
    public float getValue(int seed, float x, float y) {
        return (float) getValue(x, y);
    }

    public abstract double getValue(double x, double y);
}
