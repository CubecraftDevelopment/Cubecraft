package net.cubecraft.world.worldGen.noise.templete;

import net.cubecraft.world.worldGen.noise.Noise;

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
    public double getValue(double x, double y) {
        return this.synth.getValue(x +xo, y +yo)+ho;
    }
}
