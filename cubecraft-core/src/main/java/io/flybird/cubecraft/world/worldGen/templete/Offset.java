package io.flybird.cubecraft.world.worldGen.templete;

import io.flybird.cubecraft.world.worldGen.noiseGenerator.Synth;

public class Offset extends Synth {
    private final double xo,yo,ho;
    private final Synth synth;

    public Offset(double xo, double yo, double ho, Synth synth) {
        this.xo = xo;
        this.yo = yo;
        this.ho = ho;
        this.synth = synth;
    }

    @Override
    public double getValue(double var1, double var3) {
        return this.synth.getValue(var1+xo,var3+yo)+ho;
    }
}
