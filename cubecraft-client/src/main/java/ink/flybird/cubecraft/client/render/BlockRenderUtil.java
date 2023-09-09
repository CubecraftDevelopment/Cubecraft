package ink.flybird.cubecraft.client.render;

import ink.flybird.cubecraft.client.CubecraftClient;
import ink.flybird.cubecraft.client.render.model.object.Vertex;
import io.flybird.cubecraft.world.IWorld;
import io.flybird.cubecraft.world.block.EnumFacing;
import ink.flybird.fcommon.container.Vector3;
import org.joml.Vector3d;


public interface BlockRenderUtil {
    public static double CLASSIC_LIGHT_1 = 1;
    public static double CLASSIC_LIGHT_2 = 0.8;
    public static final double CLASSIC_LIGHT_3 = 0.6;
    public static final double CLASSIC_LIGHT_4 = 0.5;

    public static double getSmoothedLight(IWorld world, long x, long y, long z, Vector3d relativePos) {
        return world.getBlockAccess(x, y, z).getBlockLight();
        //_000,_001,_010,_011,_100,_101,_110,_111
        //todo:smooth light
    }

    public static Vertex bakeVertex(Vertex v, Vector3d pos, IWorld w, long x, long y, long z, int face) {
        if (CubecraftClient.CLIENT.getGameSetting().getValueAsBoolean("client.render.terrain.use_smooth_lighting", true)) {
            v.multiplyColor(BlockRenderUtil.getSmoothedLight(w, x, y, z, pos) / 128d);
        } else {
            Vector3<Long> v2 = EnumFacing.fromId(face).findNear(x, y, z, 1);
            v.multiplyColor((int) w.getBlockAccess(v2.x(), v2.y(), v2.z()).getBlockLight() / 128d);
        }
        if (CubecraftClient.CLIENT.getGameSetting().getValueAsBoolean("client.render.terrain.use_classic_lighting", false)) {
            v.multiplyColor(getClassicLight(face));
        }
        return v;
    }

    public static double getClassicLight(int face) {
        return switch (face) {
            case 0 -> CLASSIC_LIGHT_1;
            case 1-> CLASSIC_LIGHT_4;
            case 2, 3 -> CLASSIC_LIGHT_2;
            case 4, 5 -> CLASSIC_LIGHT_3;
            default -> throw new IllegalStateException("Unexpected value: " + face);
        };
    }

}
