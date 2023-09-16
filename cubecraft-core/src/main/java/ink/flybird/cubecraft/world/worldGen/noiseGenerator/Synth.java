package ink.flybird.cubecraft.world.worldGen.noiseGenerator;

public abstract class Synth {
    public abstract double getValue(double var1, double var3);

    public double[] create(int width, int height) {
        double[] result = new double[width * height];
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                result[x + y * width] = this.getValue(x, y);
            }
        }
        return result;
    }
}
