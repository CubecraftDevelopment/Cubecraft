package ink.flybird.cubecraft.auth;

import ink.flybird.fcommon.nbt.NBTTagCompound;

public interface SessionService {
    boolean validSession(Session session);
    NBTTagCompound write(Session session);
    void read(Session session, NBTTagCompound tag);
    String genUUID(Session session);
}
