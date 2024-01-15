package net.cubecraft.event.mod;

import net.cubecraft.mod.ModManager;
import net.cubecraft.mod.object.Mod;

public final class ModConstructEvent extends ModEvent {
    public ModConstructEvent(ModManager modManager, Mod mod) {
        super(modManager, mod);
    }
}
