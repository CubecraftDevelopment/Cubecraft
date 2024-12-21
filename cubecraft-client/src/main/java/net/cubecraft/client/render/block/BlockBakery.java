package net.cubecraft.client.render.block;

import net.cubecraft.client.registry.ClientSettings.RenderSetting.WorldSetting.ChunkSetting;
import net.cubecraft.client.render.model.object.Vertex;
import net.cubecraft.world.BlockAccessor;
import net.cubecraft.world.block.access.BlockAccess;
import net.cubecraft.world.block.property.BlockPropertyDispatcher;
import org.joml.Vector3f;


public interface BlockBakery {
    double CLASSIC_LIGHT_1 = 1;
    double CLASSIC_LIGHT_2 = 0.8;
    double CLASSIC_LIGHT_3 = 0.6;
    double CLASSIC_LIGHT_4 = 0.5;

    @SuppressWarnings({"DuplicatedCode"})
    static double getSmoothedLight(BlockAccessor world, long x, long y, long z, float rx, float ry, float rz, int f) {
        var v = 0.375;

        if (f == 0) {
            var mod = 1;

            //axis
            if (rx == 0 && BlockPropertyDispatcher.isSolid(world.getBlockAccess(x - 1, y + 1, z))) {
                return v * mod;
            }
            if (rx == 1 && BlockPropertyDispatcher.isSolid(world.getBlockAccess(x + 1, y + 1, z))) {
                return v * mod;
            }
            if (rz == 0 && BlockPropertyDispatcher.isSolid(world.getBlockAccess(x, y + 1, z - 1))) {
                return v * mod;
            }
            if (rz == 1 && BlockPropertyDispatcher.isSolid(world.getBlockAccess(x, y + 1, z + 1))) {
                return v;
            }

            //corner
            if (rx == 0 && rz == 0 && BlockPropertyDispatcher.isSolid(world.getBlockAccess(x - 1, y + 1, z - 1))) {
                return v * mod;
            }
            if (rx == 1 && rz == 1 && BlockPropertyDispatcher.isSolid(world.getBlockAccess(x + 1, y + 1, z + 1))) {
                return v * mod;
            }
            if (rx == 0 && rz == 1 && BlockPropertyDispatcher.isSolid(world.getBlockAccess(x - 1, y + 1, z + 1))) {
                return v * mod;
            }
            if (rx == 1 && rz == 0 && BlockPropertyDispatcher.isSolid(world.getBlockAccess(x + 1, y + 1, z - 1))) {
                return v * mod;
            }
        }

        if (f == 1) {
            var mod = 1;

            //axis
            if (rx == 0 && BlockPropertyDispatcher.isSolid(world.getBlockAccess(x - 1, y - 1, z))) {
                return v * mod;
            }
            if (rx == 1 && BlockPropertyDispatcher.isSolid(world.getBlockAccess(x + 1, y - 1, z))) {
                return v * mod;
            }
            if (rz == 0 && BlockPropertyDispatcher.isSolid(world.getBlockAccess(x, y - 1, z - 1))) {
                return v * mod;
            }
            if (rz == 1 && BlockPropertyDispatcher.isSolid(world.getBlockAccess(x, y - 1, z + 1))) {
                return v * mod;
            }

            //corner
            if (rx == 0 && rz == 0 && BlockPropertyDispatcher.isSolid(world.getBlockAccess(x - 1, y - 1, z - 1))) {
                return v * mod;
            }
            if (rx == 1 && rz == 1 && BlockPropertyDispatcher.isSolid(world.getBlockAccess(x + 1, y - 1, z + 1))) {
                return v * mod;
            }
            if (rx == 0 && rz == 1 && BlockPropertyDispatcher.isSolid(world.getBlockAccess(x - 1, y - 1, z + 1))) {
                return v * mod;
            }
            if (rx == 1 && rz == 0 && BlockPropertyDispatcher.isSolid(world.getBlockAccess(x + 1, y - 1, z - 1))) {
                return v * mod;
            }
        }

        if (f == 2) {
            var mod = 1;

            //axis
            if (ry == 0 && BlockPropertyDispatcher.isSolid(world.getBlockAccess(x, y - 1, z + 1))) {
                return v * mod;
            }
            if (ry == 1 && BlockPropertyDispatcher.isSolid(world.getBlockAccess(x, y + 1, z + 1))) {
                return v * mod;
            }
            if (rx == 0 && BlockPropertyDispatcher.isSolid(world.getBlockAccess(x - 1, y, z + 1))) {
                return v * mod;
            }
            if (rx == 1 && BlockPropertyDispatcher.isSolid(world.getBlockAccess(x + 1, y, z + 1))) {
                return v * mod;
            }

            //corner
            if (ry == 1 && rx == 1 && BlockPropertyDispatcher.isSolid(world.getBlockAccess(x + 1, y + 1, z + 1))) {
                return v * mod;
            }
            if (ry == 1 && rx == 0 && BlockPropertyDispatcher.isSolid(world.getBlockAccess(x - 1, y + 1, z + 1))) {
                return v * mod;
            }
            if (ry == 0 && rx == 1 && BlockPropertyDispatcher.isSolid(world.getBlockAccess(x + 1, y - 1, z + 1))) {
                return v * mod;
            }
            if (ry == 0 && rx == 0 && BlockPropertyDispatcher.isSolid(world.getBlockAccess(x - 1, y - 1, z + 1))) {
                return v * mod;
            }
        }

        if (f == 3) {
            var mod = 1;

            //axis
            if (ry == 0 && BlockPropertyDispatcher.isSolid(world.getBlockAccess(x, y - 1, z - 1))) {
                return v * mod;
            }
            if (ry == 1 && BlockPropertyDispatcher.isSolid(world.getBlockAccess(x, y + 1, z - 1))) {
                return v * mod;
            }
            if (rx == 0 && BlockPropertyDispatcher.isSolid(world.getBlockAccess(x - 1, y, z - 1))) {
                return v * mod;
            }
            if (rx == 1 && BlockPropertyDispatcher.isSolid(world.getBlockAccess(x + 1, y, z - 1))) {
                return v * mod;
            }

            //corner
            if (ry == 1 && rx == 1 && BlockPropertyDispatcher.isSolid(world.getBlockAccess(x + 1, y + 1, z - 1))) {
                return v * mod;
            }
            if (ry == 1 && rx == 0 && BlockPropertyDispatcher.isSolid(world.getBlockAccess(x - 1, y + 1, z - 1))) {
                return v * mod;
            }
            if (ry == 0 && rx == 1 && BlockPropertyDispatcher.isSolid(world.getBlockAccess(x + 1, y - 1, z - 1))) {
                return v * mod;
            }
            if (ry == 0 && rx == 0 && BlockPropertyDispatcher.isSolid(world.getBlockAccess(x - 1, y - 1, z - 1))) {
                return v * mod;
            }
        }

        if (f == 4) {
            var mod = 1;

            //axis
            if (ry == 0 && BlockPropertyDispatcher.isSolid(world.getBlockAccess(x + 1, y - 1, z))) {
                return v * mod;
            }
            if (ry == 1 && BlockPropertyDispatcher.isSolid(world.getBlockAccess(x + 1, y + 1, z))) {
                return v * mod;
            }
            if (rz == 0 && BlockPropertyDispatcher.isSolid(world.getBlockAccess(x + 1, y, z - 1))) {
                return v * mod;
            }
            if (rz == 1 && BlockPropertyDispatcher.isSolid(world.getBlockAccess(x + 1, y, z + 1))) {
                return v * mod;
            }

            //corner
            if (ry == 1 && rz == 1 && BlockPropertyDispatcher.isSolid(world.getBlockAccess(x + 1, y + 1, z + 1))) {
                return v * mod;
            }
            if (ry == 1 && rz == 0 && BlockPropertyDispatcher.isSolid(world.getBlockAccess(x + 1, y + 1, z - 1))) {
                return v * mod;
            }
            if (ry == 0 && rz == 1 && BlockPropertyDispatcher.isSolid(world.getBlockAccess(x + 1, y - 1, z + 1))) {
                return v * mod;
            }
            if (ry == 0 && rz == 0 && BlockPropertyDispatcher.isSolid(world.getBlockAccess(x + 1, y - 1, z - 1))) {
                return v * mod;
            }
        }

        if (f == 5) {
            var mod = 1;

            //axis
            if (ry == 0 && BlockPropertyDispatcher.isSolid(world.getBlockAccess(x - 1, y - 1, z))) {
                return v * mod;
            }
            if (ry == 1 && BlockPropertyDispatcher.isSolid(world.getBlockAccess(x - 1, y + 1, z))) {
                return v * mod;
            }
            if (rz == 0 && BlockPropertyDispatcher.isSolid(world.getBlockAccess(x - 1, y, z - 1))) {
                return v * mod;
            }
            if (rz == 1 && BlockPropertyDispatcher.isSolid(world.getBlockAccess(x - 1, y, z + 1))) {
                return v * mod;
            }

            //corner
            if (ry == 1 && rz == 1 && BlockPropertyDispatcher.isSolid(world.getBlockAccess(x - 1, y + 1, z + 1))) {
                return v * mod;
            }
            if (ry == 1 && rz == 0 && BlockPropertyDispatcher.isSolid(world.getBlockAccess(x - 1, y + 1, z - 1))) {
                return v * mod;
            }
            if (ry == 0 && rz == 1 && BlockPropertyDispatcher.isSolid(world.getBlockAccess(x - 1, y - 1, z + 1))) {
                return v * mod;
            }
            if (ry == 0 && rz == 0 && BlockPropertyDispatcher.isSolid(world.getBlockAccess(x - 1, y - 1, z - 1))) {
                return v * mod;
            }
        }

        return 0;
    }

