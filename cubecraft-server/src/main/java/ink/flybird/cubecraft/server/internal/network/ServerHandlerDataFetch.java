package ink.flybird.cubecraft.server.internal.network;

import ink.flybird.cubecraft.internal.network.NetHandlerType;
import ink.flybird.cubecraft.internal.network.packet.*;
import ink.flybird.cubecraft.internal.network.packet.chunk.PacketChunkSection;
import ink.flybird.cubecraft.net.NetHandlerContext;
import ink.flybird.cubecraft.net.packet.PacketListener;
import ink.flybird.cubecraft.server.ServerSharedContext;
import ink.flybird.cubecraft.server.net.ServerNetHandler;
import ink.flybird.cubecraft.world.IWorld;
import ink.flybird.cubecraft.world.chunk.*;
import ink.flybird.cubecraft.world.entity.Entity;
import ink.flybird.fcommon.registry.TypeItem;
import ink.flybird.fcommon.threading.Task;

@TypeItem(NetHandlerType.SERVER_DATA_FETCH)
public class ServerHandlerDataFetch extends ServerNetHandler {

}
