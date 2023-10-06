package ink.flybird.cubecraft.event.mod;

import ink.flybird.cubecraft.extension.ModManager;
import ink.flybird.cubecraft.extension.ModSide;

public final class ModConstructEvent extends ModEvent{
    private final ModSide side;

    public ModConstructEvent(ModManager extensionManager, Object mod, ModSide side) {
        super(extensionManager, mod);
        this.side = side;
    }

    public ModSide getSide() {
        return side;
    }
}
