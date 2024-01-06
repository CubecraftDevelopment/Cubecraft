package me.gb2022.quantum3d.render.vertex;

import me.gb2022.quantum3d.lwjgl.deprecated.GLUtil;
import org.lwjgl.opengl.GL11;

public interface VertexBuilderUploader {
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
        GLUtil.checkError("upload_builder:close_state");
    }
}
