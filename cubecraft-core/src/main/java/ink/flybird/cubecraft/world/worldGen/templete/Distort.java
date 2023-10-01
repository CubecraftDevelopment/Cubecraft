package ink.flybird.cubecraft.world.worldGen.templete;

import ink.flybird.cubecraft.world.worldGen.noiseGenerator.Noise;

public class Distort
extends Noise {
    private final Noise source;
    private final Noise distort;

    public Distort(Noise source, Noise distort) {
        this.source = source;
        this.distort = distort;
    }

    @Override
    public double getValue(double x, double y) {
        return this.source.getValue(x + this.distort.getValue(x, y), y);
    }
}
