package ink.flybird.cubecraft.internal;

import ink.flybird.cubecraft.extansion.ExtensionSide;
import ink.flybird.cubecraft.internal.auth.DefaultSessionService;
import ink.flybird.cubecraft.internal.block.BlockBehaviorRegistry;
import ink.flybird.cubecraft.internal.block.BlockRegistry;
import ink.flybird.cubecraft.internal.entity.EntityRegistry;
import ink.flybird.cubecraft.internal.inventory.InventoryRegistry;
import ink.flybird.cubecraft.internal.network.PacketRegistry;
import ink.flybird.cubecraft.internal.world.DimensionOverworld;
import ink.flybird.cubecraft.internal.world.WorldType;
import ink.flybird.cubecraft.internal.world.biome.BiomesRegistry;
import ink.flybird.cubecraft.internal.world.worldGen.WorldGeneratorFlat;
import ink.flybird.cubecraft.internal.world.worldGen.WorldGeneratorOverworld;
import ink.flybird.cubecraft.register.ContentRegistries;
import ink.flybird.fcommon.event.EventHandler;
import ink.flybird.cubecraft.mod.ContentInitializeEvent;
import ink.flybird.cubecraft.extansion.CubecraftExtension;

import ink.flybird.cubecraft.register.SharedContext;
import ink.flybird.cubecraft.world.worldGen.ChunkGeneratorPipeline;

@CubecraftExtension(side = ExtensionSide.BOTH)
public class ContentInternalMod{
    @EventHandler
    public void onContentInitialize(ContentInitializeEvent e){
        ContentRegistries.BLOCK_BEHAVIOR.registerFieldHolder(BlockBehaviorRegistry.class);
        ContentRegistries.BLOCK.registerFieldHolder(BlockRegistry.class);
        ContentRegistries.ENTITY.registerGetFunctionProvider(EntityRegistry.class);
        ContentRegistries.INVENTORY.registerGetFunctionProvider(InventoryRegistry.class);
        ContentRegistries.WORLD_GENERATOR.registerItem(WorldType.OVERWORLD, new ChunkGeneratorPipeline(WorldType.OVERWORLD).add(new WorldGeneratorOverworld()));
        ContentRegistries.BIOME.registerFieldHolder(BiomesRegistry.class);
        SharedContext.PACKET.registerGetFunctionProvider(PacketRegistry.class);
        SharedContext.SESSION_SERVICE.registerItem("cubecraft:default", new DefaultSessionService());
        ContentRegistries.DIMENSION.registerItem("cubecraft:overworld",new DimensionOverworld());
    }
}
