package net.cubecraft.world.worldGen.templete;

import net.cubecraft.world.worldGen.noiseGenerator.Noise;

public class Offset extends Noise {
    private final double xo,yo,ho;
    private final Noise synth;

    public Offset(double xo, double yo, double ho, Noise synth) {
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
