package io.flybird.cubecraft.world.worldGen.templete;

import io.flybird.cubecraft.world.worldGen.noiseGenerator.Synth;

public class Scale
extends Synth {
    private final Synth synth;
    private final double xScale;
    private final double yScale;

    public Scale(Synth synth, double xScale, double yScale) {
        this.synth = synth;
        this.xScale = 1.0 / xScale;
        this.yScale = 1.0 / yScale;
    }

    @Override
    public double getValue(double x, double y) {
        return this.synth.getValue(x * this.xScale, y * this.yScale);
    }
}
