package net.cubecraft.client.context;

import net.cubecraft.client.ClientSharedContext;
import net.cubecraft.client.CubecraftClient;
import net.cubecraft.client.gui.screen.HUDScreen;
import net.cubecraft.internal.entity.EntityPlayer;
import net.cubecraft.level.Level;
import net.cubecraft.world.World;
import net.cubecraft.world.chunk.pos.ChunkPos;

public class ClientWorldContext extends ClientContext {
    private Level level;
    private World world;
    private EntityPlayer player;

    public ClientWorldContext(CubecraftClient client) {
        super(client);
    }

    @Override
    public void joinLevel(Level level) {
        this.player = new EntityPlayer(level, ClientSharedContext.getClient().getSession());
        level.join(this.player);
        this.world = player.getWorld();
    }

    @Override
    public void leaveLevel() {
        this.world.getLevel().leave(this.player, "left");

        this.level = null;
        this.world = null;
        this.player = null;
    }

    @Override
    public void joinWorld(World world) {
        this.world = world;
        this.player.setWorld(world);

        ClientSharedContext.getClient().getClientGUIContext().setScreen(new HUDScreen());
    }

    @Override
    public void tick() {
        if (this.player != null) {
            if(this.world.getChunk(ChunkPos.fromEntity(this.player))!=null){
                this.player.tick();
            }
        }
    }

    public Level getLevel() {
        return level;
    }

    public World getWorld() {
        return world;
    }

    public EntityPlayer getPlayer() {
        return player;
    }
}
