package io.flybird.cubecraft.internal.network;

import io.flybird.cubecraft.internal.network.packet.*;
import io.flybird.cubecraft.internal.network.packet.join.PacketPlayerJoinRequest;
import io.flybird.cubecraft.internal.network.packet.join.PacketPlayerJoinResponse;
import io.flybird.cubecraft.internal.network.packet.join.PacketPlayerJoinWorld;
import io.flybird.cubecraft.internal.network.packet.join.PacketPlayerJoinWorldResponse;
import io.flybird.cubecraft.net.packet.Packet;
import ink.flybird.fcommon.registry.ItemRegisterFunc;
import ink.flybird.fcommon.registry.ConstructingMap;

public class PacketRegistry {
    /**
     * connection:
     * connect/disconnect
     * @param map register target
     */
    @ItemRegisterFunc(Packet.class)
    public void connection(ConstructingMap<Packet> map) {
        map.registerItem(PacketPlayerJoinRequest.class);
        map.registerItem(PacketPlayerJoinResponse.class);
        map.registerItem(PacketPlayerKicked.class);
        map.registerItem(PacketPlayerLeave.class);
        map.registerItem(PacketPlayerJoinWorld.class);
        map.registerItem(PacketPlayerJoinWorldResponse.class);
    }

    /**
     * playing
     * connect/disconnect
     * @param map register target
     */
    @ItemRegisterFunc(Packet.class)
    public void playing(ConstructingMap<Packet> map) {
        map.registerItem(PacketBlockChange.class);
        map.registerItem(PacketAttack.class);
        map.registerItem(PacketChunkFragment.class);
        map.registerItem(PacketChunk2DData.class);
        map.registerItem(PacketFullChunkData.class);
        map.registerItem(PacketChunkGet.class);
        map.registerItem(PacketChunkLoad.class);
        map.registerItem(PacketEntityPosition.class);
    }
}
