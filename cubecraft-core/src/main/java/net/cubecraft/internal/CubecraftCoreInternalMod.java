package net.cubecraft.internal;

import ink.flybird.fcommon.event.EventHandler;
import ink.flybird.fcommon.event.SubscribedEvent;
import ink.flybird.jflogger.ILogger;
import ink.flybird.jflogger.LogManager;
import net.cubecraft.ContentRegistries;
import net.cubecraft.SharedContext;
import net.cubecraft.Side;
import net.cubecraft.event.mod.ModConstructEvent;
import net.cubecraft.internal.auth.OfflineSessionService;
import net.cubecraft.internal.block.BlockBehaviorRegistry;
import net.cubecraft.internal.entity.EntityRegistry;
import net.cubecraft.internal.inventory.InventoryRegistry;
import net.cubecraft.internal.item.ItemRegistry;
import net.cubecraft.internal.network.PacketRegistry;
import net.cubecraft.internal.world.biome.BiomesRegistry;
import net.cubecraft.mod.CubecraftMod;
import net.cubecraft.world.block.blocks.BlockRegistry;
import net.cubecraft.world.dimension.DimensionRegistry;
import net.cubecraft.world.item.behavior.ItemBehaviorRegistry;
import net.cubecraft.world.worldGen.generator.WorldGeneratorPipelineRegistry;

@CubecraftMod(side = Side.SHARED)
public class CubecraftCoreInternalMod {
    public static final String MOD_ID = "cubecraft_core";
    public static final ILogger LOGGER = LogManager.getLogger("CoreInternalMod");

    @EventHandler
    @SubscribedEvent(MOD_ID)
    public static void onModConstruct(ModConstructEvent event){
        ContentRegistries.EVENT_BUS.registerEventListener(ItemRegistry.class);

        ContentRegistries.ENTITY.registerGetFunctionProvider(EntityRegistry.class);
        ContentRegistries.INVENTORY.registerGetFunctionProvider(InventoryRegistry.class);
        ContentRegistries.BIOME.registerFieldHolder(BiomesRegistry.class);
        SharedContext.PACKET.registerGetFunctionProvider(PacketRegistry.class);
        SharedContext.SESSION_SERVICE.registerItem("cubecraft:default", new OfflineSessionService());


        ContentRegistries.DIMENSION.registerFieldHolder(DimensionRegistry.class);
        ContentRegistries.CHUNK_GENERATE_PIPELINE.registerFieldHolder(WorldGeneratorPipelineRegistry.class);
        ContentRegistries.ITEM_BEHAVIOR.registerFieldHolder(ItemBehaviorRegistry.class);
        ContentRegistries.ITEM.registerFieldHolder(ItemRegistry.class);

        ContentRegistries.BLOCK_BEHAVIOR.registerFieldHolder(BlockBehaviorRegistry.class);
        ContentRegistries.BLOCK.registerFieldHolder(BlockRegistry.class);
    }
}