    static double bakeLight(BlockAccessor w, float vx, float vy, float vz, BlockAccess b, int f) {
        var mod = 1.0d;

        if (ChunkSetting.CLASSIC_LIGHTING.getValue()) {
            mod *= getClassicLight(f);
        }

        if (b == null || w == null) {
            return mod;
        }

        var bx = ((b.getX()+ 32768) % 65535) - 32768;
        var bz = ((b.getZ() + 32768) % 65535) - 32768;


        if (ChunkSetting.AMBIENT_OCCLUSION.getValue()) {
            var rx = vx - bx;
            var ry = vy - b.getY();
            var rz = vz - bz;

            var c2 = (1 - getSmoothedLight(w, b.getX(), b.getY(), b.getZ(), rx, ry, rz, f));

            mod *= c2;
        }

        return b.near(w, f, 1).getBlockLight() / 128d * mod;
    }

    @Deprecated
    static Vertex bakeVertex(Vertex v, Vector3f pos, BlockAccessor w, long x, long y, long z, int face) {
        if (w == null) {
            v.multiplyColor(bakeLight(null, v.pos().x(), v.pos().y(), v.pos().z(), null, face));
        } else {
            v.multiplyColor(bakeLight(w, v.pos().x(), v.pos().y(), v.pos().z(), w.getBlockAccess(x, y, z), face));
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
