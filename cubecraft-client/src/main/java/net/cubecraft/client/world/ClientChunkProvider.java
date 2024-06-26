package net.cubecraft.client.world;

import net.cubecraft.client.ClientSharedContext;
import net.cubecraft.world.chunk.pos.ChunkPos;
import me.gb2022.commons.container.KeyMap;
import net.cubecraft.internal.network.packet.PacketChunkGet;
import net.cubecraft.level.Level;
import net.cubecraft.world.IWorld;
import net.cubecraft.world.chunk.storage.SectionBlockAccess;
import net.cubecraft.world.chunk.storage.SectionLightAccess;
import net.cubecraft.world.chunk.Chunk;
import net.cubecraft.world.chunk.ChunkProvider;
import net.cubecraft.world.chunk.ProviderChunk;
import net.cubecraft.world.chunk.WorldChunk;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

//todo：绑定一个provider对应一个world(架构)
//todo：绑到网络总线
public class ClientChunkProvider extends ChunkProvider {
    private final IWorld world;

    private final KeyMap<ChunkPos, ProviderChunk> chunkCache = new KeyMap<>();
    private final HashMap<String, int[]> lightMarkCache = new HashMap<>();
    private final HashMap<String, int[]> blockMarkCache = new HashMap<>();

    public ClientChunkProvider(Level level, IWorld world) {
        super(level);
        this.world = world;
    }

    @Override
    public void generateChunk(IWorld world, ChunkPos pos) {
        ClientSharedContext.getClient().getClientIO().sendPacket(new PacketChunkGet(pos, world.getId()));
    }


    private void addSection(String world, SectionBlockAccess section, long x, long z, int sectionIndex) {
        if (!Objects.equals(world, this.world.getId())) {
            return;
        }

        ChunkPos pos = ChunkPos.create(x, z);
        ProviderChunk chunk = this.chunkCache.get(pos);
        if (chunk == null) {
            chunk = this.chunkCache.add(new ProviderChunk(pos));
        }

        chunk.setSection(sectionIndex, section);
        int[] list = this.blockMarkCache.computeIfAbsent(pos.toString(), k -> new int[Chunk.SECTION_SIZE]);
        list[sectionIndex] = 1;
        this.tryReleaseChunk(pos);
    }

    private void addSection(String world, SectionLightAccess section, long x, long z, int sectionIndex) {
        if (!Objects.equals(world, this.world.getId())) {
            return;
        }

        ChunkPos pos = ChunkPos.create(x, z);
        ProviderChunk chunk = this.chunkCache.get(pos);
        if (chunk == null) {
            chunk = this.chunkCache.add(new ProviderChunk(pos));
        }

        chunk.setSection(sectionIndex, section);
        int[] list = this.lightMarkCache.computeIfAbsent(pos.toString(), k -> new int[Chunk.SECTION_SIZE]);
        this.lightMarkCache.computeIfAbsent(pos.toString(), k -> new int[Chunk.SECTION_SIZE]);
        list[sectionIndex] = 1;
        this.tryReleaseChunk(pos);
    }

    private void tryReleaseChunk(ChunkPos pos) {
        ProviderChunk chunk = this.chunkCache.get(pos);
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
