package ink.flybird.quantum3d_legacy;

import ink.flybird.quantum3d_legacy.draw.VertexUploader;
import org.joml.Matrix4d;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;

import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;

public interface GLUtil {
    static void setDrawOutput(boolean b) {
        GL11.glColorMask(b, b, b, b);
        GL11.glDepthMask(b);
    }


    //matrix
    static void setupPerspectiveCamera(float fov, int width, int height) {
        GLUtil.assertRenderThread();
        GL11.glMatrixMode(5889);
        loadIdentity();
        createPerspectiveMatrix(fov, (float) width / height, 0.05, 131072);
        GL11.glMatrixMode(5888);
        loadIdentity();
    }

    static void setupOrthogonalCamera(int x, int y, int displayWidth, int displayHeight, int w, int h) {
        GLUtil.assertRenderThread();
        GL11.glViewport(x, y, displayWidth, displayHeight);
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        loadIdentity();
        GL11.glOrtho(x, w, h, y, -100, 100);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        loadIdentity();
    }

    static void createPerspectiveMatrix(double fov, double aspect, double zn, double zf) {
        GLUtil.assertRenderThread();
        Matrix4d mat = new Matrix4d();
        loadIdentity();
        mat.perspective(fov, aspect, zn, zf);
        DoubleBuffer matrix = BufferAllocation.allocDoubleBuffer(16);
        GL11.glMultMatrixd(mat.get(matrix));
        BufferAllocation.free(matrix);
    }

    static void loadIdentity() {
        GL11.glLoadMatrixd(new Matrix4d().identity().get(new double[16]));
    }



    //error check
    static void checkError(String status) {
        int errorStatus = GL11.glGetError();
        if (errorStatus != 0) {
            throw new RuntimeException("%s[%d]:%s".formatted(status, errorStatus, getErrorString(errorStatus)));
        }
    }

    static String getErrorString(int err) {
        return switch (err) {
            case GL11.GL_STACK_OVERFLOW -> "stack_over_flow";
            case GL11.GL_STACK_UNDERFLOW -> "stack_down_flow";
            case GL11.GL_INVALID_OPERATION -> "invalid_operation";
            case GL11.GL_INVALID_ENUM -> "invalid_enum";
            case GL11.GL_INVALID_VALUE -> "invalid_value";
            default -> "unverified_error";
        };
    }

    static void assertRenderThread() throws IllegalStateException {
        try {
            GL11.glColor4f(1, 1, 1, 1);
        } catch (Exception e) {
            throw new IllegalStateException("you are not at the render context thread!");
        }
    }


    //draw
    static void enableClientState() {
        GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
        GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
        GL11.glEnableClientState(GL11.GL_COLOR_ARRAY);
        GL11.glEnableClientState(GL11.GL_NORMAL_ARRAY);
    }

    static void disableClientState() {
        GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
        GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
        GL11.glDisableClientState(GL11.GL_COLOR_ARRAY);
        GL11.glDisableClientState(GL11.GL_NORMAL_ARRAY);
    }

    static void drawArrays(int drawMode, int i, int count) {
        GL11.glDrawArrays(drawMode, i, count);
        VertexUploader.UPLOAD_COUNTER.addAndGet(count);
    }


    //test
    static void enableAlphaTest() {
        GLUtil.assertRenderThread();
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.1f);
    }

    static void disableAlphaTest() {
        GL11.glDisable(GL11.GL_ALPHA_TEST);
    }

    static void enableDepthTest() {
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthFunc(GL11.GL_LESS);
    }

    static void disableDepthTest() {
        GL11.glDisable(GL11.GL_DEPTH_TEST);
    }


    //effect
    static void enableBlend() {
        GLUtil.assertRenderThread();
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.1f);
        GL11.glEnable(GL11.GL_BLEND);
        GL14.glBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA,1,0);
    }

    static void disableBlend() {
        GLUtil.assertRenderThread();
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glDisable(GL11.GL_BLEND);
    }

    static void enableMultiSample() {
        GLUtil.assertRenderThread();
        GL11.glEnable(GL13.GL_MULTISAMPLE);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glEnable(GL11.GL_POLYGON_SMOOTH);
    }

    static void disableMultiSample() {
        GLUtil.assertRenderThread();
        GL11.glDisable(GL13.GL_MULTISAMPLE);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glDisable(GL11.GL_POLYGON_SMOOTH);
        disableBlend();
    }

    static void setupFog(double distance, float[] color) {
        GLUtil.assertRenderThread();
        GL11.glEnable(GL11.GL_FOG);
        FloatBuffer buffer = BufferUtil.from(color);
        GL11.glFogfv(GL11.GL_FOG_COLOR, color);
        BufferAllocation.free(buffer);
        GL11.glFogf(GL11.GL_FOG_DENSITY, (float) (1f / distance));
        GL11.glHint(GL11.GL_FOG_HINT, GL11.GL_NICEST);
        GL11.glFogi(GL11.GL_FOG_MODE, GL11.GL_EXP2);
    }


    static void setupFog(double distance, FloatBuffer color) {
        GLUtil.assertRenderThread();
        GL11.glEnable(GL11.GL_FOG);
        GL11.glFogfv(GL11.GL_FOG_COLOR, color);
        GL11.glFogf(GL11.GL_FOG_DENSITY, (float) (1f / distance));
        GL11.glHint(GL11.GL_FOG_HINT, GL11.GL_NICEST);
        GL11.glFogi(GL11.GL_FOG_MODE, GL11.GL_EXP2);
    }
}
