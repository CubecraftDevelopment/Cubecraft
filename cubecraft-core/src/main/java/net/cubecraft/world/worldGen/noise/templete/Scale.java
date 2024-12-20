package net.cubecraft.world.worldGen.noise.templete;

import net.cubecraft.world.worldGen.noise.Noise;

public class Scale
extends Noise {
    private final Noise synth;
    private final double xScale;
    private final double yScale;

    public Scale(Noise synth, double xScale, double yScale) {
        this.synth = synth;
        this.xScale = 1.0 / xScale;
        this.yScale = 1.0 / yScale;
    }

    @Override
    public double getValue(double x, double y) {
        return this.synth.getValue(x * this.xScale, y * this.yScale);
    }
}
