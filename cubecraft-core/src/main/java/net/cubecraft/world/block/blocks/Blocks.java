package net.cubecraft.world.block.blocks;

import net.cubecraft.util.register.NamedRegistry;
import net.cubecraft.util.register.Registered;
import net.cubecraft.util.register.Registry;
import net.cubecraft.world.block.Block;

@Registry
public interface Blocks {
    NamedRegistry<Block> REGISTRY = new NamedRegistry<>(new Registered<>(new BlockAir(), "cubecraft:air", 0));

    Registered<Block> AIR = REGISTRY.register(new BlockAir());
    Registered<Block> STONE = REGISTRY.register(new StoneBlock("cubecraft:stone"));
    Registered<Block> WATER = REGISTRY.register(new LiquidBlock("cubecraft:water"));

    Registered<Block> DIRT = REGISTRY.register(new DirtBlock("cubecraft:dirt"));

    Registered<Block> GRASS_BLOCK = REGISTRY.register(new GrassBlock("cubecraft:grass_block"));
    Registered<Block> PODZOL = REGISTRY.register(new GrassBlock("cubecraft:podzol"));
    Registered<Block> MYCELIUM = REGISTRY.register(new GrassBlock("cubecraft:mycelium"));

    Registered<Block> OAK_LOG = REGISTRY.register(new LogBlock("cubecraft:oak_log"));
    Registered<Block> OAK_LEAVES = REGISTRY.register(new LeavesBlock("cubecraft:oak_leaves"));

    Registered<Block> SNOW = REGISTRY.register(new DirtBlock("cubecraft:snow"));

    Registered<Block> SAND = REGISTRY.register(new DirtBlock("cubecraft:sand"));
    Registered<Block> GRAVEL = REGISTRY.register(new DirtBlock("cubecraft:gravel"));
    Registered<Block> ICE = REGISTRY.register(new IceBlock("cubecraft:ice"));
    Registered<Block> BEDROCK = REGISTRY.register(new StoneBlock("cubecraft:bedrock"));

    Registered<Block> GLASS = REGISTRY.register(new GlassBlock("cubecraft:glass"));

    Registered<Block> OAK_PLANKS = REGISTRY.register(new PlankBlock("cubecraft:oak_planks"));
    Registered<Block> ACACIA_PLANKS = REGISTRY.register(new PlankBlock("cubecraft:acacia_planks"));
    Registered<Block> BIRCH_PLANKS = REGISTRY.register(new PlankBlock("cubecraft:birch_planks"));
    Registered<Block> SPRUCE_PLANKS = REGISTRY.register(new PlankBlock("cubecraft:spruce_planks"));
    Registered<Block> DARK_OAK_PLANKS = REGISTRY.register(new PlankBlock("cubecraft:dark_oak_planks"));
}
