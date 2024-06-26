package net.cubecraft.internal.auth;

import me.gb2022.commons.nbt.NBTTagCompound;
import net.cubecraft.auth.Session;
import net.cubecraft.auth.SessionService;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class OfflineSessionService implements SessionService {
    @Override
    public boolean validSession(Session session) {
        return true;
    }

    @Override
    public NBTTagCompound write(Session session) {
        NBTTagCompound tag=new NBTTagCompound();
        tag.setString("name",session.getName());
        return tag;
    }

    @Override
    public void read(Session session, NBTTagCompound tag) {
        session.setName(tag.getString("name"));
    }

    @Override
    public String genUUID(Session session){
        return UUID.nameUUIDFromBytes(session.getName().getBytes(StandardCharsets.UTF_8)).toString();
    }

    @Override
    public String getServiceName() {
        return "Offline";
    }
}
