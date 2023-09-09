package io.flybird.cubecraft.event;

import io.flybird.cubecraft.util.GameSetting;

public final class SettingReloadEvent{
    //todo:设置重载时触发
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
