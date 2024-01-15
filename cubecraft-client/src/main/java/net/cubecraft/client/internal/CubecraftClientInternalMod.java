package net.cubecraft.client.internal;

import ink.flybird.fcommon.event.EventHandler;
import ink.flybird.fcommon.event.SubscribedEvent;
import ink.flybird.jflogger.ILogger;
import ink.flybird.jflogger.LogManager;
import net.cubecraft.ContentRegistries;
import net.cubecraft.SharedContext;
import net.cubecraft.Side;
import net.cubecraft.client.ClientSettingRegistry;
import net.cubecraft.client.ClientSharedContext;
import net.cubecraft.client.context.ClientGUIContext;
import net.cubecraft.client.context.ClientRenderContext;
import net.cubecraft.client.event.app.ClientDisposeEvent;
import net.cubecraft.client.event.app.ClientPostSetupEvent;
import net.cubecraft.client.event.app.ClientSetupEvent;
import net.cubecraft.client.event.gui.context.GUIContextPreInitEvent;
import net.cubecraft.client.gui.layout.FlowLayout;
import net.cubecraft.client.gui.layout.OriginLayout;
import net.cubecraft.client.gui.layout.ViewportLayout;
import net.cubecraft.client.gui.node.*;
import net.cubecraft.client.internal.entity.BlockBrakeParticle;
import net.cubecraft.client.internal.handler.ClientAssetLoader;
import net.cubecraft.client.internal.handler.ClientListener;
import net.cubecraft.client.internal.handler.ParticleHandler;
import net.cubecraft.client.internal.handler.ScreenController;
import net.cubecraft.client.registry.ClientNetworkHandlerRegistry;
import net.cubecraft.client.registry.ColorMapRegistry;
import net.cubecraft.client.registry.GUIRegistry;
import net.cubecraft.client.registry.RenderRegistry;
import net.cubecraft.client.render.block.BlockRenderer;
import net.cubecraft.client.render.block.LiquidRenderer;
import net.cubecraft.client.render.block.ModelBlockRenderer;
import net.cubecraft.client.render.world.WorldRendererRegistry;
import net.cubecraft.client.resource.ModelAsset;
import net.cubecraft.client.resource.TextureAsset;
import net.cubecraft.event.mod.ModConstructEvent;
import net.cubecraft.mod.CubecraftMod;
import net.cubecraft.util.NSStringDispatcher;

@CubecraftMod(side = Side.CLIENT)
public final class CubecraftClientInternalMod {
    public static final ILogger LOGGER = LogManager.getLogger("ClientInternalMod");
    public static final String MOD_ID = "cubecraft_client";

    @EventHandler
    @SubscribedEvent(MOD_ID)
    public static void onModConstruct(ModConstructEvent event) {
        ContentRegistries.EVENT_BUS.registerEventListener(ClientListener.class);
        ClientSharedContext.getClient().getClientEventBus().registerEventListener(CubecraftClientInternalMod.class);
        ClientSharedContext.CLIENT_SETTING.register(ClientSettingRegistry.class);

        ClientListener listener = new ClientListener();
        ClientSharedContext.getClient().getClientEventBus().registerEventListener(listener);
        ClientSharedContext.getClient().getDeviceEventBus().registerEventListener(listener);
        event.getModManager().getModLoaderEventBus().registerEventListener(listener);

        LOGGER.info("mod constructed.");
    }

    @EventHandler
    public static void onClientSetup(ClientSetupEvent event){
        ClientSharedContext.NET_HANDLER.registerGetFunctionProvider(ClientNetworkHandlerRegistry.class);
        ClientRenderContext.COLOR_MAP.registerGetter(ColorMapRegistry.class);
        ClientSharedContext.getClient().getClientEventBus().registerEventListener(new ParticleHandler());
        ClientSharedContext.RESOURCE_MANAGER.registerEventListener(new ClientAssetLoader());
    }


    @EventHandler
    public static void onClientPostSetup(ClientPostSetupEvent event) {
        ClientRenderContext.WORLD_RENDERER.registerFieldHolder(WorldRendererRegistry.class);
        ClientRenderContext.CHUNK_LAYER_RENDERER.registerGetFunctionProvider(RenderRegistry.class);

        for (String id : ContentRegistries.BLOCK.idList()) {
            String namespace = NSStringDispatcher.getNameSpace(id);
            String localId = NSStringDispatcher.getId(id);
            if (ClientRenderContext.BLOCK_RENDERER.get(id) != null) {
                continue;
            }
            ClientRenderContext.BLOCK_RENDERER.registerItem(id, new ModelBlockRenderer(new ModelAsset(namespace + ":/block/" + localId + ".json")));
        }
        ClientRenderContext.BLOCK_RENDERER.registerItem("cubecraft:calm_water", new LiquidRenderer(new TextureAsset("cubecraft:/block/water_still.png"), new TextureAsset("cubecraft:/block/water_flow.png")));
        ClientRenderContext.BLOCK_RENDERER.registerItem("cubecraft:stone", new BlockRenderer(new TextureAsset("cubecraft", "/block/stone.png"), "cubecraft:alpha_block"));


        ContentRegistries.ENTITY.registerItem(BlockBrakeParticle.class);
    }

    @EventHandler
    public static void onClientDispose(ClientDisposeEvent event) {
        LOGGER.info("detected client dispose event :D");
    }

    @EventHandler
    public static void onGUIContextPreInit(GUIContextPreInitEvent event) {
        ClientSharedContext.getClient().getClientGUIContext().getEventBus().registerEventListener(new ScreenController());

        GUIRegistry.registerComponentRenderers();
        GUIRegistry.registerComponents();
        ClientGUIContext.LAYOUT.registerGetFunctionProvider(GUIRegistry.class);
    }
}
