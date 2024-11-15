package me.gb2022.quantum3d.render.vertex;

import me.gb2022.quantum3d.util.GLUtil;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

import java.util.concurrent.atomic.AtomicInteger;

public interface VertexBuilderUploader {
    AtomicInteger UPLOAD_COUNT = new AtomicInteger();

    static void uploadPointer(VertexBuilder builder) {
        VertexFormat format = builder.getFormat();

        DataFormat vertexFormat = format.getVertexFormat();
        GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
        GL11.glVertexPointer(vertexFormat.getSize(), vertexFormat.getType().getGlId(), 0, builder.generateVertexBuffer());
        if (format.hasColorData()) {
            DataFormat fmt = format.getColorFormat();
            GL11.glEnableClientState(GL11.GL_COLOR_ARRAY);
            GL11.glColorPointer(fmt.getSize(), fmt.getType().getGlId(), 0, builder.generateColorBuffer());
        }
        if (format.hasTextureData()) {
            DataFormat fmt = format.getTextureFormat();
            GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
            GL11.glTexCoordPointer(fmt.getSize(), fmt.getType().getGlId(), 0, builder.generateTextureBuffer());
        }
        if (format.hasNormalData()) {
            DataFormat fmt = format.getNormalFormat();
            GL11.glEnableClientState(GL11.GL_NORMAL_ARRAY);
            GL11.glNormalPointer(fmt.getSize(), fmt.getType().getGlId(), builder.generateNormalBuffer());
        }
        GLUtil.checkError("upload_builder:data_upload");
        GL11.glDrawArrays(builder.getDrawMode().glId(), 0, builder.getVertexCount());
        GLUtil.checkError("upload_builder:draw_array");

        UPLOAD_COUNT.addAndGet(builder.getVertexCount());

        disableState(format);
        GLUtil.checkError("upload_builder:close_state");
    }


    static void enableState(VertexFormat format) {
        GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
        if (format.hasColorData()) {
            GL11.glEnableClientState(GL11.GL_COLOR_ARRAY);
        }
        if (format.hasTextureData()) {
            GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
        }
        if (format.hasNormalData()) {
            GL11.glEnableClientState(GL11.GL_NORMAL_ARRAY);
        }
    }

    static void disableState(VertexFormat format) {
        GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
        if (format.hasNormalData()) {
            GL11.glDisableClientState(GL11.GL_COLOR_ARRAY);
        }
        if (format.hasTextureData()) {
            GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
        }
        if (format.hasNormalData()) {
            GL11.glDisableClientState(GL11.GL_NORMAL_ARRAY);
        }
    }

    static void setPointerAndEnableState(VertexFormat format) {
        DataFormat vertexFormat = format.getVertexFormat();
        GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);


        var offset = 0;
        var stride = format.getTotalBytes();


        GL11.glVertexPointer(vertexFormat.getSize(), vertexFormat.getType().getGlId(), stride, offset);
        offset += vertexFormat.getSize() * vertexFormat.getType().getBytes();

        if (format.hasColorData()) {
            DataFormat fmt = format.getColorFormat();
            GL11.glColorPointer(fmt.getSize(), fmt.getType().getGlId(), stride, offset);
            offset += fmt.getSize() * fmt.getType().getBytes();
        }
        if (format.hasTextureData()) {
            DataFormat fmt = format.getTextureFormat();
            GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
            GL11.glTexCoordPointer(fmt.getSize(), fmt.getType().getGlId(), stride, offset);
            offset += fmt.getSize() * fmt.getType().getBytes();
        }
        if (format.hasNormalData()) {
            DataFormat fmt = format.getNormalFormat();
            GL11.glEnableClientState(GL11.GL_NORMAL_ARRAY);
            GL11.glNormalPointer(fmt.getType().getGlId(), stride, offset);
        }
    }

    static void uploadBuffer(VertexBuilder builder, int handle) {
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, handle);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, builder.generateRawBuffer(), GL15.GL_STATIC_DRAW);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
    }

    static int getUploadedCount() {
        return UPLOAD_COUNT.getAndSet(0);
    }
}
