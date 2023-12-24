package me.gb2022.quantum3d.lwjgl.deprecated.culling;

import ink.flybird.fcommon.math.AABB;
import me.gb2022.quantum3d.lwjgl.deprecated.Camera;
import me.gb2022.quantum3d.lwjgl.deprecated.drawcall.IRenderCall;

import java.util.Arrays;

public abstract class ICuller {
    final Camera camera;

    public ICuller(Camera camera) {
        this.camera = camera;
    }


    public boolean listVisible(IRenderCall list) {
        return true;
    }

    public boolean[] listsVisible(IRenderCall[] calls) {
        boolean[] b = new boolean[calls.length];
        Arrays.fill(b, true);
        return b;
    }

    public boolean aabbVisible(AABB aabb) {
        return true;
    }

    public void update() {

    }
}
