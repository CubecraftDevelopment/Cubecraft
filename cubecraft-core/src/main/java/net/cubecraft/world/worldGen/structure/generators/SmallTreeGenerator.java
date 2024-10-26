package net.cubecraft.world.worldGen.structure.generators;

import net.cubecraft.util.WorldRandom;
import net.cubecraft.world.block.blocks.Blocks;
import net.cubecraft.world.chunk.Chunk;
import net.cubecraft.world.chunk.ChunkCache;
import net.cubecraft.world.chunk.PrimerChunk;
import net.cubecraft.world.chunk.WorldChunk;
import net.cubecraft.world.worldGen.structure.StructureController;
import net.cubecraft.world.worldGen.structure.StructureGeneratingContainer;
import net.cubecraft.world.worldGen.structure.StructureShape;

import java.util.Objects;

public class SmallTreeGenerator implements StructureController {
    private final StructureShape shape;

    public SmallTreeGenerator(StructureShape shape) {
        this.shape = shape;
    }

    @Override
    public StructureShape getShape() {
        return this.shape;
    }

    @Override
    public void attempt(Chunk chunk, long x, long z, int y0, int y1, ChunkCache<WorldChunk> cache) {
        WorldChunk c = cache.getByWorldPos(x, z);

        if (c == null) {
            return;
        }

        if (WorldRandom.xz(x, z) < 0.99) {
            return;
        }

        int y = c.getHighestBlockAt((int) (x & 15), (int) (z & 15));

        if (y > PrimerChunk.HEIGHT - topBound()) {
            return;
        }

        if(!Objects.equals(c.getBlockId((int) (x & 15), y, (int) (z & 15)), Blocks.GRASS_BLOCK.getId())){
            return;
        }

        StructureGeneratingContainer container = new StructureGeneratingContainer(x, y, z, c.getWorld());

        this.shape.generate(container);
    }
}
