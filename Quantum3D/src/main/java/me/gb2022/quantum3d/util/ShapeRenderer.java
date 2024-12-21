package me.gb2022.quantum3d.util;

import me.gb2022.quantum3d.legacy.draw.LegacyVertexBuilder;
import me.gb2022.commons.math.AABB;
import me.gb2022.quantum3d.render.vertex.VertexBuilder;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import java.nio.FloatBuffer;

public interface ShapeRenderer {
    Tesselator TESSELATOR = Tesselator.instance;

    static void drawRectUV(double x0, double x1, double y0, double y1, double z, double u0, double u1, double v0, double v1) {
        TESSELATOR.begin();
        TESSELATOR.vertexUV(x1, y0, z, (float) u1, (float) v0);
        TESSELATOR.vertexUV(x0, y0, z, (float) u0, (float) v0);
        TESSELATOR.vertexUV(x0, y1, z, (float) u0, (float) v1);
        TESSELATOR.vertexUV(x1, y1, z, (float) u1, (float) v1);
        TESSELATOR.end();
    }

    static void drawRectUV(VertexBuilder builder,double x0, double x1, double y0, double y1, double z, double u0, double u1, double v0, double v1){
        builder.setTextureCoordinate(u1, v0).addVertex(x1, y0, z);
        builder.setTextureCoordinate(u0, v0).addVertex(x0, y0, z);
        builder.setTextureCoordinate(u0, v1).addVertex(x0, y1, z);
        builder.setTextureCoordinate(u1, v1).addVertex(x1, y1, z);
    }

    static void drawRect(double x0, double x1, double y0, double y1, double z0, double z1) {
        TESSELATOR.begin();
        TESSELATOR.vertex((float) x0, (float) y1, (float) z1);
        TESSELATOR.vertex((float) x1, (float) y1, (float) z0);
        TESSELATOR.vertex((float) x1, (float) y0, (float) z0);
        TESSELATOR.vertex((float) x0, (float) y0, (float) z1);
        TESSELATOR.end();
    }

    static void setColor(int r, int g, int b, int a) {
        GL11.glColor4f(r / 256f, g / 256f, b / 256f, a / 256f);
    }

    static void setColor(int color) {
        int r = (color >> 16 & 0xFF);
        int g = (color >> 8 & 0xFF);
        int b = (color & 0xFF);
        setColor(r, g, b, 255);
    }

    static void begin() {
        TESSELATOR.begin();
    }

    static void end() {
        TESSELATOR.end();
    }


    //with builder
    static void drawRect(LegacyVertexBuilder builder, double x0, double x1, double y0, double y1, double z0, double z1) {
        builder.vertex((float) x0, (float) y1, (float) z1);
        builder.vertex((float) x1, (float) y1, (float) z0);
        builder.vertex((float) x1, (float) y0, (float) z0);
        builder.vertex((float) x0, (float) y0, (float) z1);
    }

    static void drawRectUV(LegacyVertexBuilder builder, double x0, double x1, double y0, double y1, double z0, double u0, double u1, double v0, double v1) {
        builder.vertexUV(x1, y0, z0, (float) u1, (float) v0);
        builder.vertexUV(x0, y0, z0, (float) u0, (float) v0);
        builder.vertexUV(x0, y1, z0, (float) u0, (float) v1);
        builder.vertexUV(x1, y1, z0, (float) u1, (float) v1);
    }

    static void renderAABBBox(LegacyVertexBuilder builder, AABB aabb) {
        builder.vertex(aabb.x0, aabb.y0, aabb.z1);
        builder.vertex(aabb.x0, aabb.y0, aabb.z0);
        builder.vertex(aabb.x1, aabb.y0, aabb.z0);
        builder.vertex(aabb.x1, aabb.y0, aabb.z1);
        builder.vertex(aabb.x1, aabb.y1, aabb.z1);
        builder.vertex(aabb.x1, aabb.y1, aabb.z0);
        builder.vertex(aabb.x0, aabb.y1, aabb.z0);
        builder.vertex(aabb.x0, aabb.y1, aabb.z1);
        builder.vertex(aabb.x0, aabb.y1, aabb.z0);
        builder.vertex(aabb.x1, aabb.y1, aabb.z0);
        builder.vertex(aabb.x1, aabb.y0, aabb.z0);
        builder.vertex(aabb.x0, aabb.y0, aabb.z0);
        builder.vertex(aabb.x0, aabb.y1, aabb.z1);
        builder.vertex(aabb.x0, aabb.y0, aabb.z1);
        builder.vertex(aabb.x1, aabb.y0, aabb.z1);
        builder.vertex(aabb.x1, aabb.y1, aabb.z1);
        builder.vertex(aabb.x0, aabb.y1, aabb.z1);
        builder.vertex(aabb.x0, aabb.y1, aabb.z0);
        builder.vertex(aabb.x0, aabb.y0, aabb.z0);
        builder.vertex(aabb.x0, aabb.y0, aabb.z1);
        builder.vertex(aabb.x1, aabb.y0, aabb.z1);
        builder.vertex(aabb.x1, aabb.y0, aabb.z0);
        builder.vertex(aabb.x1, aabb.y1, aabb.z0);
        builder.vertex(aabb.x1, aabb.y1, aabb.z1);
    }

