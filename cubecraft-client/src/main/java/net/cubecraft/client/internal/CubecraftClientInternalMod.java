package net.cubecraft.client.internal;

import me.gb2022.commons.event.EventHandler;
import me.gb2022.commons.event.SubscribedEvent;
import net.cubecraft.ContentRegistries;
import net.cubecraft.Side;
import net.cubecraft.client.CubecraftClient;
import net.cubecraft.client.context.ClientGUIContext;
import net.cubecraft.client.event.app.ClientDisposeEvent;
import net.cubecraft.client.event.app.ClientSetupEvent;
import net.cubecraft.client.event.gui.context.GUIContextInitEvent;
import net.cubecraft.client.internal.entity.BlockBrakeParticle;
import net.cubecraft.client.internal.handler.ClientAssetLoader;
import net.cubecraft.client.internal.handler.ClientListener;
import net.cubecraft.client.internal.handler.ParticleHandler;
import net.cubecraft.client.internal.handler.ScreenController;
import net.cubecraft.client.internal.plugins.CameraPlugin;
import net.cubecraft.client.internal.renderer.particle.ParticleRenderers;
import net.cubecraft.client.registry.*;
import net.cubecraft.client.render.LevelRenderer;
import net.cubecraft.client.render.block.IBlockRenderer;
import net.cubecraft.client.render.block.LiquidRenderer;
import net.cubecraft.client.render.block.ModelBlockRenderer;
import net.cubecraft.client.render.world.ParticleRenderer;
import net.cubecraft.client.resource.ModelAsset;
import net.cubecraft.client.resource.TextureAsset;
import net.cubecraft.event.mod.ModConstructEvent;
import net.cubecraft.mod.CubecraftMod;
import net.cubecraft.util.NSStringDispatcher;
import net.cubecraft.world.block.blocks.Blocks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@CubecraftMod(side = Side.CLIENT)
public final class CubecraftClientInternalMod {
    public static final Logger LOGGER = LogManager.getLogger("ClientInternalMod");
    public static final String MOD_ID = "cubecraft_client";

    @EventHandler
    @SubscribedEvent(MOD_ID)
    public static void onModConstruct(ModConstructEvent event) {
        ContentRegistries.EVENT_BUS.registerEventListener(ClientListener.class);
        CubecraftClient.getInstance().getClientEventBus().registerEventListener(CubecraftClientInternalMod.class);

        ClientListener listener = new ClientListener();
        CubecraftClient.getInstance().getClientEventBus().registerEventListener(listener);
        CubecraftClient.getInstance().getDeviceEventBus().registerEventListener(listener);
        event.getModManager().getModLoaderEventBus().registerEventListener(listener);

        LOGGER.info("mod constructed.");

        ContentRegistries.ENTITY.registerItem(BlockBrakeParticle.class);


        ColorMaps.REGISTRY.handle(ColorMaps.class);
    }

    @EventHandler
    public static void onClientSetup(ClientSetupEvent event) {
        event.getClient().addComponent(new CameraPlugin());

        ColorMaps.COLOR_MAP.registerGetter(ColorMapRegistry.class);
        CubecraftClient.getInstance().getClientEventBus().registerEventListener(new ParticleHandler());

        ClientAssetLoader.init();

        LevelRenderer.WORLD_RENDERER.registerGetFunctionProvider(RenderRegistry.class);
        ParticleRenderer.PARTICLE_RENDERERS.registerFieldHolder(ParticleRenderers.class);

        Blocks.REGISTRY.withShadow((reg) -> {
            var id = reg.getName();
            var namespace = NSStringDispatcher.getNameSpace(id);
            var localId = NSStringDispatcher.getId(id);

            if (IBlockRenderer.REGISTRY.isPresent(id)) {
                return;
            }

            var object = Blocks.REGISTRY.object(id);

            if (object.isLiquid()) {
                var still = new TextureAsset("cubecraft:/block/water_still.png");
                var flow = new TextureAsset("cubecraft:/block/water_flow.png");

                IBlockRenderer.REGISTRY.register(id, new LiquidRenderer(still, flow));
                return;
            }

            IBlockRenderer.REGISTRY.register(
                    id,
                    new ModelBlockRenderer(new ModelAsset(namespace + ":/block/" + localId + ".json"))
            );
        });
    }

    @EventHandler
    public static void onClientDispose(ClientDisposeEvent event) {
        LOGGER.info("detected client dispose event :D");
    }

    @EventHandler
    public static void onGUIContextInit(GUIContextInitEvent event) {
        CubecraftClient.getInstance().getClientGUIContext().getEventBus().registerEventListener(new ScreenController());

        ClientGUIContext.COMPONENT_RENDERER_PART.registerGetFunctionProvider(GUIRegistry.class);
        ClientGUIContext.NODE.registerGetFunctionProvider(GUIRegistry.class);
        ClientGUIContext.LAYOUT.registerGetFunctionProvider(GUIRegistry.class);
    }
}
