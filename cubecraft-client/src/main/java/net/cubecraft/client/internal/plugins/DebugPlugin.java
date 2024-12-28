package net.cubecraft.client.internal.plugins;

import me.gb2022.commons.event.EventHandler;
import me.gb2022.quantum3d.device.KeyboardButton;
import me.gb2022.quantum3d.device.event.KeyboardReleaseEvent;
import net.cubecraft.SharedContext;
import net.cubecraft.client.ClientComponent;
import net.cubecraft.client.ClientContext;
import net.cubecraft.client.CubecraftClient;
import net.cubecraft.client.context.ClientGUIContext;
import net.cubecraft.client.util.control.InputCommand;
import net.cubecraft.client.util.control.InputSettingItem;
import net.cubecraft.client.gui.ComponentRenderer;
import net.cubecraft.client.resource.ModelAsset;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class DebugPlugin extends ClientComponent {
    public static final InputSettingItem KEY_DEBUG = new InputSettingItem("misc", "debug", InputCommand.KEY_F3);
    private static final Logger LOGGER = LogManager.getLogger("Debug");

    @Override
    public void clientSetup(CubecraftClient client) {
        client.getClientEventBus().registerEventListener(this);
        client.getDeviceEventBus().registerEventListener(this);
    }

    @EventHandler
    public void onKeyPress(KeyboardReleaseEvent event) {
        if (KEY_DEBUG.isActive(event.getKeyboard(), null)) {
            if (event.getKey() == KeyboardButton.KEY_R) {
                var rm=ClientContext.RESOURCE_MANAGER;

                rm.load("default", true);
                rm.load("client:default", true);

                for (var id : ClientGUIContext.NODE.keySet()) {
                    var clazz = ClientGUIContext.NODE.get(id);

                    id = id.replaceAll("([a-z])([A-Z])", "$1_$2")  // 小写字母和大写字母之间插入下划线
                            .replaceAll("([A-Z])([A-Z][a-z])", "$1_$2")  // 处理连续的大写字母
                            .toLowerCase();

                    var asset = new ModelAsset("cubecraft:/ui/" + id + ".json");
                    var resID = "cubecraft:" + id + "_render_controller";

                    rm.registerResource("default", resID, asset);
                    rm.loadResource(asset);
                    ComponentRenderer renderer = SharedContext.createJsonReader().fromJson(asset.getRawText(), ComponentRenderer.class);
                    ClientGUIContext.COMPONENT_RENDERER.put(clazz, renderer);
                }


                LOGGER.info("[resource]Active [default] reloaded");
            }
        } else {
            if (KEY_DEBUG.isTriggered(event.getKeyboard(), null, event.getKey(), null)) {
                this.client.isDebug = !this.client.isDebug;
            }
        }
    }
}
