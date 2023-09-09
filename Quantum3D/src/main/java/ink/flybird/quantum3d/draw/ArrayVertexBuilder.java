package ink.flybird.quantum3d.draw;

import ink.flybird.quantum3d.BufferUtil;
import ink.flybird.quantum3d.GLUtil;
import ink.flybird.fcommon.container.ArrayUtil;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.system.MemoryUtil;

public final class ArrayVertexBuilder extends VertexBuilder {
    private final double[] color;
    private final double[] normal;
    private final double[] tc;
    private final double[] vertex;
    private final double[] raw;

    public ArrayVertexBuilder(int size, DrawMode mode) {
        super(size, mode);
        color = new double[size * 4];
        normal = new double[size * 3];
        tc = new double[size * 3];
        vertex = new double[size * 3];
        this.raw = new double[size * 12];
    }

    public void end() {
        for (int i = 0; i < count; i++) {
            raw[i * 12] = vertex[i * 3];
            raw[i * 12 + 1] = vertex[i * 3 + 1];
            raw[i * 12 + 2] = vertex[i * 3 + 2];

            raw[i * 12 + 3] = tc[i * 2];
            raw[i * 12 + 4] = tc[i * 2 + 1];

            raw[i * 12 + 5] = color[i * 4];
            raw[i * 12 + 6] = color[i * 4 + 1];
            raw[i * 12 + 7] = color[i * 4 + 2];
            raw[i * 12 + 8] = color[i * 4 + 3];

            raw[i * 12 + 9] = normal[i * 3];
            raw[i * 12 + 10] = normal[i * 3 + 1];
            raw[i * 12 + 11] = normal[i * 3 + 2];
        }
    }

    public void vertex(double x, double y, double z) {
        this.vertex[this.count * 3] = (float) x;
        this.vertex[this.count * 3 + 1] = (float) y;
        this.vertex[this.count * 3 + 2] = (float) z;

        this.color[this.count * 4] = this.r;
        this.color[this.count * 4 + 1] = g;
        this.color[this.count * 4 + 2] = b;
        this.color[this.count * 4 + 3] = a;

        normal[this.count * 3] = this.n;
        normal[this.count * 3 + 1] = this.f;
        normal[this.count * 3 + 2] = this.l;

        tc[this.count * 2] = this.u;
        tc[this.count * 2 + 1] = this.v;
        ++this.count;
    }


    public double[] getRawArray() {
        return ArrayUtil.copySub(0, count * 12, this.raw);
    }

    public double[] getVertexArray() {
        return ArrayUtil.copySub(0, count * 3, vertex);
    }

    public double[] getNormalArray() {
        return ArrayUtil.copySub(0, count * 3, normal);
    }

    public double[] getColorArray() {
        return ArrayUtil.copySub(0, count * 4, color);
    }

    public double[] getTexCoordArray() {
        return ArrayUtil.copySub(0, count * 2, tc);
    }


    @Override
    public synchronized void uploadPointer() {
        VertexUploader.UPLOAD_COUNTER.addAndGet(this.count);

        BufferUtil.fillBuffer(VertexUploader.TEX_COORD_ARRAY_SWAP, this.getTexCoordArray());
        BufferUtil.fillBuffer(VertexUploader.VERTEX_ARRAY_SWAP, this.getVertexArray());
        BufferUtil.fillBuffer(VertexUploader.COLOR_ARRAY_SWAP, this.getColorArray());
        BufferUtil.fillBuffer(VertexUploader.NORMAL_ARRAY_SWAP, this.getNormalArray());

        GLUtil.enableClientState();

        GL11.glTexCoordPointer(2, GL11.GL_DOUBLE, 0, MemoryUtil.memAddress(VertexUploader.TEX_COORD_ARRAY_SWAP));
        GL11.glColorPointer(4, GL11.GL_DOUBLE, 0, MemoryUtil.memAddress(VertexUploader.COLOR_ARRAY_SWAP));
        GL11.glNormalPointer(GL11.GL_DOUBLE, 0, MemoryUtil.memAddress(VertexUploader.NORMAL_ARRAY_SWAP));
        GL11.glVertexPointer(3, GL11.GL_DOUBLE, 0, MemoryUtil.memAddress(VertexUploader.VERTEX_ARRAY_SWAP));
        GL11.glDrawArrays(this.getDrawMode().getGlMode(), 0, this.getCount());

        GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
        GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
        GL11.glDisableClientState(GL11.GL_COLOR_ARRAY);
        GL11.glDisableClientState(GL11.GL_NORMAL_ARRAY);
    }

    @Override
    public void uploadInterLeaved() {
        VertexUploader.UPLOAD_COUNTER.addAndGet(this.getCount());

        GLUtil.enableClientState();
        GL11.glInterleavedArrays(GL11.GL_T2F_C4F_N3F_V3F, 0, this.getRawArray());
        GL11.glDrawArrays(this.getDrawMode().getGlMode(), 0, this.getCount());
        GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
        GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
        GL11.glDisableClientState(GL11.GL_COLOR_ARRAY);
        GL11.glDisableClientState(GL11.GL_NORMAL_ARRAY);
    }

    @Override
    public synchronized void uploadBuffer(int buffer) {
        VertexUploader.UPLOAD_COUNTER.addAndGet(this.count);

        BufferUtil.fillBuffer(VertexUploader.RAW_ARRAY_SWAP, this.getRawArray());
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, buffer);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, VertexUploader.RAW_ARRAY_SWAP, 35044);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
    }

    @Override
    public void free() {
        super.free();
    }
}
