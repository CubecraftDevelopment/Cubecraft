package ink.flybird.quantum3d_legacy.culling;

import ink.flybird.quantum3d_legacy.Camera;
import ink.flybird.quantum3d_legacy.drawcall.IRenderCall;
import ink.flybird.fcommon.math.AABB;

import java.util.Arrays;

public abstract class ICuller {
    final Camera camera;

    public ICuller(Camera camera){
        this.camera = camera;
    }


    public boolean listVisible(IRenderCall list) {
        return true;
    }

    public boolean[] listsVisible(IRenderCall[] calls){
        boolean[] b=new boolean[calls.length];
        Arrays.fill(b,true);
        return b;
    }

    public boolean aabbVisible(AABB aabb) {
        return true;
    }

    public void update() {

    }
}
