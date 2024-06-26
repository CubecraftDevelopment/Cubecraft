package net.cubecraft.auth;

import me.gb2022.commons.nbt.NBTTagCompound;

public interface SessionService {
    boolean validSession(Session session);

    NBTTagCompound write(Session session);

    void read(Session session, NBTTagCompound tag);

    String genUUID(Session session);

    String getServiceName();
}
