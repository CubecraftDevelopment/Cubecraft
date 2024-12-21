package net.cubecraft.client.render.chunk;

import me.gb2022.quantum3d.legacy.drawcall.DrawCallException;
import me.gb2022.quantum3d.render.vertex.DrawMode;
import me.gb2022.quantum3d.render.vertex.VertexBuilder;
import me.gb2022.quantum3d.render.vertex.VertexBuilderUploader;
import me.gb2022.quantum3d.render.vertex.VertexFormat;
import me.gb2022.quantum3d.util.GLUtil;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;

public interface RenderBatch {
    static RenderBatch create(boolean vbo) {
        if (vbo) {
            return new VBORenderBatch();
        } else {
            return new ListRenderBatch();
        }
    }

    void call();

    void upload(VertexBuilder builder);

    void allocate();

    void free();

    boolean isAllocated();

    int getHandle();


    final class ListRenderBatch implements RenderBatch {
        private int list = -1;
        private int count;

        @Override
        public void call() {
            if (!this.isAllocated()) {
                return;
            }
            GL11.glCallList(this.list);

            VertexBuilderUploader.UPLOAD_COUNT.addAndGet(this.count);
        }

        @Override
        public void upload(VertexBuilder builder) {
            if (!this.isAllocated()) {
                return;
            }
            GL11.glNewList(this.list, GL11.GL_COMPILE);
            if (!builder.getLifetimeCounter().isAllocated()) {
                GL11.glEndList();
                return;
            }
            VertexBuilderUploader.uploadPointer(builder);
            GL11.glEndList();

            this.count = builder.getVertexCount();
        }

        @Override
        public void allocate() {
            if(this.isAllocated()) {
                return;
            }
            this.list = GL11.glGenLists(1);
        }

        @Override
        public void free() {
            if(!this.isAllocated()) {
                return;
            }
            GL11.glDeleteLists(this.list, 1);
            this.list = -1;
        }

        @Override
        public boolean isAllocated() {
            return this.list != -1;
        }

        @Override
        public int getHandle() {
            return this.list;
        }
    }


    final class VBORenderBatch implements RenderBatch {
        private DrawMode mode;
        private VertexFormat format;

        private int vbo;
        private int count;
        private boolean allocated = false;
        private boolean uploaded = false;

        @Override
        public void call() {
            if (!this.allocated) {
                return;
            }
            if (!this.uploaded) {
                return;
            }
            if (this.count <= 0) {
                return;
            }

            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, this.vbo);

            //VertexBuilderUploader.setPointerAndEnableState(this.format, 0);

            GL11.glVertexPointer(3, GL11.GL_FLOAT, 36, 0);
            GL11.glColorPointer(4, GL11.GL_FLOAT, 36, 12);
            GL11.glTexCoordPointer(2, GL11.GL_FLOAT, 36, 28);

            GLUtil.drawArrays(GL11.GL_QUADS, 0, this.count);

            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

            VertexBuilderUploader.UPLOAD_COUNT.addAndGet(this.count);
        }

        @Override
        public void upload(VertexBuilder builder) {
            if (!this.allocated) {
                throw new DrawCallException("not initialized");
            }
            this.mode = builder.getDrawMode();
            this.format = builder.getFormat();
            this.count = builder.getVertexCount();
            VertexBuilderUploader.uploadBuffer(builder, this.vbo);
            GL30.glBindVertexArray(0);
            this.uploaded = true;
        }

        @Override
        public void allocate() {
            if (!this.allocated) {
                this.vbo = GL15.glGenBuffers();
                this.allocated = true;
            }
        }

        @Override
        public void free() {
            this.allocated = false;
            GL15.glDeleteBuffers(this.vbo);
        }

        @Override
        public boolean isAllocated() {
            return this.allocated;
        }

        @Override
        public int getHandle() {
            return this.vbo;
        }
    }
}
