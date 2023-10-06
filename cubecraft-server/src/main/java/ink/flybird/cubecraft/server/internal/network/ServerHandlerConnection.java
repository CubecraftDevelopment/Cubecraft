package ink.flybird.cubecraft.server.internal.network;

import ink.flybird.cubecraft.internal.network.NetHandlerType;
import ink.flybird.cubecraft.internal.network.packet.PacketPlayerKicked;
import ink.flybird.cubecraft.internal.network.packet.join.PacketPlayerJoinWorld;
import ink.flybird.cubecraft.internal.network.packet.join.PacketPlayerJoinWorldResponse;
import ink.flybird.cubecraft.net.NetHandlerContext;
import ink.flybird.cubecraft.net.packet.DisconnectPacket;
import ink.flybird.cubecraft.net.packet.PacketListener;
import ink.flybird.cubecraft.server.ServerSharedContext;
import ink.flybird.cubecraft.server.event.PlayerKickEvent;
import ink.flybird.cubecraft.server.event.PlayerLeaveEvent;
import ink.flybird.cubecraft.server.event.ServerStopEvent;
import ink.flybird.cubecraft.server.net.ServerNetHandler;
import ink.flybird.fcommon.registry.TypeItem;
import ink.flybird.fcommon.event.EventHandler;

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