    static void renderAABBBoxInner(LegacyVertexBuilder builder, AABB aabb) {
        builder.vertex(aabb.x1, aabb.y0, aabb.z1);
        builder.vertex(aabb.x1, aabb.y0, aabb.z0);
        builder.vertex(aabb.x0, aabb.y0, aabb.z0);
        builder.vertex(aabb.x0, aabb.y0, aabb.z1);
        builder.vertex(aabb.x0, aabb.y1, aabb.z1);
        builder.vertex(aabb.x0, aabb.y1, aabb.z0);
        builder.vertex(aabb.x1, aabb.y1, aabb.z0);
        builder.vertex(aabb.x1, aabb.y1, aabb.z1);
        builder.vertex(aabb.x0, aabb.y0, aabb.z0);
        builder.vertex(aabb.x1, aabb.y0, aabb.z0);
        builder.vertex(aabb.x1, aabb.y1, aabb.z0);
        builder.vertex(aabb.x0, aabb.y1, aabb.z0);
        builder.vertex(aabb.x1, aabb.y1, aabb.z1);
        builder.vertex(aabb.x1, aabb.y0, aabb.z1);
        builder.vertex(aabb.x0, aabb.y0, aabb.z1);
        builder.vertex(aabb.x0, aabb.y1, aabb.z1);
        builder.vertex(aabb.x0, aabb.y0, aabb.z1);
        builder.vertex(aabb.x0, aabb.y0, aabb.z0);
        builder.vertex(aabb.x0, aabb.y1, aabb.z0);
        builder.vertex(aabb.x0, aabb.y1, aabb.z1);
        builder.vertex(aabb.x1, aabb.y1, aabb.z1);
        builder.vertex(aabb.x1, aabb.y1, aabb.z0);
        builder.vertex(aabb.x1, aabb.y0, aabb.z0);
        builder.vertex(aabb.x1, aabb.y0, aabb.z1);
    }

    static void renderAABB(VertexBuilder builder, double x0, double y0, double z0, double x1, double y1, double z1) {
        builder.addVertex(x0, y0, z0);
        builder.addVertex(x1, y0, z0);
        builder.addVertex(x0, y1, z0);
        builder.addVertex(x1, y1, z0);
        builder.addVertex(x0, y0, z1);
        builder.addVertex(x1, y0, z1);
        builder.addVertex(x0, y1, z1);
        builder.addVertex(x1, y1, z1);
        builder.addVertex(x0, y0, z0);
        builder.addVertex(x0, y1, z0);
        builder.addVertex(x1, y0, z0);
        builder.addVertex(x1, y1, z0);
        builder.addVertex(x0, y0, z1);
        builder.addVertex(x0, y1, z1);
        builder.addVertex(x1, y0, z1);
        builder.addVertex(x1, y1, z1);
        builder.addVertex(x0, y0, z0);
        builder.addVertex(x0, y0, z1);
        builder.addVertex(x1, y0, z0);
        builder.addVertex(x1, y0, z1);
        builder.addVertex(x0, y1, z0);
        builder.addVertex(x0, y1, z1);
        builder.addVertex(x1, y1, z0);
        builder.addVertex(x1, y1, z1);
    }

