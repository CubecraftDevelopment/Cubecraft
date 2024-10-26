package net.cubecraft;

import net.cubecraft.util.register.NamedRegistry;
import net.cubecraft.world.biome.Biome;
import net.cubecraft.world.block.Block;
import net.cubecraft.world.item.Item;
import net.cubecraft.world.worldGen.pipeline.ChunkGeneratorPipeline;

public interface CoreRegistries {
    NamedRegistry<Block> BLOCKS = new NamedRegistry<>();

    NamedRegistry<Biome> BIOMES = new NamedRegistry<>();

    NamedRegistry<Item> ITEMS = new NamedRegistry<>();
}
