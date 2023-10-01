package ink.flybird.cubecraft.world.worldGen.pipeline;

import ink.flybird.cubecraft.world.chunk.ProviderChunk;
import ink.flybird.cubecraft.world.worldGen.cache.HeightMap;
import ink.flybird.cubecraft.world.worldGen.context.NoiseProviderContext;
import ink.flybird.cubecraft.world.worldGen.context.NoiseSamplerContext;
import ink.flybird.cubecraft.world.worldGen.noiseGenerator.Noise;
import ink.flybird.fcommon.container.ArrayQueue;
import ink.flybird.fcommon.container.OrderedHashMap;

import java.util.HashMap;

public final class ChunkGeneratorPipeline {
    private final OrderedHashMap<String, NoiseProvider> noiseProviderMap = new OrderedHashMap<>();
    private final OrderedHashMap<String, NoiseSampler> noiseSamplerMap = new OrderedHashMap<>();

    public void registerNoiseProvider(String id, NoiseProvider processor) {
        this.noiseProviderMap.put(id, processor);
    }

    public void registerNoiseSampler(String id, NoiseSampler processor) {
        this.noiseSamplerMap.put(id, processor);
    }

    public OrderedHashMap<String, NoiseProvider> getNoiseProviderMap() {
        return this.noiseProviderMap;
    }

    public OrderedHashMap<String, NoiseSampler> getNoiseSamplerMap() {
        return this.noiseSamplerMap;
    }

    private Task createGenerateTask(ProviderChunk chunk, long seed) {
        return new Task(this, chunk, seed);
    }

    private Runnable createTask(ProviderChunk chunk, long seed, ArrayQueue<ProviderChunk> dest) {
        return () -> {
            Task t = createGenerateTask(chunk, seed);
            t.run();
            dest.add(t.chunk);
        };
    }


    static final class Task {
        private final ChunkGeneratorPipeline pipeline;
        private final HashMap<String, Noise> noiseCache = new HashMap<>();
        private final HashMap<String, HeightMap> heightMapCache = new HashMap<>();
        private final ProviderChunk chunk;
        private final long seed;

        public Task(ChunkGeneratorPipeline pipeline, ProviderChunk chunk, long seed) {
            this.pipeline = pipeline;
            this.chunk = chunk;
            this.seed = seed;
        }

        public void run() {
            NoiseProviderContext noiseProviderContext = new NoiseProviderContext(
                    this.chunk,
                    this.seed,
                    this.noiseCache
            );
            for (NoiseProvider provider : this.pipeline.getNoiseProviderMap().values()) {
                provider.generateNoise(noiseProviderContext);
            }
            NoiseSamplerContext noiseSamplerContext = new NoiseSamplerContext(
                    this.chunk,
                    this.seed,
                    this.noiseCache,
                    this.heightMapCache
            );
            for (NoiseSampler provider : this.pipeline.getNoiseSamplerMap().values()) {
                provider.sample(noiseSamplerContext);
            }

            //todo:补充阶段：biome，structure，decoration，entity
        }
    }
}
