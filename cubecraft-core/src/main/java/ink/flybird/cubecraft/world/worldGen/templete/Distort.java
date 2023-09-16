package ink.flybird.cubecraft.world.worldGen.templete;

import ink.flybird.cubecraft.world.worldGen.noiseGenerator.Synth;

public class Distort
extends Synth {
    private final Synth source;
    private final Synth distort;

    public Distort(Synth source, Synth distort) {
        this.source = source;
        this.distort = distort;
    }

    @Override
    public double getValue(double x, double y) {
        return this.source.getValue(x + this.distort.getValue(x, y), y);
    }
}
