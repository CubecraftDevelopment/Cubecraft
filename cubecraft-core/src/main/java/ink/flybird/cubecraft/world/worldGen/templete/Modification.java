package ink.flybird.cubecraft.world.worldGen.templete;

import ink.flybird.cubecraft.world.worldGen.noiseGenerator.Noise;

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
    public double getValue(double var1, double var3) {
        return synth.getValue(var1*scale+offset,var3*scale+offset)*scaleVert+offsetVert;
    }
}
