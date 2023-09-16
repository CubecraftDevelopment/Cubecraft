package ink.flybird.cubecraft.internal.network.packet.chunk;

import ink.flybird.cubecraft.internal.network.packet.PacketType;
import ink.flybird.cubecraft.net.packet.PacketConstructor;
import ink.flybird.cubecraft.world.chunk.storage.UnifiedLightSection;
import ink.flybird.fcommon.registry.TypeItem;
import io.netty.buffer.ByteBuf;

@TypeItem(PacketType.CHUNK_BLOCK_SECTION)
public final class PacketUnifiedLightSection extends PacketChunkDataSection {
    private UnifiedLightSection section;

    public PacketUnifiedLightSection(String world, long chunkX, long chunkZ, int sectionIndex, UnifiedLightSection section) {
        super(world, chunkX, chunkZ, sectionIndex);
        this.section = section;
    }

    @PacketConstructor
    public PacketUnifiedLightSection() {
        this(null, 0, 0, 0, null);
    }

    @Override
    public void writePacketData(ByteBuf buffer) throws Exception {
        super.writePacketData(buffer);
        buffer.writeByte(section.getBlockLight(0, 0, 0));
    }

    @Override
    public void readPacketData(ByteBuf buffer) throws Exception {
        super.readPacketData(buffer);
        this.section = new UnifiedLightSection(buffer.readByte());
    }

    public UnifiedLightSection getSection() {
        return this.section;
    }
}
