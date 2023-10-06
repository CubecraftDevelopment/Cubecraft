package ink.flybird.cubecraft.level;

import ink.flybird.cubecraft.world.IWorld;
import ink.flybird.cubecraft.world.chunk.ChunkPos;
import ink.flybird.fcommon.nbt.NBTTagCompound;
import org.joml.Vector3d;
import org.joml.Vector3f;

public final class Location {
    private final String worldId;
    private final double x;
    private final double y;
    private final double z;
    private final float yaw;
    private final float pitch;
    private final float roll;

    public Location(String worldId, double x, double y, double z, float yaw, float pitch, float roll) {
        this.worldId = worldId;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.roll = roll;
    }

    public Location(NBTTagCompound tag) {
        this.x = tag.getDouble("x");
        this.y = tag.getDouble("y");
        this.z = tag.getDouble("z");
        this.yaw = tag.getFloat("yaw");
        this.pitch = tag.getFloat("pitch");
        this.roll = tag.getFloat("roll");
        this.worldId = tag.getString("dim");
    }

    public NBTTagCompound getNBT() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setDouble("x", x);
        tag.setDouble("y", y);
        tag.setDouble("z", z);
        tag.setFloat("yaw", this.yaw);
        tag.setFloat("pitch", this.pitch);
        tag.setFloat("roll", this.roll);
        tag.setString("world", this.worldId);
        return tag;
    }

    public String getWorldId() {
        return worldId;
    }


    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }


    public long getBlockX() {
        return ((long) this.getX());
    }

    public long getBlockY() {
        return ((long) this.getY());
    }

    public long getBlockZ() {
        return ((long) this.getZ());
    }


    public double getYaw() {
        return yaw;
    }

    public double getPitch() {
        return pitch;
    }

    public double getRoll() {
        return roll;
    }

    public Vector3d getPosition() {
        return new Vector3d(this.x, this.y, this.z);
    }

    public Vector3f getRotation() {
        return new Vector3f(this.yaw, this.pitch, this.roll);
    }


    public IWorld getWorld(Level level) {
        return level.getWorld(this.getWorldId());
    }

    public ChunkPos getChunkPos() {
        return ChunkPos.create(this.getBlockX(), getBlockZ());
    }
}
