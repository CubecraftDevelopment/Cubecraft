package ink.flybird.cubecraft.client.net;

import ink.flybird.cubecraft.client.CubecraftClient;
import io.flybird.cubecraft.net.INetHandler;
import io.flybird.cubecraft.net.packet.Packet;

public abstract class ClientNetHandler implements INetHandler {

    protected final void sendPacket(Packet pkt){
        CubecraftClient.CLIENT.getClientIO().sendPacket(pkt);
    }

    protected final void closeConnection(){
        CubecraftClient.CLIENT.getClientIO().closeConnection();
    }
}


