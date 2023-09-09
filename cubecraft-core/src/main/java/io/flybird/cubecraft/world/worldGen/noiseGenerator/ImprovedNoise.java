package io.flybird.cubecraft.world.worldGen.noiseGenerator;

import java.util.Random;

public class ImprovedNoise
extends Synth {
    private final int[] p = new int[512];
    public double scale;

    public ImprovedNoise() {
        this(new Random());
    }

    public ImprovedNoise(Random random) {
        setSeed(random);
    }

    public void setSeed(Random random){
        int i;
        for (i = 0; i < 256; ++i) {
            this.p[i] = i;
        }
        for (i = 0; i < 256; ++i) {
            int j = random.nextInt(256 - i) + i;
            int tmp = this.p[i];
            this.p[i] = this.p[j];
            this.p[j] = tmp;
            this.p[i + 256] = this.p[i];
        }
    }

    public double noise(double x, double y, double z) {
        int X = (int)Math.floor(x) & 0xFF;
        int Y = (int)Math.floor(y) & 0xFF;
        int Z = (int)Math.floor(z) & 0xFF;
        x -= Math.floor(x);
        y -= Math.floor(y);
        z -= Math.floor(z);
        double u = this.fade(x);
        double v = this.fade(y);
        double w = this.fade(z);
        int A = this.p[X] + Y;
        int AA = this.p[A] + Z;
        int AB = this.p[A + 1] + Z;
        int B = this.p[X + 1] + Y;
        int BA = this.p[B] + Z;
        int BB = this.p[B + 1] + Z;
        return this.lerp(w, this.lerp(v, this.lerp(u, this.grad(this.p[AA], x, y, z), this.grad(this.p[BA], x - 1.0, y, z)), this.lerp(u, this.grad(this.p[AB], x, y - 1.0, z), this.grad(this.p[BB], x - 1.0, y - 1.0, z))), this.lerp(v, this.lerp(u, this.grad(this.p[AA + 1], x, y, z - 1.0), this.grad(this.p[BA + 1], x - 1.0, y, z - 1.0)), this.lerp(u, this.grad(this.p[AB + 1], x, y - 1.0, z - 1.0), this.grad(this.p[BB + 1], x - 1.0, y - 1.0, z - 1.0))));
    }

    public double fade(double t) {
        return t * t * t * (t * (t * 6.0 - 15.0) + 10.0);
    }

    public double lerp(double t, double a, double b) {
        return a + t * (b - a);
    }

    public double grad(int hash, double x, double y, double z) {
        double u;
        int h = hash & 0xF;
        double d = u = h < 8 ? x : y;
        double v = h < 4 ? y : (h == 12 || h == 14 ? x : z);
        return ((h & 1) == 0 ? u : -u) + ((h & 2) == 0 ? v : -v);
    }

    @Override
    public double getValue(double x, double y) {
        return this.noise(x, y, 0.0);
    }
}
