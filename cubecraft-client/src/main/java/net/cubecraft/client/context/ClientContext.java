package net.cubecraft.client.context;

import net.cubecraft.client.CubecraftClient;
import net.cubecraft.level.Level;
import net.cubecraft.world.IWorld;

public abstract class ClientContext {
    protected final CubecraftClient client;

    protected ClientContext(CubecraftClient client) {
        this.client = client;
    }

    public void joinLevel(Level level) {
    }

    public void init(){
    }

    public void joinWorld(IWorld world) {
    }

    public void leaveLevel() {
    }

    public void tick(){
    }
}
