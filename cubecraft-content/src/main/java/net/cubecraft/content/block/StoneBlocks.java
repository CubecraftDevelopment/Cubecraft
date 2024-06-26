package net.cubecraft.content.block;

import me.gb2022.commons.registry.FieldRegistry;
import net.cubecraft.world.block.Block;
import net.cubecraft.world.block.blocks.StoneBlock;

public interface StoneBlocks {
    String ANDESITE_BLOCK_ID = "cubecraft:andesite";
    String DIORITE_BLOCK_ID = "cubecraft:diorite";
    String GRANITE_BLOCK_ID = "cubecraft:granite";
    String BASALT_BLOCK_ID = "cubecraft:basalt";

    @FieldRegistry(value = "andesite")
    Block ANDESITE = new StoneBlock(ANDESITE_BLOCK_ID);

    @FieldRegistry(value = "diorite")
    Block DIORITE = new StoneBlock(DIORITE_BLOCK_ID);

    @FieldRegistry(value = "granite")
    Block GRANITE = new StoneBlock(GRANITE_BLOCK_ID);

    @FieldRegistry(value = "granite")
    Block BASALT = new StoneBlock(BASALT_BLOCK_ID);
}
