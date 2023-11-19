package net.cubecraft.event;

import net.cubecraft.util.setting.GameSetting;

public final class SettingReloadEvent{
    private final GameSetting setting;

    public SettingReloadEvent(GameSetting setting) {
        this.setting = setting;
    }

    public GameSetting getSetting() {
        return setting;
    }

    public boolean isNodeChanged(String node){
        return true;
    }
}
