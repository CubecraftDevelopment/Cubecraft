package ink.flybird.cubecraft.client.render.chunk.sort;

import ink.flybird.cubecraft.client.render.RenderType;
import ink.flybird.cubecraft.client.render.chunk.compile.ChunkCompileRequest;
import ink.flybird.cubecraft.internal.entity.EntityPlayer;
import ink.flybird.quantum3d_legacy.Camera;
import ink.flybird.quantum3d_legacy.culling.FrustumCuller;

import java.util.Comparator;

public final class ChunkCompileRequestSorter implements Comparator<ChunkCompileRequest> {
    private final FrustumCuller frustum;
    private final Camera camera;
    private final EntityPlayer player;

    public ChunkCompileRequestSorter(FrustumCuller frustum, Camera camera, EntityPlayer player) {
        this.frustum = frustum;
        this.camera = camera;
        this.player = player;
    }

    @Override
    public int compare(ChunkCompileRequest o1, ChunkCompileRequest o2) {
        if (o1 == o2) {
            return 0;
        }
        if (o1 == null) {
            return 1;
        }
        if (o2 == null) {
            return -1;
        }
        if (o1.getLayer()==null) {
            return 1;
        }
        if (o2.getLayer()==null) {
            return -1;
        }
        int order=ChunkSorter.getVisibleOrderInAABB(this.frustum, this.player, this.camera, o1.getPos(), o2.getPos());
        if(order!=0){
            return order;
        }
        if(o1.getLayer().getRenderType()!=RenderType.ALPHA){
            return 1;
        }
        if(o2.getLayer().getRenderType()!=RenderType.ALPHA){
            return -1;
        }
        throw new RuntimeException("wtf is this sorting?"+ o1 +","+ o2);
    }
}
