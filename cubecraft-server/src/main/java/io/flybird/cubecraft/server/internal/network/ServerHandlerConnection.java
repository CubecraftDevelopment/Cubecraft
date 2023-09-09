package io.flybird.cubecraft.server.internal.network;

import io.flybird.cubecraft.internal.network.NetHandlerType;
import io.flybird.cubecraft.internal.network.packet.*;
import io.flybird.cubecraft.internal.network.packet.join.PacketPlayerJoinWorld;
import io.flybird.cubecraft.internal.network.packet.join.PacketPlayerJoinWorldResponse;
import io.flybird.cubecraft.net.NetHandlerContext;
import io.flybird.cubecraft.net.packet.DisconnectPacket;
import io.flybird.cubecraft.net.packet.PacketListener;
import io.flybird.cubecraft.server.ServerRegistries;
import io.flybird.cubecraft.server.event.PlayerKickEvent;
import io.flybird.cubecraft.server.event.PlayerLeaveEvent;
import io.flybird.cubecraft.server.event.ServerStopEvent;
import io.flybird.cubecraft.server.net.ServerNetHandler;
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
        ServerRegistries.SERVER.getEventBus().callEvent(new PlayerLeaveEvent(ServerRegistries.SERVER.getPlayers().getPlayer(ctx.from())));
        ServerRegistries.SERVER.getPlayers().remove(ctx.from());
        ctx.closeConnection();
    }

    @EventHandler
    public void onPlayerKicked(PlayerKickEvent e){
        this.sendPacket(ServerRegistries.SERVER.getPlayers().getAddr(e.player()), new PacketPlayerKicked(e.reason()));
    }

    @EventHandler
    public void onServerClosed(ServerStopEvent e){
        this.broadcastPacket(new PacketPlayerKicked(e.reason()));
        this.allCloseConnection();
        ServerRegistries.SERVER.getPlayers().clear();
    }
}
