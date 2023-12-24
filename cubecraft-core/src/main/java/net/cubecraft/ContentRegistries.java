package net.cubecraft;

import ink.flybird.fcommon.event.EventBus;
import ink.flybird.fcommon.event.SimpleEventBus;
import ink.flybird.fcommon.registry.ConstructingMap;
import ink.flybird.fcommon.registry.FieldRegistry;
import ink.flybird.fcommon.registry.FieldRegistryHolder;
import ink.flybird.fcommon.registry.RegisterMap;
import net.cubecraft.internal.item.ItemRegistry;
import net.cubecraft.world.block.blocks.BlockRegistry;
import net.cubecraft.world.IWorld;
import net.cubecraft.world.biome.BiomeMap;
import net.cubecraft.world.block.Block;
import net.cubecraft.world.block.behavior.BlockBehavior;
import net.cubecraft.world.block.property.BlockProperty;
import net.cubecraft.world.dimension.Dimension;
import net.cubecraft.world.entity.Entity;
import net.cubecraft.world.item.container.Inventory;
import net.cubecraft.world.item.Item;
import net.cubecraft.world.item.behavior.ItemBehavior;
import net.cubecraft.world.worldGen.pipeline.WorldGenPipelineBuilder;

@FieldRegistryHolder("cubecraft")
public interface ContentRegistries {
    EventBus EVENT_BUS = new SimpleEventBus();

    //item
    @FieldRegistry("inventory")
    ConstructingMap<Inventory> INVENTORY = new ConstructingMap<>(Inventory.class);

    @FieldRegistry("item_behavior")
    RegisterMap<ItemBehavior> ITEM_BEHAVIOR = new RegisterMap<>();

    @FieldRegistry("item")
    RegisterMap<Item> ITEM = new RegisterMap<>(ItemRegistry.DUMMY);

    //block
    @FieldRegistry("block")
    RegisterMap<Block> BLOCK = new RegisterMap<>(BlockRegistry.UNKNOWN_BLOCK);

    @FieldRegistry("block_property")
    RegisterMap<BlockProperty<?>> BLOCK_PROPERTY = new RegisterMap<>();

    @FieldRegistry("block_behavior")
    RegisterMap<BlockBehavior> BLOCK_BEHAVIOR = new RegisterMap<>();


    //world
    @FieldRegistry("dimension")
    RegisterMap<Dimension> DIMENSION = new RegisterMap<>();

    @FieldRegistry("biome")
    BiomeMap BIOME = new BiomeMap();

    @FieldRegistry("world_generator_pipeline")
    RegisterMap<WorldGenPipelineBuilder> CHUNK_GENERATE_PIPELINE = new RegisterMap<>();

    //entity
    @FieldRegistry("entity")
    ConstructingMap<Entity> ENTITY = new ConstructingMap<>(Entity.class, IWorld.class);
}
