package net.cubecraft.world.block.blocks;

import net.cubecraft.internal.block.BlockBehaviorRegistry;
import net.cubecraft.internal.block.BlockType;
import net.cubecraft.world.block.Block;
import net.cubecraft.world.block.OverwrittenBlock;


public interface BlockRegistry {
    Block AIR = new BlockAir();
    Block STONE = new StoneBlock("cubecraft:stone");
    Block DIRT = new DirtBlock(BlockType.DIRT);
    Block GRASS_BLOCK = new GrassBlock(BlockType.GRASS_BLOCK);
    Block UNKNOWN_BLOCK = new OverwrittenBlock("cubecraft:fallback", BlockBehaviorRegistry.STONE);
}
