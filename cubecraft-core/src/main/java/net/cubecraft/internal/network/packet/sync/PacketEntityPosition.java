package net.cubecraft.internal.network.packet.sync;

import io.netty.buffer.ByteBuf;
import me.gb2022.commons.registry.TypeItem;
import net.cubecraft.net.packet.Packet;
import net.cubecraft.net.packet.PacketConstructor;
import net.cubecraft.world.entity.EntityLocation;

import java.nio.charset.StandardCharsets;

/**
 * this packet transform a position update.
 * it could be processed or send on both side
 */
@TypeItem("cubecraft:entity_position")
public final class PacketEntityPosition implements Packet {
    private String uuid;
    private EntityLocation location = new EntityLocation(0, 0, 0, 0, 0, 0, "cubecraft:overworld");

    public PacketEntityPosition(String uuid, EntityLocation location) {
        this.uuid = uuid;
        this.location = location;
    }

    @PacketConstructor
    public PacketEntityPosition() {
    }

    @Override
    public void writePacketData(ByteBuf buffer) {
        buffer.writeDouble(location.getX())
                .writeDouble(location.getY())
                .writeDouble(location.getZ())
                .writeDouble(location.getXRot())
                .writeDouble(location.getYRot())
                .writeDouble(location.getZRot())
                .writeCharSequence(location.getDim(), StandardCharsets.UTF_8);
    }

    @Override
    public void readPacketData(ByteBuf buffer) {
        location.setX(buffer.readDouble());
        location.setY(buffer.readDouble());
        location.setZ(buffer.readDouble());
        location.setXRot(buffer.readDouble());
        location.setYRot(buffer.readDouble());
        location.setZRot(buffer.readDouble());
        location.setDim((String) buffer.readCharSequence(buffer.readableBytes(), StandardCharsets.UTF_8));
    }

    public String getUuid() {
        return uuid;
    }

    public EntityLocation getLocation() {
        return location;
    }
}