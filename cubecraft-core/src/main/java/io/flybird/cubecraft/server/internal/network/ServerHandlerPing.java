package io.flybird.cubecraft.server.internal.network;

import com.google.gson.Gson;
import io.flybird.cubecraft.net.NetHandlerContext;
import io.flybird.cubecraft.net.packet.PacketListener;
import io.flybird.cubecraft.net.packet.PingPacket;
import io.flybird.cubecraft.server.CubecraftServer;
import io.flybird.cubecraft.server.ServerRegistries;
import io.flybird.cubecraft.server.net.ServerNetHandler;
import ink.flybird.fcommon.registry.TypeItem;

@TypeItem("cubecraft:server_ping")
public class ServerHandlerPing extends ServerNetHandler {

    @PacketListener
    public void onPing(PingPacket packet, NetHandlerContext context){
        packet.properties().setProperty("version",CubecraftServer.VERSION.replace('.','_'));
        packet.properties().setProperty("players",new Gson().toJson(ServerRegistries.SERVER.getPlayers().getPlayerNames(),String[].class));
    }
}