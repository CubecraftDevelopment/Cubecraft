package net.cubecraft.world.block.blocks;

import net.cubecraft.CoreRegistries;
import net.cubecraft.util.register.Registered;
import net.cubecraft.util.register.Registry;
import net.cubecraft.world.block.Block;

@Registry
public interface Blocks {
    Registered<Block> AIR = CoreRegistries.BLOCKS.register(new BlockAir());
    Registered<Block> STONE = CoreRegistries.BLOCKS.register(new StoneBlock("cubecraft:stone"));
    Registered<Block> GRASS_BLOCK = CoreRegistries.BLOCKS.register(new GrassBlock("cubecraft:grass_block"));
    Registered<Block> DIRT = CoreRegistries.BLOCKS.register(new DirtBlock("cubecraft:dirt"));
    Registered<Block> OAK_LOG = CoreRegistries.BLOCKS.register(new StoneBlock("cubecraft:oak_log"));
    Registered<Block> OAK_LEAVES = CoreRegistries.BLOCKS.register(new LeavesBlock("cubecraft:oak_leaves"));
    Registered<Block> CALM_WATER = CoreRegistries.BLOCKS.register(new LiquidBlock("cubecraft:calm_water"));
    Registered<Block> SNOW = CoreRegistries.BLOCKS.register(new DirtBlock("cubecraft:snow"));
    Registered<Block> SAND = CoreRegistries.BLOCKS.register(new DirtBlock("cubecraft:sand"));
    Registered<Block> GRAVEL = CoreRegistries.BLOCKS.register(new DirtBlock("cubecraft:gravel"));
    Registered<Block> ICE = CoreRegistries.BLOCKS.register(new IceBlock("cubecraft:ice"));
}
