package ink.flybird.cubecraft.server.internal.network;

import com.google.gson.Gson;
import ink.flybird.cubecraft.net.NetHandlerContext;
import ink.flybird.cubecraft.net.packet.PacketListener;
import ink.flybird.cubecraft.net.packet.PingPacket;
import ink.flybird.cubecraft.server.CubecraftServer;
import ink.flybird.cubecraft.server.ServerRegistries;
import ink.flybird.cubecraft.server.net.ServerNetHandler;
import ink.flybird.fcommon.registry.TypeItem;

@TypeItem("cubecraft:server_ping")
public class ServerHandlerPing extends ServerNetHandler {

    @PacketListener
    public void onPing(PingPacket packet, NetHandlerContext context){
        packet.properties().setProperty("version",CubecraftServer.VERSION.replace('.','_'));
        packet.properties().setProperty("players",new Gson().toJson(ServerRegistries.SERVER.getPlayers().getPlayerNames(),String[].class));
    }
}