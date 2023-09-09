package ink.flybird.quantum3d.draw;

import ink.flybird.quantum3d.BufferAllocation;
import ink.flybird.quantum3d.GLUtil;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.system.MemoryUtil;

import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;

public final class OffHeapVertexBuilder extends VertexBuilder {
    public static final int ALLOCATED = 1;
    public static final int RELEASED = 2;

    private final DoubleBuffer vertexArray;
    private final FloatBuffer texCoordArray;
    private final FloatBuffer colorArray;
    private final FloatBuffer normalArray;
    private final DoubleBuffer rawArray;
    private int status;

    public OffHeapVertexBuilder(int size, DrawMode drawMode) {
        super(size, drawMode);
        this.status = ALLOCATED;
        this.vertexArray = BufferAllocation.allocDoubleBuffer(this.size * 3);
        this.texCoordArray = BufferAllocation.allocFloatBuffer(this.size * 3);
        this.colorArray = BufferAllocation.allocFloatBuffer(this.size * 4);
        this.normalArray = BufferAllocation.allocFloatBuffer(this.size * 3);
        this.rawArray = BufferAllocation.allocDoubleBuffer(this.size * 13);
    }

    public void free() {
        if (this.status != ALLOCATED) {
            return;
        }
        super.free();
        this.status = RELEASED;
        BufferAllocation.free(this.vertexArray);
        BufferAllocation.free(this.texCoordArray);
        BufferAllocation.free(this.colorArray);
        BufferAllocation.free(this.normalArray);
        BufferAllocation.free(this.rawArray);
    }

    @Override
    public void vertex(double x, double y, double z) {
        this.vertexArray.put(x).put(y).put(z);
        this.texCoordArray.put(this.u).put(this.v);
        this.colorArray.put(this.r).put(this.g).put(this.b).put(this.a);
        this.normalArray.put(this.n).put(this.f).put(this.l);
        this.rawArray.put(this.u).put(this.v).put(this.r).put(this.g).put(this.b).put(this.a).put(this.n).put(this.f).put(this.l).put(x).put(y).put(z);
        this.count += 1;
    }

    public DoubleBuffer getVertexArray() {
        return this.vertexArray.flip();
    }

    public FloatBuffer getTexCoordArray() {
        return texCoordArray.flip();
    }

    public FloatBuffer getColorArray() {
        return colorArray.flip();
    }

    public FloatBuffer getNormalArray() {
        return normalArray.flip();
    }

    public DoubleBuffer getRawArray() {
        return rawArray.flip();
    }

    @Override
    public void uploadPointer() {//TODO:OPTIONAL FORMAT
        VertexUploader.UPLOAD_COUNTER.addAndGet(this.getCount());

        GLUtil.enableClientState();
        GL11.glVertexPointer(3, GL11.GL_DOUBLE, 0, MemoryUtil.memAddress(this.getVertexArray()));
        GL11.glTexCoordPointer(2, GL11.GL_FLOAT, 0, MemoryUtil.memAddress(this.getTexCoordArray()));
        GL11.glColorPointer(4, GL11.GL_FLOAT, 0, MemoryUtil.memAddress(this.getColorArray()));
        GL11.glNormalPointer(GL11.GL_FLOAT, 0, MemoryUtil.memAddress(this.getNormalArray()));
        GL11.glDrawArrays(this.getDrawMode().getGlMode(), 0, this.getCount());
        GLUtil.disableClientState();
    }

    @Override
    public void uploadInterLeaved() {
        VertexUploader.UPLOAD_COUNTER.addAndGet(this.getCount());

        GLUtil.enableClientState();
        GL11.glInterleavedArrays(GL11.GL_T2F_C4F_N3F_V3F, 0, this.getRawArray());
        GL11.glDrawArrays(this.getDrawMode().getGlMode(), 0, this.getCount());
        GLUtil.disableClientState();
    }

    @Override
    public void uploadBuffer(int buffer) {
        VertexUploader.UPLOAD_COUNTER.addAndGet(this.getCount());

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, buffer);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, this.getRawArray(), GL15.GL_STATIC_DRAW);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
    }

    @Override
    protected void finalize() {
        if (this.status == RELEASED) {
            return;
        }
        try {
            this.free();
            System.out.println("un-excepted free!");
        } catch (Exception ignored) {
        }
    }
}
