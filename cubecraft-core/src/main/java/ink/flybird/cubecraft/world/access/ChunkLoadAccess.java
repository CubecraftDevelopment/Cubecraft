package ink.flybird.cubecraft.world.access;

import ink.flybird.cubecraft.world.IWorld;
import ink.flybird.cubecraft.world.chunk.ChunkLoadTicket;
import ink.flybird.cubecraft.world.chunk.ChunkPos;

public interface ChunkLoadAccess {
    //load
    static void loadChunkRange(IWorld world, ChunkPos pos, int range, ChunkLoadTicket ticket) {
        long centerCX = pos.x();
        long centerCZ = pos.z();
        for (long x = centerCX - range; x <= centerCX + range; x++) {
            for (long z = centerCZ - range; z <= centerCZ + range; z++) {
                world.loadChunk(new ChunkPos(x, z), ticket);
            }
        }
    }

    static void loadChunkAndNear(IWorld world, ChunkPos pos, ChunkLoadTicket ticket) {
        world.loadChunk(pos, ticket);
        for (ChunkPos p : pos.getAllNear()) {
            world.loadChunk(p, ticket);
        }
    }


    //lock
    static void addChunkLockAndNear(IWorld world, ChunkPos pos,Object caller) {
        waitUntilChunkExistAndNear(world, pos);
        world.getChunk(pos).getDataLock().addLock(caller);
        for (ChunkPos p : pos.getAllNear()) {
            world.addChunkLock(p,caller);
        }
    }

    static void removeChunkLockAndNear(IWorld world, ChunkPos pos,Object caller) {
        waitUntilChunkExistAndNear(world, pos);
        world.getChunk(pos).getDataLock().removeLock(caller);
        for (ChunkPos p : pos.getAllNear()) {
            world.removeChunkLock(p,caller);
        }
    }

    static void waitUntilChunkExistAndNear(IWorld world, ChunkPos pos) {
        while (true) {
            boolean b = true;
            if (!world.isChunkLoaded(pos)) {
                continue;
            }
            for (ChunkPos p : pos.getAllNear()) {
                if (!world.isChunkLoaded(p)) {
                    b = false;
                }
            }
            if (b) {
                return;
            }
            Thread.yield();
        }
    }

    static void addChunkLockRange(IWorld world, ChunkPos pos, int range,Object caller) {
        long centerCX = pos.x();
        long centerCZ = pos.z();
        for (long x = centerCX - range; x <= centerCX + range; x++) {
            for (long z = centerCZ - range; z <= centerCZ + range; z++) {
                world.addChunkLock(new ChunkPos(x, z),caller);
            }
        }
    }

    static void removeChunkLockRange(IWorld world, ChunkPos pos, int range,Object caller) {
        long centerCX = pos.x();
        long centerCZ = pos.z();
        for (long x = centerCX - range; x <= centerCX + range; x++) {
            for (long z = centerCZ - range; z <= centerCZ + range; z++) {
                world.removeChunkLock(new ChunkPos(x, z),caller);
            }
        }
    }
}
