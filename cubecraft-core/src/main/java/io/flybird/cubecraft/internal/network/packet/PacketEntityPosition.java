package io.flybird.cubecraft.internal.network.packet;

import io.flybird.cubecraft.net.packet.Packet;
import io.flybird.cubecraft.net.packet.PacketConstructor;
import io.flybird.cubecraft.world.entity.EntityLocation;
import ink.flybird.fcommon.registry.TypeItem;
import io.netty.buffer.ByteBuf;

import java.nio.charset.StandardCharsets;

/**
 * this packet transform a position update.
 * it could be processed or send on both side
 */
@TypeItem("cubecraft:entity_position")
public class PacketEntityPosition implements Packet {
    private String uuid;
    private EntityLocation newLoc=new EntityLocation(0,0,0,0,0,0, "cubecraft:overworld");

    public PacketEntityPosition(String uuid, EntityLocation newLoc) {
        this.uuid = uuid;
        this.newLoc = newLoc;
    }

    @PacketConstructor
    public PacketEntityPosition(){}

    @Override
    public void writePacketData(ByteBuf buffer) {
        buffer.writeDouble(newLoc.getX())
                .writeDouble(newLoc.getY())
                .writeDouble(newLoc.getZ())
                .writeDouble(newLoc.getXRot())
                .writeDouble(newLoc.getYRot())
                .writeDouble(newLoc.getZRot())
                .writeCharSequence(newLoc.getDim(), StandardCharsets.UTF_8);
    }

    @Override
    public void readPacketData(ByteBuf buffer) {
        newLoc.setX(buffer.readDouble());
        newLoc.setY(buffer.readDouble());
        newLoc.setZ(buffer.readDouble());
        newLoc.setXRot(buffer.readDouble());
        newLoc.setYRot(buffer.readDouble());
        newLoc.setZRot(buffer.readDouble());
        newLoc.setDim((String) buffer.readCharSequence(buffer.readableBytes(),StandardCharsets.UTF_8));
    }

    public String getUuid() {
        return uuid;
    }

    public EntityLocation getNewLoc() {
        return newLoc;
    }
}