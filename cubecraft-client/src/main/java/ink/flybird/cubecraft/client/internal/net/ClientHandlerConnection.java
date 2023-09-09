package ink.flybird.cubecraft.client.internal.net;

import ink.flybird.cubecraft.client.CubecraftClient;
import ink.flybird.cubecraft.client.net.ClientNetHandler;
import io.flybird.cubecraft.internal.network.NetHandlerType;
import io.flybird.cubecraft.internal.network.packet.join.PacketPlayerJoinResponse;
import io.flybird.cubecraft.net.NetHandlerContext;
import io.flybird.cubecraft.net.packet.ConnectSuccessPacket;
import io.flybird.cubecraft.net.packet.PacketListener;
import io.flybird.cubecraft.internal.network.packet.join.PacketPlayerJoinWorld;
import io.flybird.cubecraft.internal.network.packet.join.PacketPlayerJoinWorldResponse;
import ink.flybird.fcommon.registry.TypeItem;

@TypeItem(NetHandlerType.CLIENT_CONNECTION)
public class ClientHandlerConnection extends ClientNetHandler {

    @PacketListener
    public void onJoinResponse(PacketPlayerJoinResponse response, NetHandlerContext ctx){
        if(response.isAccepted()){
            ctx.sendPacket(new PacketPlayerJoinWorld());
        }else{
            System.out.println(response.getReason());
            ctx.closeConnection();
        }
    }

    @PacketListener
    public void onJoinResponse(PacketPlayerJoinWorldResponse packet, NetHandlerContext ctx){
        CubecraftClient.CLIENT.setClientLevelInfo(packet.getLevelInfo());
        //todo:set client world
    }

    @PacketListener
    public void onConnectSuccess(ConnectSuccessPacket packet,NetHandlerContext ctx){

    }
}
