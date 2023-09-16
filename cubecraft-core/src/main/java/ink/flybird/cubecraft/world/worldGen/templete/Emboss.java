package ink.flybird.cubecraft.world.worldGen.templete;

import ink.flybird.cubecraft.world.worldGen.noiseGenerator.Synth;

public class Emboss
extends Synth {
    private final Synth synth;

    public Emboss(Synth synth) {
        this.synth = synth;
    }

    @Override
    public double getValue(double x, double y) {
        return this.synth.getValue(x, y) - this.synth.getValue(x + 1.0, y + 1.0);
    }
}
