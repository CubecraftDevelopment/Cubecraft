package io.flybird.cubecraft.world.worldGen.templete;

import io.flybird.cubecraft.world.worldGen.noiseGenerator.Synth;
import ink.flybird.fcommon.math.MathHelper;

public class Select extends Synth {
    private final Synth high,low,select;

    public Select(Synth high, Synth low, Synth select) {
        this.high = high;
        this.low = low;
        this.select = select;
    }

    @Override
    public double getValue(double var1, double var3) {
        if(select.getValue(var1, var3)>1.0f){
            return high.getValue(var1, var3);
        }
        if(select.getValue(var1, var3)<0.0f){
            return low.getValue(var1, var3);
        }
        return MathHelper.linearInterpolate(low.getValue(var1, var3),high.getValue(var1, var3),select.getValue(var1, var3));
    }
}
