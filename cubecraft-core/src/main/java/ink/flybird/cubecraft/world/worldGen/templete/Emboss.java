package ink.flybird.cubecraft.world.worldGen.templete;

import ink.flybird.cubecraft.world.worldGen.noiseGenerator.Noise;

public class Emboss
extends Noise {
    private final Noise synth;

    public Emboss(Noise synth) {
        this.synth = synth;
    }

    @Override
    public double getValue(double x, double y) {
        return this.synth.getValue(x, y) - this.synth.getValue(x + 1.0, y + 1.0);
    }
}
