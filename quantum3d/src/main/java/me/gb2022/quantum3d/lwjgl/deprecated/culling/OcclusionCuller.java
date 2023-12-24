package me.gb2022.quantum3d.lwjgl.deprecated.culling;

import ink.flybird.fcommon.math.AABB;
import me.gb2022.quantum3d.lwjgl.deprecated.BufferAllocation;
import me.gb2022.quantum3d.lwjgl.deprecated.Camera;
import me.gb2022.quantum3d.lwjgl.deprecated.GLUtil;
import me.gb2022.quantum3d.lwjgl.deprecated.drawcall.IRenderCall;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.system.MemoryUtil;

import java.nio.IntBuffer;

public class OcclusionCuller extends ICuller {
    private final IntBuffer buffer = BufferAllocation.allocIntBuffer(1);

    public OcclusionCuller(Camera camera) {
        super(camera);
    }

    public boolean listVisible(IRenderCall list) {
        GL11.glColorMask(false, false, false, false);
        GL11.glDepthMask(false);
        boolean b = _listVisible(list);
        GL11.glColorMask(true, true, true, true);
        GL11.glDepthMask(true);
        return b;
    }

    public boolean _listVisible(IRenderCall list) {
        GLUtil.checkError("occlusion:pre_check");
        //关闭颜色写入
        //查询像素通过数量
        int queryID = GL15.glGenQueries();
        GL15.glBeginQuery(GL15.GL_SAMPLES_PASSED, queryID);
        list.call();
        GL15.glEndQuery(GL15.GL_SAMPLES_PASSED);
        //重新打开颜色写入
        buffer.clear();
        GL15.glGetQueryObjectiv(queryID, GL15.GL_QUERY_RESULT, buffer);
        int samples = buffer.get(0);
        GL15.glDeleteQueries(queryID);
        return samples > 0;
    }

    @Override
    public boolean[] listsVisible(IRenderCall[] calls) {
        GL11.glColorMask(false, false, false, false);
        GL11.glDepthMask(false);
        boolean[] b = new boolean[calls.length];
        int i = 0;
        for (IRenderCall call : calls) {
            //查询像素通过数量
            int queryID = GL15.glGenQueries();
            GL15.glBeginQuery(GL15.GL_SAMPLES_PASSED, queryID);
            call.call();
            GL15.glEndQuery(GL15.GL_SAMPLES_PASSED);
            //重新打开颜色写入
            IntBuffer buf = MemoryUtil.memAllocInt(1);
            GL15.glGetQueryObjectiv(queryID, GL15.GL_QUERY_RESULT, buf);
            int samples = buf.get(0);
            GL15.glDeleteQueries(queryID);
            MemoryUtil.memFree(buf);
            b[i] = samples > 0;
            i++;
        }
        //关闭颜色写入
        GL11.glColorMask(true, true, true, true);
        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_CULL_FACE);
        return b;
    }

    @Override
    public boolean aabbVisible(AABB aabb) {
        //todo
        return false;
    }
}
