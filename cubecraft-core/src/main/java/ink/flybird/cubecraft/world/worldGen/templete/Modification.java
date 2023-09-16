package ink.flybird.cubecraft.world.worldGen.templete;

import ink.flybird.cubecraft.world.worldGen.noiseGenerator.Synth;

public class Modification extends Synth {
    final double scale;
    final double offset;
    final double scaleVert;
    final double offsetVert;
    final Synth synth;

    public Modification(double scale, double offset, double scaleVert, double offsetVert, Synth synth) {
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
