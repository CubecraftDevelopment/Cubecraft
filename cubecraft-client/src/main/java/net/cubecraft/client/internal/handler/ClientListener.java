package net.cubecraft.client.internal.handler;

import me.gb2022.commons.event.EventHandler;
import me.gb2022.quantum3d.device.KeyboardButton;
import me.gb2022.quantum3d.device.event.KeyboardPressEvent;
import me.gb2022.quantum3d.device.event.WindowFocusEvent;
import net.cubecraft.client.CubecraftClient;
import net.cubecraft.client.context.ClientGUIContext;
import net.cubecraft.client.event.ClientRendererInitializeEvent;
import net.cubecraft.client.registry.ClientSettings;


public class ClientListener {

    @EventHandler
    public void onClientRendererInitialize(ClientRendererInitializeEvent e) {
        e.renderer().world.getEventBus().registerEventListener(new ParticleHandler());
    }

    @EventHandler
    public void onKeyEventPressed(KeyboardPressEvent e) {
        if (e.getKey() == KeyboardButton.KEY_F11) {
            CubecraftClient.getInstance().getWindow().setFullscreen(!CubecraftClient.getInstance().getWindow().isFullscreen());
        }
        if (e.getKey() == KeyboardButton.KEY_ESCAPE) {
            ClientGUIContext guiManager = CubecraftClient.getInstance().getClientGUIContext();
            if (guiManager.getScreen().getParentScreen() != null) {
                guiManager.setScreen(guiManager.getScreen().getParentScreen());
            }
        }
        if (e.getKey() == KeyboardButton.KEY_F3) {
            CubecraftClient.getInstance().isDebug = !CubecraftClient.getInstance().isDebug;
        }
    }

    @EventHandler
    public void onFocusEvent(WindowFocusEvent event) {
        if (event.isFocus()) {
            CubecraftClient.getInstance().maxFPS = ClientSettings.RenderSetting.MAX_FPS.getValue();
        } else {
            CubecraftClient.getInstance().maxFPS = ClientSettings.RenderSetting.INACTIVE_FPS.getValue();
        }
    }
}
