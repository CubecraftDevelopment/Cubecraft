package net.cubecraft.server.internal.network;

import net.cubecraft.internal.network.NetHandlerType;
import net.cubecraft.internal.network.packet.PacketPlayerKicked;
import net.cubecraft.internal.network.packet.join.PacketPlayerJoinWorld;
import net.cubecraft.internal.network.packet.join.PacketPlayerJoinWorldResponse;
import net.cubecraft.net.NetHandlerContext;
import net.cubecraft.net.packet.DisconnectPacket;
import net.cubecraft.net.packet.PacketListener;
import net.cubecraft.server.ServerSharedContext;
import net.cubecraft.server.event.PlayerKickEvent;
import net.cubecraft.server.event.PlayerLeaveEvent;
import net.cubecraft.server.event.ServerStopEvent;
import net.cubecraft.server.net.ServerNetHandler;
import me.gb2022.commons.registry.TypeItem;
import me.gb2022.commons.event.EventHandler;

@TypeItem(NetHandlerType.SERVER_CONNECTION)
public class ServerHandlerConnection extends ServerNetHandler {

    @PacketListener
    public void onJoinRequest(PacketPlayerJoinWorld packet, NetHandlerContext ctx){
        ctx.sendPacket(new PacketPlayerJoinWorldResponse());
    }

    @PacketListener
    public void onLeaveRequest(DisconnectPacket packet, NetHandlerContext ctx){
        ServerSharedContext.SERVER.getEventBus().callEvent(new PlayerLeaveEvent(ServerSharedContext.SERVER.getPlayers().getPlayer(ctx.from())));
        ServerSharedContext.SERVER.getPlayers().remove(ctx.from());
        ctx.closeConnection();
    }

    @EventHandler
    public void onPlayerKicked(PlayerKickEvent e){
        this.sendPacket(ServerSharedContext.SERVER.getPlayers().getAddr(e.player()), new PacketPlayerKicked(e.reason()));
    }

    @EventHandler
    public void onServerClosed(ServerStopEvent e){
        this.broadcastPacket(new PacketPlayerKicked(e.reason()));
        this.allCloseConnection();
        ServerSharedContext.SERVER.getPlayers().clear();
    }
}
