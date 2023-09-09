package io.flybird.cubecraft.server.internal.network;

import io.flybird.cubecraft.internal.network.NetHandlerType;
import io.flybird.cubecraft.internal.network.packet.*;
import io.flybird.cubecraft.internal.network.packet.chunk.PacketChunkSection;
import io.flybird.cubecraft.net.NetHandlerContext;
import io.flybird.cubecraft.net.packet.PacketListener;
import io.flybird.cubecraft.server.ServerRegistries;
import io.flybird.cubecraft.server.net.ServerNetHandler;
import io.flybird.cubecraft.world.IWorld;
import io.flybird.cubecraft.world.chunk.*;
import io.flybird.cubecraft.world.entity.Entity;
import ink.flybird.fcommon.registry.TypeItem;
import ink.flybird.fcommon.threading.Task;

@TypeItem(NetHandlerType.SERVER_DATA_FETCH)
public class ServerHandlerDataFetch extends ServerNetHandler {

    //todo:测试区块收发
    @PacketListener
    public void onPacketChunkGetRequest(PacketChunkGet packet, NetHandlerContext ctx) {
        Task.submit(new Task() {
            @Override
            public void run() {
                IWorld dim = ServerRegistries.SERVER.getDim(packet.getWorld());
                if (dim == null) {
                    return;
                }
                long x = packet.getChunkX();
                long z = packet.getChunkZ();
                ChunkPos pos = new ChunkPos(x, z);

                dim.loadChunk(pos, new ChunkLoadTicket(ChunkLoadLevel.None_TICKING, 1000));
                if (dim.getChunk(pos) == null) {
                    return;
                }
                WorldChunk c = dim.getChunk(pos);
                for (int i = 0; i < Chunk.SECTION_SIZE; i++) {
                    ctx.sendPacket(new PacketChunkSection(c, packet.getWorld(), z, i, x));
                }
                this.finish();
            }
        });
    }

    @PacketListener
    public void onPacketChunkLoadRequest(PacketChunkLoad packet, NetHandlerContext ctx) {
        IWorld dim = ServerRegistries.SERVER.getDim(packet.getWorld());
        if (dim == null) {
            return;
        }
        dim.loadChunk(new ChunkPos(packet.getChunkX(), packet.getChunkZ()), packet.getTicket());
    }

    @PacketListener
    public void onPacketPositionChange(PacketEntityPosition packet, NetHandlerContext ctx) {
        Entity e = ServerRegistries.SERVER.getEntity(packet.getUuid());
        if (e != null) {
            e.setLocation(packet.getNewLoc(), ServerRegistries.SERVER.getLevel().getDims());
        }
    }

    @PacketListener
    public void onPacketBlockChange(PacketBlockChange packet, NetHandlerContext ctx) {
        ServerRegistries.SERVER.getDim(packet.getWorld()).loadChunkIfNull(ChunkPos.fromWorldPos(packet.getX(), packet.getZ()), new ChunkLoadTicket(ChunkLoadLevel.None_TICKING, 1));
        ServerRegistries.SERVER.getDim(packet.getWorld()).setBlockState(packet.getState());
    }

    @PacketListener
    public void onPacketAttack(PacketAttack packet, NetHandlerContext ctx) {
        Entity e = ServerRegistries.SERVER.getEntity(packet.getUuid0());
        Entity e2 = ServerRegistries.SERVER.getEntity(packet.getUuid1());
        if (e == null || e2 == null) {
            return;
        }
        //todo:e2扣血
    }
}
