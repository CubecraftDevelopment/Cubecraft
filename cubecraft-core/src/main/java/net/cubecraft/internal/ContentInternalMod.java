package net.cubecraft.internal;

import ink.flybird.fcommon.event.EventHandler;
import net.cubecraft.ContentRegistries;
import net.cubecraft.SharedContext;
import net.cubecraft.event.mod.ContentInitializeEvent;
import net.cubecraft.extension.CubecraftMod;
import net.cubecraft.extension.ModSide;
import net.cubecraft.internal.auth.OfflineSessionService;
import net.cubecraft.internal.block.BlockBehaviorRegistry;
import net.cubecraft.internal.entity.EntityRegistry;
import net.cubecraft.internal.inventory.InventoryRegistry;
import net.cubecraft.internal.item.ItemRegistry;
import net.cubecraft.internal.network.PacketRegistry;
import net.cubecraft.internal.world.biome.BiomesRegistry;
import net.cubecraft.world.block.blocks.BlockRegistry;
import net.cubecraft.world.dimension.DimensionRegistry;
import net.cubecraft.world.item.behavior.ItemBehaviorRegistry;
import net.cubecraft.world.worldGen.generator.WorldGeneratorPipelineRegistry;

@CubecraftMod(side = ModSide.BOTH)
public class ContentInternalMod {
    @EventHandler
    public void onContentInitialize(ContentInitializeEvent e) {

        ContentRegistries.BLOCK_BEHAVIOR.registerFieldHolder(BlockBehaviorRegistry.class);
        ContentRegistries.BLOCK.registerFieldHolder(BlockRegistry.class);


        ContentRegistries.ENTITY.registerGetFunctionProvider(EntityRegistry.class);
        ContentRegistries.INVENTORY.registerGetFunctionProvider(InventoryRegistry.class);
        ContentRegistries.BIOME.registerFieldHolder(BiomesRegistry.class);
        SharedContext.PACKET.registerGetFunctionProvider(PacketRegistry.class);
        SharedContext.SESSION_SERVICE.registerItem("cubecraft:default", new OfflineSessionService());


        ContentRegistries.DIMENSION.registerFieldHolder(DimensionRegistry.class);
        ContentRegistries.CHUNK_GENERATE_PIPELINE.registerFieldHolder(WorldGeneratorPipelineRegistry.class);

        ContentRegistries.ITEM_BEHAVIOR.registerFieldHolder(ItemBehaviorRegistry.class);

        ContentRegistries.ITEM.registerFieldHolder(ItemRegistry.class);
    }
}
