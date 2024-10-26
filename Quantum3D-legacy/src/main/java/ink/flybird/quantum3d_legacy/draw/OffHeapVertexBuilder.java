package ink.flybird.quantum3d_legacy.draw;

import ink.flybird.quantum3d_legacy.BufferAllocation;
import ink.flybird.quantum3d_legacy.GLUtil;
import me.gb2022.commons.context.LifetimeCounter;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;

public final class OffHeapVertexBuilder extends VertexBuilder {
    private final Exception callerRef = new RuntimeException("reference");

    private final ByteBuffer vertexArray;
    private final ByteBuffer texCoordArray;
    private final ByteBuffer colorArray;
    private final ByteBuffer normalArray;
    private final ByteBuffer rawArray;
    private final LifetimeCounter counter = new LifetimeCounter();

    public OffHeapVertexBuilder(int size, DrawMode drawMode) {
        super(size, drawMode);
        this.counter.allocate();
        this.vertexArray = BufferAllocation.allocByteBuffer(this.size * 3 * 4);
        this.texCoordArray = BufferAllocation.allocByteBuffer(this.size * 3 * 4);
        this.colorArray = BufferAllocation.allocByteBuffer(this.size * 4 * 4);
        this.normalArray = BufferAllocation.allocByteBuffer(this.size * 3 * 4);
        this.rawArray = BufferAllocation.allocByteBuffer(this.size * 13 * 4);
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
        this.vertexArray.putFloat((float) x).putFloat((float) y).putFloat((float) z);
        this.texCoordArray.putFloat(this.u).putFloat(this.v);
        this.colorArray.putFloat(this.r).putFloat(this.g).putFloat(this.b).putFloat(this.a);
        this.normalArray.putFloat(this.n).putFloat(this.f).putFloat(this.l);
        this.rawArray
                .putFloat(this.u).putFloat(this.v)
                .putFloat(this.r).putFloat(this.g).putFloat(this.b).putFloat(this.a)
                .putFloat(this.n).putFloat(this.f).putFloat(this.l)
                .putFloat((float) x).putFloat((float) y).putFloat((float) z);

        this.count++;
    }

    public ByteBuffer getVertexArray() {
        return this.vertexArray.flip().slice();
    }

    public ByteBuffer getTexCoordArray() {
        return texCoordArray.flip().slice();
    }

    public ByteBuffer getColorArray() {
        return colorArray.flip().slice();
    }

    public ByteBuffer getNormalArray() {
        return normalArray.flip().slice();
    }

    public ByteBuffer getRawArray() {
        return rawArray.flip().slice();
    }

    @Override
    public void uploadPointer() {
        if (!this.counter.isAllocated()) {
            return;
        }
        VertexUploader.UPLOAD_COUNTER.addAndGet(this.getCount());


        //GL11.glEnableClientState(GL11.GL_NORMAL_ARRAY);


        GL11.glVertexPointer(3, GL11.GL_FLOAT, 0, MemoryUtil.memAddress(this.getVertexArray()));
        GL11.glTexCoordPointer(2, GL11.GL_FLOAT, 0, MemoryUtil.memAddress(this.getTexCoordArray()));
        GL11.glColorPointer(4, GL11.GL_FLOAT, 0, MemoryUtil.memAddress(this.getColorArray()));
        GL11.glNormalPointer(GL11.GL_FLOAT, 0, MemoryUtil.memAddress(this.getNormalArray()));


        GLUtil.enableClientState();
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

        this.free();

        try {
            //System.out.println("[-----leak-----]");
            //for (var e:this.callerRef.getStackTrace()){
            //    System.out.println(e);
            //}
        } catch (Exception ignored) {
        }
    }

    @Override
    public void clear() {
        super.clear();
        this.colorArray.clear();
        this.colorArray.position(0);
        this.normalArray.clear();
        this.normalArray.position(0);
        this.rawArray.clear();
        this.rawArray.position(0);
        this.vertexArray.clear();
        this.vertexArray.position(0);
        this.texCoordArray.clear();
        this.texCoordArray.position(0);
    }

}
