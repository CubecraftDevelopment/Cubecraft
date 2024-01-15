package net.cubecraft.client.context;

import net.cubecraft.client.ClientSharedContext;
import net.cubecraft.client.CubecraftClient;
import net.cubecraft.client.gui.screen.HUDScreen;
import net.cubecraft.internal.entity.EntityPlayer;
import net.cubecraft.level.Level;
import net.cubecraft.world.IWorld;

public class ClientWorldContext extends ClientContext {
    private Level level;
    private IWorld world;
    private EntityPlayer player;

    public ClientWorldContext(CubecraftClient client) {
        super(client);
    }

    @Override
    public void joinLevel(Level level) {
        this.player = new EntityPlayer(null, ClientSharedContext.getClient().getSession());
        this.client.setWorld(level.getLocation(this.player).getWorld(level));
        level.join(this.player);
    }

    @Override
    public void leaveLevel() {
        this.level = null;
        this.world = null;
        this.player = null;
    }

    @Override
    public void joinWorld(IWorld world) {
        this.world = world;
        this.player.setWorld(world);

        ClientSharedContext.getClient().getClientGUIContext().setScreen(new HUDScreen());
    }

    @Override
    public void tick() {
        if (this.player != null) {
            this.player.tick();
        }
    }

    public Level getLevel() {
        return level;
    }

    public IWorld getWorld() {
        return world;
    }

    public EntityPlayer getPlayer() {
        return player;
    }
}
