package net.cubecraft.client.internal.handler;

import me.gb2022.quantum3d.device.event.WindowFocusEvent;
import net.cubecraft.client.ClientSettingRegistry;
import net.cubecraft.client.CubecraftClient;
import net.cubecraft.client.event.ClientRendererInitializeEvent;
import net.cubecraft.client.gui.GUIContext;
import net.cubecraft.client.render.world.ParticleRenderer;
import net.cubecraft.event.SettingReloadEvent;
import net.cubecraft.event.register.BlockRegisterEvent;
import ink.flybird.fcommon.event.EventHandler;
import me.gb2022.quantum3d.device.KeyboardButton;
import me.gb2022.quantum3d.device.event.KeyboardPressEvent;


public class ClientListener {
    @EventHandler
    public static void onBlockRegister(BlockRegisterEvent event) {


        //todo:注册事件
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
            GUIContext guiManager = CubecraftClient.CLIENT.getGuiManager();
            if (guiManager.getScreen().getParentScreen() != null) {
                guiManager.setScreen(guiManager.getScreen().getParentScreen());
            }
        }
        if (e.getKey() == KeyboardButton.KEY_F3) {
            CubecraftClient.CLIENT.isDebug = !CubecraftClient.CLIENT.isDebug;
        }
    }

    @EventHandler
    public void onFocusEvent(WindowFocusEvent event){
        if(event.isFocus()) {
            CubecraftClient.CLIENT.maxFPS=ClientSettingRegistry.MAX_FPS.getValue();
        }else{
            CubecraftClient.CLIENT.maxFPS=ClientSettingRegistry.INACTIVE_FPS_LIMIT.getValue();
        }
    }

    @EventHandler
    public void onSettingReload(SettingReloadEvent event){
        if(CubecraftClient.CLIENT.getWindow().isFocused()) {
            CubecraftClient.CLIENT.maxFPS=ClientSettingRegistry.MAX_FPS.getValue();
        }else{
            CubecraftClient.CLIENT.maxFPS=ClientSettingRegistry.INACTIVE_FPS_LIMIT.getValue();
        }
    }
}
