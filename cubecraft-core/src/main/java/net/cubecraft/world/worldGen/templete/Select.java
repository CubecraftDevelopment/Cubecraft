package net.cubecraft.world.worldGen.templete;

import net.cubecraft.world.worldGen.noiseGenerator.Noise;
import ink.flybird.fcommon.math.MathHelper;

public class Select extends Noise {
    private final Noise high,low,select;

    public Select(Noise high, Noise low, Noise select) {
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
