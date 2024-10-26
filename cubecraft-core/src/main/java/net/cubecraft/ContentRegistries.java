package net.cubecraft;

import me.gb2022.commons.event.SimpleEventBus;
import me.gb2022.commons.registry.ConstructingMap;
import me.gb2022.commons.registry.FieldRegistry;
import me.gb2022.commons.registry.FieldRegistryHolder;
import me.gb2022.commons.registry.RegisterMap;
import net.cubecraft.internal.item.ItemRegistry;
import net.cubecraft.world.World;
import net.cubecraft.world.block.Block;
import net.cubecraft.world.block.behavior.BlockBehavior;
import net.cubecraft.world.block.blocks.BlockRegistry;
import net.cubecraft.world.block.property.BlockProperty;
import net.cubecraft.world.dimension.Dimension;
import net.cubecraft.world.entity.Entity;
import net.cubecraft.world.item.Item;
import net.cubecraft.world.item.behavior.ItemBehavior;
import net.cubecraft.world.item.container.Inventory;
import net.cubecraft.world.worldGen.pipeline.WorldGenPipelineBuilder;

@FieldRegistryHolder("cubecraft")
public interface ContentRegistries {
    SimpleEventBus EVENT_BUS = new SimpleEventBus();

    //item
    @FieldRegistry("inventory")
    ConstructingMap<Inventory> INVENTORY = new ConstructingMap<>(Inventory.class);

    @FieldRegistry("item_behavior")
    RegisterMap<ItemBehavior> ITEM_BEHAVIOR = new RegisterMap<>(ItemBehavior.class);

    @FieldRegistry("item")
    RegisterMap<Item> ITEM = new RegisterMap<>(ItemRegistry.DUMMY, Item.class);

    //block
    @FieldRegistry("block")
    RegisterMap<Block> BLOCK = new RegisterMap<>(BlockRegistry.UNKNOWN_BLOCK, Block.class);

    @SuppressWarnings("rawtypes")
    @FieldRegistry("block_property")
    RegisterMap<BlockProperty> BLOCK_PROPERTY = new RegisterMap<>(BlockProperty.class);

    @FieldRegistry("block_behavior")
    RegisterMap<BlockBehavior> BLOCK_BEHAVIOR = new RegisterMap<>(BlockBehavior.class);


    //world
    @FieldRegistry("dimension")
    RegisterMap<Dimension> DIMENSION = new RegisterMap<>(Dimension.class);

    @FieldRegistry("world_generator_pipeline")
    RegisterMap<WorldGenPipelineBuilder> CHUNK_GENERATE_PIPELINE = new RegisterMap<>(WorldGenPipelineBuilder.class);

    //entity
    @FieldRegistry("entity")
    ConstructingMap<Entity> ENTITY = new ConstructingMap<>(Entity.class, World.class);
}
