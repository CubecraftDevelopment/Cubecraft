package net.cubecraft.client.world;

import me.gb2022.commons.container.keymap.KeyMap;
import net.cubecraft.client.ClientSharedContext;
import net.cubecraft.internal.network.packet.PacketChunkGet;
import net.cubecraft.level.Level;
import net.cubecraft.world.World;
import net.cubecraft.world.chunk.ChunkProvider;
import net.cubecraft.world.chunk.PrimerChunk;
import net.cubecraft.world.chunk.WorldChunk;
import net.cubecraft.world.chunk.pos.ChunkPos;

import java.util.Arrays;
import java.util.HashMap;

//todo：绑定一个provider对应一个world(架构)
//todo：绑到网络总线
public class ClientChunkProvider extends ChunkProvider {
    private final World world;

    private final KeyMap<ChunkPos, PrimerChunk> chunkCache = new KeyMap<>();
    private final HashMap<String, int[]> lightMarkCache = new HashMap<>();
    private final HashMap<String, int[]> blockMarkCache = new HashMap<>();

    public ClientChunkProvider(Level level, World world) {
        super(level);
        this.world = world;
    }

    @Override
    public void generateChunk(World world, ChunkPos pos) {
        ClientSharedContext.getClient().getClientIO().sendPacket(new PacketChunkGet(pos, world.getId()));
    }


    private void tryReleaseChunk(ChunkPos pos) {
        PrimerChunk chunk = this.chunkCache.get(pos);
        if (chunk == null) {
            return;
        }

        int[] list = this.blockMarkCache.get(pos.toString());
        if (list == null) {
            return;
        }
        if (!Arrays.stream(list).allMatch((i) -> i == 1)) {
            return;
        }

        int[] list2 = this.lightMarkCache.get(pos.toString());
        if (list2 == null) {
            return;
        }
        if (!Arrays.stream(list2).allMatch((i) -> i == 1)) {
            return;
        }
        this.world.getChunkCache().add(new WorldChunk(this.world, chunk));
    }
}
