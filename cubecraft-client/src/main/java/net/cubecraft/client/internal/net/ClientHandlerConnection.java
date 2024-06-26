package net.cubecraft.client.internal.net;

import net.cubecraft.client.net.ClientNetHandler;
import net.cubecraft.internal.network.NetHandlerType;
import net.cubecraft.internal.network.packet.join.PacketPlayerJoinResponse;
import net.cubecraft.net.NetHandlerContext;
import net.cubecraft.net.packet.ConnectSuccessPacket;
import net.cubecraft.net.packet.PacketListener;
import net.cubecraft.internal.network.packet.join.PacketPlayerJoinWorld;
import net.cubecraft.internal.network.packet.join.PacketPlayerJoinWorldResponse;
import me.gb2022.commons.registry.TypeItem;

@TypeItem(NetHandlerType.CLIENT_CONNECTION)
public class ClientHandlerConnection extends ClientNetHandler {

    @PacketListener
    public void onJoinResponse(PacketPlayerJoinResponse response, NetHandlerContext ctx){
        if(response.isAccepted()){
            ctx.sendPacket(new PacketPlayerJoinWorld());
        }else{
            ctx.closeConnection();
        }
    }

    @PacketListener
    public void onJoinResponse(PacketPlayerJoinWorldResponse packet, NetHandlerContext ctx){
        //todo:set client world
    }

    @PacketListener
    public void onConnectSuccess(ConnectSuccessPacket packet, NetHandlerContext ctx){

    }
}
