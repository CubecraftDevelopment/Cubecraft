package net.cubecraft.resource;

import net.cubecraft.resource.item.IResource;

import java.util.Collection;

public interface ResourceHolder {
    void getResources(Collection<IResource> list);
}
