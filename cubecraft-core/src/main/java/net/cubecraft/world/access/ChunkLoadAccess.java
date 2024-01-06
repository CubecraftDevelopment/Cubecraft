package net.cubecraft.world.access;

import net.cubecraft.world.IWorld;
import net.cubecraft.world.chunk.task.ChunkLoadTicket;
import net.cubecraft.world.chunk.pos.ChunkPos;

public interface ChunkLoadAccess {
    //load
    static void loadChunkRange(IWorld world, ChunkPos pos, int range, ChunkLoadTicket ticket) {
        long centerCX = pos.getX();
        long centerCZ = pos.getZ();
        for (long x = centerCX - range; x <= centerCX + range; x++) {
            for (long z = centerCZ - range; z <= centerCZ + range; z++) {
                world.loadChunk(ChunkPos.create(x, z), ticket);
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
        long centerCX = pos.getX();
        long centerCZ = pos.getZ();
        for (long x = centerCX - range; x <= centerCX + range; x++) {
            for (long z = centerCZ - range; z <= centerCZ + range; z++) {
                ChunkPos p=ChunkPos.create(x, z);
                world.loadChunk(p,ChunkLoadTicket.LOAD_DATA);
                //todo:hotspot
                world.waitUntilChunkExist(p);
                world.addChunkLock(p,caller);
            }
        }
    }

    static void removeChunkLockRange(IWorld world, ChunkPos pos, int range,Object caller) {
        long centerCX = pos.getX();
        long centerCZ = pos.getZ();
        for (long x = centerCX - range; x <= centerCX + range; x++) {
            for (long z = centerCZ - range; z <= centerCZ + range; z++) {
                ChunkPos p=ChunkPos.create(x, z);
                world.loadChunk(p,ChunkLoadTicket.LOAD_DATA);
                world.waitUntilChunkExist(p);
                world.removeChunkLock(p,caller);
            }
        }
    }
}
