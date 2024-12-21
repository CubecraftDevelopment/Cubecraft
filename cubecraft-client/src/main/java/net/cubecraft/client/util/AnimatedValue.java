package net.cubecraft.client.util;

public class AnimatedValue {
    private double aniStart;
    private double aniEnd;
    private long aniStartTime;
    private long aniEndTime;
    private double value;
    private boolean active = false;
    private Animator animator;

    public static double cubicInterpolate(double a, double b, float t) {
        // t 的三次方函数
        float t2 = t * t;
        float t3 = t2 * t;

        // 计算平滑插值
        return a + (b - a) * (t3 * (t * (t * 6 - 15) + 10));
    }

    public void update() {
        if (!this.active) {
            return;
        }

        var time = System.currentTimeMillis();

        if (time > this.aniEndTime) {
            this.active = false;
            return;
        }

        var space = (float) (this.aniEndTime - this.aniStartTime);
        var current = time - this.aniStartTime;

        if (current > space) {
            this.active = false;
            return;
        }


        var aspect = (current / space);
        var v = this.animator.get(this.aniStart, this.aniEnd, aspect);

        if (Double.isNaN(v) || Double.isInfinite(v)) {
            this.active = false;
            return;
        }

        this.value = v;
    }

    public void animate(double start, double to, int time, Animator animator) {
        this.aniStart = start;
        this.aniEnd = to;

        var stamp = System.currentTimeMillis();

        this.aniStartTime = stamp;
        this.aniEndTime = stamp + time;

        this.active = true;
        this.animator = animator;
    }

    public void animateTo(double to, int time, Animator animator) {
        this.animate(this.value, to, time, animator);
    }

    public void animateCubicTo(double to, int time) {
        this.animateTo(to, time, AnimatedValue::cubicInterpolate);
    }

    public void animateCubic(double start, double to, int time) {
        this.animate(start, to, time, AnimatedValue::cubicInterpolate);
    }

    public double getValue() {
        return this.value;
    }

    public interface Animator {
        double get(double x, double y, float t);
    }
}
