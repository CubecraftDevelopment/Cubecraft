package net.cubecraft.client.render.model.component;

public abstract class Cube {
    private final double x0;
    private final double y0;
    private final double z0;
    private final double x1;
    private final double y1;
    private final double z1;


    protected Cube(double x0, double y0, double z0, double x1, double y1, double z1) {
        this.x0 = x0;
        this.y0 = y0;
        this.z0 = z0;
        this.x1 = x1;
        this.y1 = y1;
        this.z1 = z1;
    }
}
