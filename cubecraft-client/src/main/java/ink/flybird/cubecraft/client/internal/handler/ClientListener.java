package ink.flybird.cubecraft.client.internal.handler;

import ink.flybird.cubecraft.client.ClientRenderContext;
import ink.flybird.cubecraft.client.CubecraftClient;
import ink.flybird.cubecraft.client.event.ClientRendererInitializeEvent;
import ink.flybird.cubecraft.client.gui.GUIManager;
import ink.flybird.cubecraft.client.internal.renderer.block.ModelRenderer;
import ink.flybird.cubecraft.client.internal.renderer.world.ParticleRenderer;
import ink.flybird.cubecraft.client.resources.ResourceLocation;
import ink.flybird.fcommon.event.EventHandler;
import ink.flybird.quantum3d.device.KeyboardButton;
import ink.flybird.quantum3d.device.event.KeyboardPressEvent;
import ink.flybird.cubecraft.event.BlockRegisterEvent;


public class ClientListener {

    @EventHandler
    public void onBlockRegister(BlockRegisterEvent e) {
        String id = e.id();
        ClientRenderContext.BLOCK_RENDERER.registerItem(id, new ModelRenderer("/resource/cubecraft/model/block/"+id + ".json"));
    }

    @EventHandler
    public void onClientRendererInitialize(ClientRendererInitializeEvent e) {
        e.renderer().world.getEventBus().registerEventListener(new ParticleHandler());
        ((ParticleRenderer) e.renderer().renderers.get("cubecraft:particle_renderer")).setParticleEngine(e.client().getParticleEngine());
    }

    @EventHandler
    public void onKeyEventPressed(KeyboardPressEvent e) {
        if (e.getKey() == KeyboardButton.KEY_F11) {
            CubecraftClient.CLIENT.getWindow().setFullscreen(!CubecraftClient.CLIENT.getWindow().isFullscreen());
        }
        if (e.getKey() == KeyboardButton.KEY_ESCAPE) {
            GUIManager guiManager = CubecraftClient.CLIENT.getGuiManager();
            if (guiManager.getScreen().getParentScreen() != null) {
                guiManager.setScreen(guiManager.getScreen().getParentScreen());
            }
        }
        if (e.getKey() == KeyboardButton.KEY_F3) {
            CubecraftClient.CLIENT.isDebug = !CubecraftClient.CLIENT.isDebug;
        }
    }
}
