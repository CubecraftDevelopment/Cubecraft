package net.cubecraft.event.mod;

import net.cubecraft.mod.ModManager;

public abstract class ModEvent {
    private final ModManager modManager;
    private final Object mod;

    public ModEvent(ModManager extensionManager, Object mod) {
        this.modManager = extensionManager;
        this.mod = mod;
    }

    public ModManager getModManager() {
        return modManager;
    }

    public <T> T getMod(Class<T> type) {
        return type.cast(this.mod);
    }
}
