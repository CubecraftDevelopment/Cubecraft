package ink.flybird.cubecraft.world.worldGen;
import ink.flybird.cubecraft.world.chunk.WorldChunk;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

@Deprecated
public class ChunkGeneratorPipeline {
    private final ArrayList<IChunkGenerator> handlers =new ArrayList<>();

    public final String id;

    public ChunkGeneratorPipeline(String id) {
        this.id=id;
    }

    public ChunkGeneratorPipeline add(IChunkGenerator generator){
        this.handlers.add(generator);
        return this;
    }

    public void generate(WorldChunk chunk, WorldGeneratorSetting worldGeneratorSetting){
        for (GenerateStage stage:GenerateStage.byOrder()){
            this.generate(stage,chunk,worldGeneratorSetting);
        }
    }

    public void generate(GenerateStage stage, WorldChunk chunk, WorldGeneratorSetting setting){
        for (IChunkGenerator handler:this.handlers){
            Method[] ms = handler.getClass().getMethods();
            for (Method m : ms) {
                if (Arrays.stream(m.getAnnotations()).anyMatch(annotation -> annotation instanceof WorldGenListener)) {
                    WorldGenListener a=m.getAnnotation(WorldGenListener.class);
                    if (a.stage() == stage&& Objects.equals(a.world(), chunk.getWorld().getID())) {
                        try {
                            m.invoke(handler,chunk,setting);
                        } catch (IllegalAccessException | InvocationTargetException e2) {
                        }
                    }
                }
            }
        }
    }
}
