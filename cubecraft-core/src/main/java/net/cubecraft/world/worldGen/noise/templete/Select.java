package net.cubecraft.world.worldGen.noise.templete;

import net.cubecraft.world.worldGen.noise.Noise;

public class Select extends Noise {
    private final Noise high, low, select;

    public Select(Noise high, Noise low, Noise select) {
        this.high = high;
        this.low = low;
        this.select = select;
    }

    @Override
    public double getValue(double x, double y) {
        if (select.getValue(x, y) > 1.0f) {
            return high.getValue(x, y);
        }
        if (select.getValue(x, y) < 0.0f) {
            return low.getValue(x, y);
        }

        var a = low.getValue(x, y);
        var b = high.getValue(x, y);
        var t = select.getValue(x, y);

        return a + (b - a) * t;
    }
}
