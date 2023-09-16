package ink.flybird.quantum3d_legacy;

import org.lwjgl.opengl.*;

public class FrameBuffer {
    int glID;
    public void alloc(){
        this.glID= GL30.glGenFramebuffers();
    }
}
