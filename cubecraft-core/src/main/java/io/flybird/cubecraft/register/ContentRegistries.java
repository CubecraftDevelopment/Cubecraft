package io.flybird.cubecraft.register;

import ink.flybird.fcommon.registry.ConstructingMap;
import ink.flybird.fcommon.registry.RegisterMap;
import io.flybird.cubecraft.internal.block.BlockRegistry;
import io.flybird.cubecraft.world.IDimension;
import io.flybird.cubecraft.world.IWorld;
import io.flybird.cubecraft.world.biome.BiomeMap;
import io.flybird.cubecraft.world.block.Block;
import io.flybird.cubecraft.world.entity.Entity;
import io.flybird.cubecraft.world.item.Inventory;
import io.flybird.cubecraft.world.item.Item;
import io.flybird.cubecraft.world.worldGen.ChunkGeneratorPipeline;

public class ContentRegistries {
    public static final RegisterMap<Block, ?> BLOCK_BEHAVIOR = new RegisterMap<>();
    public static final RegisterMap<Block, Block> BLOCK = new RegisterMap<>(BLOCK_BEHAVIOR, BlockRegistry.UNKNOWN_BLOCK);

    public static final ConstructingMap<Entity> ENTITY = new ConstructingMap<>(Entity.class, IWorld.class);
    public static final RegisterMap<ChunkGeneratorPipeline, ?> WORLD_GENERATOR = new RegisterMap<>();
    public static final BiomeMap BIOME = new BiomeMap();
    public static final RegisterMap<Item, ?> ITEM = new RegisterMap<>();
    public static final ConstructingMap<Inventory> INVENTORY=new ConstructingMap<>(Inventory.class);
    public static final RegisterMap<IDimension,?> DIMENSION = new RegisterMap<>();
}