    static void renderAABB(LegacyVertexBuilder builder, double x0, double y0, double z0, double x1, double y1, double z1) {
        builder.vertex(x0, y0, z0);
        builder.vertex(x1, y0, z0);
        builder.vertex(x0, y1, z0);
        builder.vertex(x1, y1, z0);
        builder.vertex(x0, y0, z1);
        builder.vertex(x1, y0, z1);
        builder.vertex(x0, y1, z1);
        builder.vertex(x1, y1, z1);
        builder.vertex(x0, y0, z0);
        builder.vertex(x0, y1, z0);
        builder.vertex(x1, y0, z0);
        builder.vertex(x1, y1, z0);
        builder.vertex(x0, y0, z1);
        builder.vertex(x0, y1, z1);
        builder.vertex(x1, y0, z1);
        builder.vertex(x1, y1, z1);
        builder.vertex(x0, y0, z0);
        builder.vertex(x0, y0, z1);
        builder.vertex(x1, y0, z0);
        builder.vertex(x1, y0, z1);
        builder.vertex(x0, y1, z0);
        builder.vertex(x0, y1, z1);
        builder.vertex(x1, y1, z0);
        builder.vertex(x1, y1, z1);
    }

    static void renderAABB(VertexBuilder builder, AABB aabb) {
        renderAABB(builder, aabb.x0, aabb.y0, aabb.z0, aabb.x1, aabb.y1, aabb.z1);
    }

    static void renderAABB(LegacyVertexBuilder builder, AABB aabb) {
        renderAABB(builder, aabb.x0, aabb.y0, aabb.z0, aabb.x1, aabb.y1, aabb.z1);
    }


    class Tesselator {
        static final Tesselator instance = new Tesselator();
        private final FloatBuffer buffer = BufferUtils.createFloatBuffer(524288);
        private final float[] array = new float[524288];
        private int vertices = 0;
        private float u;
        private float v;
        private float r;
        private float g;
        private float b;
        private boolean hasColor = false;
        private boolean hasTexture = false;
        private int len = 3;
        private int p = 0;
        private boolean noColor = false;

        private Tesselator() {
        }

        void end() {
            if (this.vertices > 0) {
                this.buffer.clear();
                this.buffer.put(this.array, 0, this.p);
                this.buffer.flip();
                if (this.hasTexture && this.hasColor) {
                    GL11.glInterleavedArrays(10794, 0, this.buffer);
                } else if (this.hasTexture) {
                    GL11.glInterleavedArrays(10791, 0, this.buffer);
                } else if (this.hasColor) {
                    GL11.glInterleavedArrays(10788, 0, this.buffer);
                } else {
                    GL11.glInterleavedArrays(10785, 0, this.buffer);
                }
                GL11.glEnableClientState(32884);
                if (this.hasTexture) {
                    GL11.glEnableClientState(32888);
                }
                if (this.hasColor) {
                    GL11.glEnableClientState(32886);
                }
                GL11.glDrawArrays(7, 0, this.vertices);
                GL11.glDisableClientState(32884);
                if (this.hasTexture) {
                    GL11.glDisableClientState(32888);
                }
                if (this.hasColor) {
                    GL11.glDisableClientState(32886);
                }
            }
            this.clear();
        }

        private void clear() {
            this.vertices = 0;
            this.buffer.clear();
            this.p = 0;
        }

        void begin() {
            this.clear();
            this.hasColor = false;
            this.hasTexture = false;
            this.noColor = false;
        }

        void tex(float u, float v) {
            if (!this.hasTexture) {
                this.len += 2;
            }
            this.hasTexture = true;
            this.u = u;
            this.v = v;
        }

        void color(int r, int g, int b) {
            this.color((byte) r, (byte) g, (byte) b);
        }

        void color(byte r, byte g, byte b) {
            if (this.noColor) {
                return;
            }
            if (!this.hasColor) {
                this.len += 3;
            }
            this.hasColor = true;
            this.r = (float) (r & 0xFF) / 255.0f;
            this.g = (float) (g & 0xFF) / 255.0f;
            this.b = (float) (b & 0xFF) / 255.0f;
        }

        void vertexUV(double x, double y, double z, float u, float v) {
            this.tex(u, v);
            this.vertex((float) x, (float) y, (float) z);
        }

        void vertex(float x, float y, float z) {
            if (this.hasTexture) {
                this.array[this.p++] = this.u;
                this.array[this.p++] = this.v;
            }
            if (this.hasColor) {
                this.array[this.p++] = this.r;
                this.array[this.p++] = this.g;
                this.array[this.p++] = this.b;
            }
            this.array[this.p++] = x;
            this.array[this.p++] = y;
            this.array[this.p++] = z;
            ++this.vertices;
            if (this.vertices % 4 == 0 && this.p >= 524288 - this.len * 4) {
                this.end();
            }
        }
    }
}
