package net.cubecraft.world.worldGen.noise.templete;

import net.cubecraft.world.worldGen.noise.Noise;

public class Modification extends Noise {
    final double scale;
    final double offset;
    final double scaleVert;
    final double offsetVert;
    final Noise synth;

    public Modification(double scale, double offset, double scaleVert, double offsetVert, Noise synth) {
        this.scale = scale;
        this.offset = offset;
        this.scaleVert = scaleVert;
        this.offsetVert = offsetVert;
        this.synth = synth;
    }


    @Override
    public double getValue(double x, double y) {
        return synth.getValue(x *scale+offset, y *scale+offset)*scaleVert+offsetVert;
    }
}
