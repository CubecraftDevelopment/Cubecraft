package net.cubecraft.util;

import org.joml.Vector2d;

public final class Spline {
    private final double[] x;
    private final double[] y;

    public Spline(Vector2d... points) {
        this.x = new double[points.length];
        this.y = new double[points.length];

        for (int i = 0; i < points.length; i++) {
            this.x[i] = points[i].x;
            this.y[i] = points[i].y;
        }
    }

    public double interpolate(double t) {
        int n = x.length;

        // 确保输入的数据点是有序的
        for (int i = 0; i < n - 1; i++) {
            if (t >= x[i] && t <= x[i + 1]) {
                // 使用线性插值公式计算
                double slope = (y[i + 1] - y[i]) / (x[i + 1] - x[i]);
                return y[i] + slope * (t - x[i]);
            }
        }

        throw new IndexOutOfBoundsException(String.valueOf(t));
    }
}
