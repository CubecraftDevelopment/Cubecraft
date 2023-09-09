package io.flybird.cubecraft.internal;

import ink.flybird.fcommon.event.EventHandler;
import io.flybird.cubecraft.event.mod.ContentInitializeEvent;
import io.flybird.cubecraft.extansion.CubecraftExtension;
import io.flybird.cubecraft.internal.auth.DefaultSessionService;
import io.flybird.cubecraft.internal.block.BlockBehaviorRegistry;
import io.flybird.cubecraft.internal.block.BlockRegistry;
import io.flybird.cubecraft.internal.entity.EntityRegistry;
import io.flybird.cubecraft.internal.inventory.InventoryRegistry;
import io.flybird.cubecraft.internal.network.PacketRegistry;
import io.flybird.cubecraft.internal.world.DimensionOverworld;
import io.flybird.cubecraft.internal.world.WorldType;
import io.flybird.cubecraft.internal.world.biome.BiomesRegistry;

import io.flybird.cubecraft.internal.world.worldGen.WorldGeneratorOverworld;
import io.flybird.cubecraft.register.ContentRegistries;
import io.flybird.cubecraft.register.SharedContext;
import io.flybird.cubecraft.world.worldGen.ChunkGeneratorPipeline;

@CubecraftExtension
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
