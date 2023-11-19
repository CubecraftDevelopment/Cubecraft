package ink.flybird.quantum3d_legacy.draw;

import ink.flybird.fcommon.context.LifetimeCounter;
import org.joml.Vector2d;
import org.joml.Vector3d;
import org.joml.Vector4d;

public abstract class VertexBuilder {
    protected final int size;
    protected final DrawMode drawMode;
    protected int count;
    protected float u = 0.0f, v = 0.0f;
    protected float r = 1.0f, g = 1.0f, b = 1.0f, a = 1.0f;
    protected float n = 1.0f, f = 1.0f, l = 1.0f;

    private final LifetimeCounter lifetimeCounter = new LifetimeCounter();

    protected VertexBuilder(int size, DrawMode drawMode) {
        VertexBuilderAllocator.ALLOCATED_COUNT.addAndGet(1);
        this.size = size;
        this.drawMode = drawMode;
        this.lifetimeCounter.allocate();
    }


    //context
    public void begin() {
    }

    public void end() {
    }

    public void free() {
        if(!this.lifetimeCounter.isAllocated()){
            return;
        }
        this.lifetimeCounter.release();
        VertexBuilderAllocator.ALLOCATED_COUNT.addAndGet(-1);
    }


    //upload
    public abstract void uploadPointer();

    public abstract void uploadInterLeaved();

    public abstract void uploadBuffer(int buffer);


    //draw
    public abstract void vertex(double x, double y, double z);

    public void tex(float u, float v) {
        this.u = u;
        this.v = v;
    }

    public void color(float r, float g, float b, float a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    public void normal(float n, float f, float l) {
        this.n = n;
        this.f = f;
        this.l = l;
    }


    public final DrawMode getDrawMode() {
        return drawMode;
    }

    public final int getSize() {
        return size;
    }

    public final int getCount() {
        return count;
    }


    //aliases
    public void vertexUV(double x, double y, double z, float u, float v) {
        this.tex(u, v);
        this.vertex(x, y, z);
    }

    public final void color(int c) {
        byte r = (byte) (c >> 16);
        byte g = (byte) (c >> 8);
        byte b = (byte) c;
        this.color(r, g, b, (byte) 255);
    }

    public final void color(int r, int g, int b) {
        float r2 = (r & 0xFF) / 255.0f;
        float g2 = (g & 0xFF) / 255.0f;
        float b2 = (b & 0xFF) / 255.0f;
        this.color(r2, g2, b2, 1.0f);
    }

    public final void color(int r, int g, int b, int a) {
        float r2 = (r & 0xFF) / 255.0f;
        float g2 = (g & 0xFF) / 255.0f;
        float b2 = (b & 0xFF) / 255.0f;
        float a2 = (a & 0xFF) / 255.0f;
        this.color(r2, g2, b2, a2);
    }

    public final void vertex(Vector3d v) {
        this.vertex(v.x, v.y, v.z);
    }

    public final void normal(Vector3d v) {
        normal((float) v.x, (float) v.y, (float) v.z);
    }

    public final void tex(Vector2d v) {
        tex((float) v.x, (float) v.y);
    }

    public final void color(Vector4d v) {
        this.color((float) v.x, (float) v.y, (float) v.z, (float) v.w);
    }

    @Override
    protected void finalize() {
        if(!this.lifetimeCounter.isAllocated()){
            return;
        }
        this.free();
    }
}
