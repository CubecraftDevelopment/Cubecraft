package net.cubecraft.client.render.chunk.compile;

import net.cubecraft.client.render.chunk.RenderChunkPos;
import net.cubecraft.world.BlockAccessor;
import net.cubecraft.world.World;
import net.cubecraft.world.block.access.IBlockAccess;
import net.cubecraft.world.block.access.NonLoadedBlockAccess;
import net.cubecraft.world.chunk.WorldChunk;
import net.cubecraft.world.chunk.pos.ChunkPos;
import net.cubecraft.world.chunk.task.ChunkLoadTicket;

import java.util.Objects;

public final class ChunkCompileRegion implements BlockAccessor {
    public final long cx;
    public final long cy;
    public final long cz;

    private final World world;
    private final IBlockAccess[][][] data;

    public ChunkCompileRegion(World world, RenderChunkPos pos, IBlockAccess[][][] data) {
        this.world = world;

        this.cx = pos.getX();
        this.cy = pos.getY();
        this.cz = pos.getZ();

        this.data = data;
    }


    public static ChunkCompileRegion read(World world, RenderChunkPos pos) {
        long cx = pos.getX();
        long cy = pos.getY();
        long cz = pos.getZ();

        ChunkPos location = ChunkPos.fromWorldPos(cx, cz);

        IBlockAccess[][][] data = new IBlockAccess[18][18][18];

        boolean empty = true;

        WorldChunk[][] cache = new WorldChunk[3][3];

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                cache[i][j] = (WorldChunk) world.loadChunk(ChunkPos.create(cx + i - 1, cz + j - 1), ChunkLoadTicket.LOAD_DATA).get();
            }
        }

        for (int x = -1; x < 17; x++) {
            for (int z = -1; z < 17; z++) {
                int localChunkX = x >> 4;
                int localChunkZ = z >> 4;

                WorldChunk chunk = cache[localChunkX + 1][localChunkZ + 1];

                for (int y = -1; y < 17; ++y) {
                    IBlockAccess block = chunk.getBlockAccessRelative(x & 15, y + cy * 16, z & 15);
                    data[x + 1][y + 1][z + 1] = block;

                    if (block instanceof NonLoadedBlockAccess) {
                        block.getBlockID();
                    }

                    if (!Objects.equals(block.getBlockID(), "cubecraft:air")) {
                        empty = false;
                    }
                }
            }
        }

        if (empty) {
            return null;
        }
        return new ChunkCompileRegion(world, pos, data);
    }


    @Override
    public IBlockAccess getBlockAccess(long x, long y, long z) {


        int rx = (int) (x + 1 - this.cx * 16);
        int ry = (int) (y + 1 - this.cy * 16);
        int rz = (int) (z + 1 - this.cz * 16);

        if (rx < 0 || rx > 17 || ry < 0 || ry > 17 || rz < 0 || rz > 17) {
            //return this.world.getBlockAccess(rx, ry, rz);
        }

        return this.data[rx][ry][rz];
    }

    public World getWorld() {
        return world;
    }
}
