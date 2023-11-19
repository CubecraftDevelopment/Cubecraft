package net.cubecraft.client.render;

import net.cubecraft.client.ClientSettingRegistry;
import net.cubecraft.client.render.model.object.Vertex;
import net.cubecraft.world.IWorld;
import net.cubecraft.world.block.EnumFacing;
import net.cubecraft.world.block.access.IBlockAccess;
import ink.flybird.fcommon.container.Vector3;
import ink.flybird.fcommon.math.MathHelper;
import net.cubecraft.world.block.property.BlockPropertyDispatcher;
import org.joml.Vector3d;

import java.util.Arrays;


public interface BlockBakery {
    double CLASSIC_LIGHT_1 = 1;
    double CLASSIC_LIGHT_2 = 0.8;
    double CLASSIC_LIGHT_3 = 0.6;
    double CLASSIC_LIGHT_4 = 0.5;
    /*
            double bv000 = world.getBlockAccess(x - 1, y - 1, z - 1).getBlockLight();
        double v000 = MathHelper.min3(
                Math.min(bv000, world.getBlockAccess(x, y - 1, z - 1).getBlockLight()),
                Math.min(bv000, world.getBlockAccess(x - 1, y, z - 1).getBlockLight()),
                Math.min(bv000, world.getBlockAccess(x - 1, y - 1, z).getBlockLight())
        );

        double bv001 = world.getBlockAccess(x - 1, y - 1, z + 1).getBlockLight();
        double v001 = MathHelper.min3(
                Math.min(bv001, world.getBlockAccess(x, y - 1, z + 1).getBlockLight()),
                Math.min(bv001, world.getBlockAccess(x - 1, y, z + 1).getBlockLight()),
                Math.min(bv001, world.getBlockAccess(x - 1, y - 1, z).getBlockLight())
        );

        double bv010 = world.getBlockAccess(x - 1, y + 1, z - 1).getBlockLight();
        double v010 = MathHelper.min3(
                Math.min(bv010, world.getBlockAccess(x, y + 1, z - 1).getBlockLight()),
                Math.min(bv010, world.getBlockAccess(x - 1, y, z - 1).getBlockLight()),
                Math.min(bv010, world.getBlockAccess(x - 1, y + 1, z).getBlockLight())
        );

        double bv011 = world.getBlockAccess(x - 1, y - 1, z + 1).getBlockLight();
        double v011 = MathHelper.min3(
                Math.min(bv011, world.getBlockAccess(x, y + 1, z + 1).getBlockLight()),
                Math.min(bv011, world.getBlockAccess(x - 1, y, z + 1).getBlockLight()),
                Math.min(bv011, world.getBlockAccess(x - 1, y + 1, z).getBlockLight())
        );

        double bv100 = world.getBlockAccess(x + 1, y - 1, z - 1).getBlockLight();
        double v100 = MathHelper.min3(
                Math.min(bv100, world.getBlockAccess(x, y - 1, z - 1).getBlockLight()),
                Math.min(bv100, world.getBlockAccess(x + 1, y, z - 1).getBlockLight()),
                Math.min(bv100, world.getBlockAccess(x + 1, y - 1, z).getBlockLight())
        );

        double bv101 = world.getBlockAccess(x + 1, y - 1, z + 1).getBlockLight();
        double v101 = MathHelper.min3(
                Math.min(bv101, world.getBlockAccess(x, y - 1, z + 1).getBlockLight()),
                Math.min(bv101, world.getBlockAccess(x + 1, y, z + 1).getBlockLight()),
                Math.min(bv101, world.getBlockAccess(x + 1, y - 1, z).getBlockLight())
        );

        double bv110 = world.getBlockAccess(x + 1, y + 1, z - 1).getBlockLight();
        double v110 = MathHelper.min3(
                Math.min(bv110, world.getBlockAccess(x, y + 1, z - 1).getBlockLight()),
                Math.min(bv110, world.getBlockAccess(x + 1, y, z - 1).getBlockLight()),
                Math.min(bv110, world.getBlockAccess(x + 1, y + 1, z).getBlockLight())
        );

        double bv111 = world.getBlockAccess(x + 1, y - 1, z + 1).getBlockLight();
        double v111 = MathHelper.min3(
                Math.min(bv111, world.getBlockAccess(x, y + 1, z + 1).getBlockLight()),
                Math.min(bv111, world.getBlockAccess(x + 1, y, z + 1).getBlockLight()),
                Math.min(bv111, world.getBlockAccess(x + 1, y + 1, z).getBlockLight())
        );

        return MathHelper.linear_interpolate3d(
                v000,v001,v010,v011,v100,v101,v110,v111,
                relativePos
        );

     */


