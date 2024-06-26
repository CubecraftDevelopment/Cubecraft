package net.cubecraft.internal.world.biome;

import net.cubecraft.world.biome.Biome;
import net.cubecraft.world.block.blocks.BlockRegistry;
import net.cubecraft.internal.block.BlockType;
import net.cubecraft.world.chunk.WorldChunk;
import me.gb2022.commons.registry.FieldRegistry;
import me.gb2022.commons.registry.FieldRegistryHolder;

@FieldRegistryHolder(value = "cubecraft")
public class BiomesRegistry {
    @FieldRegistry(value = "plains")
    public static final Biome PLAINS=new Biome(0, 0, 0, 0, 0, BiomeType.PLAINS, BlockType.STONE, 0x114514, 0x114514) {
        @Override
        public void buildSurface(WorldChunk primer, int x, int z, double height, long seed) {
            try {
                primer.setBlockState(BlockRegistry.GRASS_BLOCK.defaultState(x, (long) height, z));
                primer.setBlockState(BlockRegistry.DIRT.defaultState(x, (long) height - 1, z));
                primer.setBlockState(BlockRegistry.DIRT.defaultState(x, (long) height - 2, z));
            }catch (Exception e){

            }
        }
    };
}
