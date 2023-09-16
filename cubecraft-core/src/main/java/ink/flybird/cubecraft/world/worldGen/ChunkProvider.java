package ink.flybird.cubecraft.world.worldGen;

import ink.flybird.cubecraft.register.ContentRegistries;
import ink.flybird.cubecraft.world.IWorld;
import ink.flybird.cubecraft.world.chunk.WorldChunk;
import ink.flybird.cubecraft.world.chunk.ChunkPos;
import ink.flybird.cubecraft.world.worldGen.noiseGenerator.PerlinNoise;
import ink.flybird.cubecraft.world.worldGen.noiseGenerator.Synth;
import ink.flybird.cubecraft.world.worldGen.templete.Scale;

import java.util.HashMap;
import java.util.Random;

public class ChunkProvider implements IChunkProvider{
    public final ChunkGeneratorPipeline pipeline;

    public static final int REGION_GRID_SIZE = 64;
    public static final HashMap<String, ChunkProvider> providers = new HashMap<>();

    public final IWorld world;

    final Synth test;

    public ChunkProvider(IWorld world) {
        this.world = world;
        this.test= new Scale(new PerlinNoise(new Random(world.getSeed()), 3),1,1);
        this.pipeline= ContentRegistries.WORLD_GENERATOR.get(this.world.getID());
    }

    public static ChunkProvider getProvider(IWorld world) {
        return providers.getOrDefault(world.getID(), new ChunkProvider(world));
    }

    /**
     * this method will try to load chunk on disk first. If result null,it will try to generate a new chunk.
     *
     * @param pos position
     * @return a brand-new chunk
     */
    @Override
    public WorldChunk loadChunk(ChunkPos pos) {
        long regionX = pos.x() / REGION_GRID_SIZE;
        long regionZ = pos.z() / REGION_GRID_SIZE;
        WorldChunk c=new WorldChunk(ChunkProvider.this.world, pos);


        this.pipeline.generate(c,new WorldGeneratorSetting(114514,new HashMap<>()));
        return c;
    }

}