    static float[] getTopAO(IWorld world, long x, long y, long z) {
        //00,01,10,11
        float[] result = new float[4];

        IBlockAccess blockEPM = world.getBlockAccess(x, y + 1, z - 1);
        IBlockAccess blockMPE = world.getBlockAccess(x - 1, y + 1, z);
        IBlockAccess blockPPE = world.getBlockAccess(x + 1, y + 1, z);
        IBlockAccess blockEPP = world.getBlockAccess(x, y + 1, z + 1);

        if (BlockPropertyDispatcher.isSolid(blockMPE)) {
            result[0] = 0.5f;
            result[1] = 0.5f;
        }

        if (BlockPropertyDispatcher.isSolid(blockPPE)) {
            result[2] = 0.5f;
            result[3] = 0.5f;
        }

        if (BlockPropertyDispatcher.isSolid(blockEPM)) {
            result[0] = 0.5f;
            result[2] = 0.5f;
        }

        if (BlockPropertyDispatcher.isSolid(blockEPP)) {
            result[1] = 0.5f;
            result[3] = 0.5f;
        }

        return result;
    }


    static float[] getAOModelModifier(IWorld world, long x, long y, long z) {
        float[] result = new float[8];
        Arrays.fill(result, 1.0f);


        IBlockAccess blockMPM = world.getBlockAccess(x - 1, y + 1, z - 1);
        IBlockAccess blockEPM = world.getBlockAccess(x, y + 1, z - 1);
        IBlockAccess blockPPM = world.getBlockAccess(x + 1, y + 1, z - 1);
        IBlockAccess blockMPE = world.getBlockAccess(x - 1, y + 1, z);
        IBlockAccess blockEPE = world.getBlockAccess(x, y + 1, z);
        IBlockAccess blockPPE = world.getBlockAccess(x + 1, y + 1, z);
        IBlockAccess blockMPP = world.getBlockAccess(x - 1, y + 1, z + 1);
        IBlockAccess blockEPP = world.getBlockAccess(x, y + 1, z + 1);
        IBlockAccess blockPPP = world.getBlockAccess(x + 1, y + 1, z + 1);


        IBlockAccess blockEMM = world.getBlockAccess(x, y - 1, z - 1);
        IBlockAccess blockMME = world.getBlockAccess(x - 1, y - 1, z);
        IBlockAccess blockMMM = world.getBlockAccess(x - 1, y - 1, z - 1);


        //v000,v001,v010,v011,v100,v101,v110,v111,

        if (BlockPropertyDispatcher.isSolid(blockEPM) && BlockPropertyDispatcher.isSolid(blockMPE)) {
            result[2] = 0.5f;
        }

        if (!(BlockPropertyDispatcher.isSolid(blockEPM) && BlockPropertyDispatcher.isSolid(blockMPE)) && BlockPropertyDispatcher.isSolid(blockMPM)) {
            result[2] = 0.5f;
        }


        if (BlockPropertyDispatcher.isSolid(blockEPP) && BlockPropertyDispatcher.isSolid(blockMPP)) {
            result[3] = 0.5f;
        }

        if (BlockPropertyDispatcher.isSolid(blockMPP)) {
            result[3] = 0.5f;
        }


        /*
        if (blockEPM) && blockMPE)) {
            result[2] = 0.5f;
        }

        if (blockMPM)) {
            result[2] =
             0.5f;

        }*/


        return result;
    }


