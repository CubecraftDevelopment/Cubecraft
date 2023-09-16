package ink.flybird.cubecraft.client;

import ink.flybird.cubecraft.client.event.ScopedKeypressEvent;
import ink.flybird.cubecraft.event.SettingReloadEvent;
import ink.flybird.fcommon.event.EventBus;
import ink.flybird.fcommon.event.EventHandler;
import ink.flybird.fcommon.event.SimpleEventBus;
import ink.flybird.quantum3d.device.Keyboard;
import ink.flybird.quantum3d.device.KeyboardButton;
import ink.flybird.quantum3d.device.adapter.KeyboardEventAdapter;
import ink.flybird.quantum3d.device.event.KeyboardPressEvent;

import java.util.HashMap;
import java.util.Map;

public class KeyBindingController {
    private final EventBus eventBus = new SimpleEventBus();
    private final Keyboard keyboard;
    private String scope;
    private final HashMap<String, HashMap<String, String>> map = new HashMap<>(128);

    public KeyBindingController(Keyboard keyboard, KeyboardEventAdapter adapter) {
        this.keyboard = keyboard;
        //this.adapter.getEventBus().registerEventListener(this);
    }

    public static String encodeBinding(KeyboardButton... buttons) {
        StringBuilder sb = new StringBuilder();
        for (KeyboardButton button : buttons) {
            sb.append(button.name()).append("+");
        }
        return sb.substring(0, sb.length());
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public void setKey(String scope, String action, KeyboardButton button) {
        if (!this.map.containsKey(scope)) {
            this.map.put(scope, new HashMap<>());
        }
        Map<String, String> map = this.map.get(scope);
        map.put(button.name(), action);
    }

    @EventHandler
    public void onEvent(KeyboardPressEvent event) {
        Map<String, String> map = this.map.get(this.scope);
        if(!map.containsKey(event.getKey().name())){
            return;
        }
        this.getEventBus().callEvent(new ScopedKeypressEvent(this.scope, map.get(event.getKey().name())), this.scope);
    }

    @EventHandler
    public void onSettingReload(SettingReloadEvent event) {

    }

    public EventBus getEventBus() {
        return eventBus;
    }
}
