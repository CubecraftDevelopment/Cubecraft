package io.flybird.cubecraft.internal.auth;

import ink.flybird.fcommon.nbt.NBTTagCompound;
import io.flybird.cubecraft.auth.Session;
import io.flybird.cubecraft.auth.SessionService;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class DefaultSessionService implements SessionService {
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
}