    static double getSmoothedLight(IWorld world, long x, long y, long z, Vector3d relativePos) {


        float[] cornerResults = new float[8];
        // 定义8个顶点的坐标偏移量
        int[][] cornerOffsets = {
                {-1, -1, -1},
                {-1, -1, 1},
                {-1, 1, -1},
                {-1, 1, 1},
                {1, -1, -1},
                {1, -1, 1},
                {1, 1, -1},
                {1, 1, 1},
        };


        boolean[] mark = new boolean[8];
        Arrays.fill(mark, false);
        // 遍历每个顶点的坐标偏移量
        for (int i = 0; i < 8; i++) {
            boolean[] adjacentBlocks = new boolean[3];
            byte[] adjacentLights = new byte[3];

            int[] offset = cornerOffsets[i];
            long cornerX = x + offset[0];
            long cornerY = y + offset[1];
            long cornerZ = z + offset[2];

            // 检查当前顶点的三个相邻方块

            adjacentBlocks[0] = BlockPropertyDispatcher.isSolid(world.getBlockAccess(cornerX, y, cornerZ)); // 上下方向
            adjacentBlocks[1] = BlockPropertyDispatcher.isSolid(world.getBlockAccess(x, cornerY, cornerZ)); // 左右方向
            adjacentBlocks[2] = BlockPropertyDispatcher.isSolid(world.getBlockAccess(cornerX, cornerY, z)); // 前后方向

            adjacentLights[0] = world.getBlockAccess(cornerX, y, cornerZ).getBlockLight(); // 上下方向
            adjacentLights[1] = world.getBlockAccess(x, cornerY, cornerZ).getBlockLight(); // 左右方向
            adjacentLights[2] = world.getBlockAccess(cornerX, cornerY, z).getBlockLight(); // 前后方
            // 判定凹拐角条件：存在两个相邻方块间的夹角等于90度

            cornerResults[i] = (float) MathHelper.max(adjacentLights[0], adjacentLights[1], adjacentLights[2]);

            if ((adjacentBlocks[0] && adjacentBlocks[1])
                    || (adjacentBlocks[0] && adjacentBlocks[2])
                    || (adjacentBlocks[1] && adjacentBlocks[2])
            ) {
                if (mark[i]) {
                    continue;
                }
                cornerResults[i] *= 0.75f;
                mark[i] = true;
            }
        }

        //cornerResults = getAOModelModifier(world, x, y, z);

        return MathHelper.linear_interpolate3d(
                cornerResults[0],
                cornerResults[1],
                cornerResults[2],
                cornerResults[3],
                cornerResults[4],
                cornerResults[5],
                cornerResults[6],
                cornerResults[7],
                relativePos
        );


        //_000,_001,_010,_011,_100,_101,_110,_111


        /*
        if (blockEME) && blockEPE)) {
            return 0.5;
        }
        if (blockEME)) {
            return MathHelper.linearInterpolate(0.5, 1, relativePos.y);
        }

        if (blockEPE)) {
            return MathHelper.linearInterpolate(1, 0.5, relativePos.y);
        }
        world.getBlockAccess(x - 1, y - 1, z - 1).getBlockLight(),
                world.getBlockAccess(x - 1, y - 1, z + 1).getBlockLight(),
                world.getBlockAccess(x - 1, y + 1, z - 1).getBlockLight(),
                world.getBlockAccess(x - 1, y + 1, z + 1).getBlockLight(),
                world.getBlockAccess(x + 1, y - 1, z - 1).getBlockLight(),
                world.getBlockAccess(x + 1, y - 1, z + 1).getBlockLight(),
                world.getBlockAccess(x + 1, y + 1, z - 1).getBlockLight(),
                world.getBlockAccess(x + 1, y + 1, z + 1).getBlockLight(),
         */


        //todo:smooth light
    }


    static Vertex bakeVertex(Vertex v, Vector3d pos, IWorld w, long x, long y, long z, int face) {
        if (ClientSettingRegistry.CHUNK_USE_AO.getValue()) {
            v.multiplyColor(getSmoothedLight(w, x, y, z, pos) / 128d);
        } else {
            Vector3<Long> v2 = EnumFacing.fromId(face).findNear(x, y, z, 1);
            v.multiplyColor((int) w.getBlockAccess(v2.x(), v2.y(), v2.z()).getBlockLight() / 128d);
        }
        if (ClientSettingRegistry.CHUNK_CLASSIC_LIGHTING.getValue()) {
            v.multiplyColor(getClassicLight(face));
        }
        return v;
    }

    static double getClassicLight(int face) {
        return switch (face) {
            case 0 -> CLASSIC_LIGHT_1;
            case 1 -> CLASSIC_LIGHT_4;
            case 2, 3 -> CLASSIC_LIGHT_2;
            case 4, 5 -> CLASSIC_LIGHT_3;
            default -> throw new IllegalStateException("Unexpected value: " + face);
        };
    }
}
