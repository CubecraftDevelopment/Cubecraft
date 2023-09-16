package ink.flybird.quantum3d_legacy.draw;

import ink.flybird.fcommon.context.LifetimeCounter;
import ink.flybird.quantum3d_legacy.BufferAllocation;
import ink.flybird.quantum3d_legacy.GLUtil;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.system.MemoryUtil;

import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;

public final class OffHeapVertexBuilder extends VertexBuilder {
    public static final int ALLOCATED = 1;
    public static final int RELEASED = 2;


    private final FloatBuffer vertexArray;
    private final FloatBuffer texCoordArray;
    private final FloatBuffer colorArray;
    private final FloatBuffer normalArray;
    private final DoubleBuffer rawArray;
    private final LifetimeCounter counter = new LifetimeCounter();

    public OffHeapVertexBuilder(int size, DrawMode drawMode) {
        super(size, drawMode);
        this.counter.allocate();
        this.vertexArray = BufferAllocation.allocFloatBuffer(this.size * 3);
        this.texCoordArray = BufferAllocation.allocFloatBuffer(this.size * 3);
        this.colorArray = BufferAllocation.allocFloatBuffer(this.size * 4);
        this.normalArray = BufferAllocation.allocFloatBuffer(this.size * 3);
        this.rawArray = BufferAllocation.allocDoubleBuffer(this.size * 13);
    }

    public void free() {
        if (!this.counter.isAllocated()) {
            return;
        }
        super.free();
        this.counter.release();
        BufferAllocation.free(this.vertexArray);
        BufferAllocation.free(this.texCoordArray);
        BufferAllocation.free(this.colorArray);
        BufferAllocation.free(this.normalArray);
        BufferAllocation.free(this.rawArray);
    }

    @Override
    public void vertex(double x, double y, double z) {
        if (!this.counter.isAllocated()) {
            return;
        }
        this.vertexArray.put((float) x).put((float) y).put((float) z);
        this.texCoordArray.put(this.u).put(this.v);
        this.colorArray.put(this.r).put(this.g).put(this.b).put(this.a);
        this.normalArray.put(this.n).put(this.f).put(this.l);
        this.rawArray.put(this.u).put(this.v).put(this.r).put(this.g).put(this.b).put(this.a).put(this.n).put(this.f).put(this.l).put(x).put(y).put(z);
        this.count += 1;
    }

    public FloatBuffer getVertexArray() {
        return this.vertexArray.flip().slice();
    }

    public FloatBuffer getTexCoordArray() {
        return texCoordArray.flip().slice();
    }

    public FloatBuffer getColorArray() {
        return colorArray.flip().slice();
    }

    public FloatBuffer getNormalArray() {
        return normalArray.flip().slice();
    }

    public DoubleBuffer getRawArray() {
        return rawArray.flip().slice();
    }

    @Override
    public void uploadPointer() {//TODO:OPTIONAL FORMAT
        if (!this.counter.isAllocated()) {
            return;
        }
        VertexUploader.UPLOAD_COUNTER.addAndGet(this.getCount());


        //GL11.glEnableClientState(GL11.GL_NORMAL_ARRAY);


        GL11.glVertexPointer(3, GL11.GL_FLOAT, 0, MemoryUtil.memAddress(this.getVertexArray()));
        GL11.glTexCoordPointer(2, GL11.GL_FLOAT, 0, MemoryUtil.memAddress(this.getTexCoordArray()));
        GL11.glColorPointer(4, GL11.GL_FLOAT, 0, MemoryUtil.memAddress(this.getColorArray()));

        GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
        GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
        GL11.glEnableClientState(GL11.GL_COLOR_ARRAY);

        //GL11.glNormalPointer(GL11.GL_FLOAT, 0, MemoryUtil.memAddress(this.getNormalArray()));
        GL11.glDrawArrays(this.getDrawMode().getGlMode(), 0, this.getCount());
        GLUtil.disableClientState();
    }

    @Override
    public void uploadInterLeaved() {
        if (!this.counter.isAllocated()) {
            return;
        }

        VertexUploader.UPLOAD_COUNTER.addAndGet(this.getCount());

        GLUtil.enableClientState();
        GL11.glInterleavedArrays(GL11.GL_T2F_C4F_N3F_V3F, 0, this.getRawArray());
        GL11.glDrawArrays(this.getDrawMode().getGlMode(), 0, this.getCount());
        GLUtil.disableClientState();
    }

    @Override
    public void uploadBuffer(int buffer) {
        if (!this.counter.isAllocated()) {
            return;
        }

        VertexUploader.UPLOAD_COUNTER.addAndGet(this.getCount());

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, buffer);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, this.getRawArray(), GL15.GL_STATIC_DRAW);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
    }

    @Override
    protected void finalize() {
        if (!this.counter.isAllocated()) {
            return;
        }
        try {
            this.free();
        } catch (Exception ignored) {
        }
    }
}
