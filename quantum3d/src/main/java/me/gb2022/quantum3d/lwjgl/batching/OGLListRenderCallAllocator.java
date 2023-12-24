package me.gb2022.quantum3d.lwjgl.batching;

import me.gb2022.quantum3d.render.command.RenderCall;
import me.gb2022.quantum3d.render.command.RenderCallAllocator;

public class OGLListRenderCallAllocator extends RenderCallAllocator {

    @Override
    public RenderCall create() {
        return new OGLListRenderCall();
    }
}
