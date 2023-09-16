package ink.flybird.cubecraft.internal.network.packet.chunk;

import ink.flybird.cubecraft.internal.network.packet.PacketType;
import ink.flybird.cubecraft.net.packet.PacketConstructor;
import ink.flybird.cubecraft.world.chunk.storage.UnifiedBlockSection;
import ink.flybird.fcommon.file.NBTBuilder;
import ink.flybird.fcommon.nbt.NBTTagCompound;
import ink.flybird.fcommon.registry.TypeItem;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

@TypeItem(PacketType.CHUNK_BLOCK_SECTION)
public final class PacketUnifiedBlockSection extends PacketChunkDataSection {
    private UnifiedBlockSection section;

    public PacketUnifiedBlockSection(String world, long chunkX, long chunkZ, int sectionIndex, UnifiedBlockSection section) {
        super(world, chunkX, chunkZ, sectionIndex);
        this.section = section;
    }

    @PacketConstructor
    public PacketUnifiedBlockSection() {
        this(null, 0, 0, 0, null);
    }

    @Override
    public void writePacketData(ByteBuf buffer) throws Exception {
        super.writePacketData(buffer);
        ByteBufOutputStream bufferOutput = new ByteBufOutputStream(buffer);
        GZIPOutputStream zipOutput = new GZIPOutputStream(bufferOutput);
        NBTBuilder.write(this.section.getData(), new DataOutputStream(zipOutput));
        zipOutput.close();
        bufferOutput.close();
    }

    @Override
    public void readPacketData(ByteBuf buffer) throws Exception {
        super.readPacketData(buffer);
        ByteBufInputStream bufferInput = new ByteBufInputStream(buffer);
        GZIPInputStream zipInput = new GZIPInputStream(bufferInput);
        this.section = new UnifiedBlockSection((NBTTagCompound) NBTBuilder.read(new DataInputStream(zipInput)));
        zipInput.close();
        bufferInput.close();
    }

    public UnifiedBlockSection getSection() {
        return this.section;
    }
}
