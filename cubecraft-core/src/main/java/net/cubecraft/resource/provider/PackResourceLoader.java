package net.cubecraft.resource.provider;

import net.cubecraft.resource.ResourcePack;
import net.cubecraft.resource.item.IResource;

import java.io.InputStream;

//todo:resource loader,resource pack
public final class PackResourceLoader extends ResourceLoader {
    private final int priority;
    private final ResourcePack pack;

    public PackResourceLoader(int priority, ResourcePack pack) {
        this.priority = priority;
        this.pack = pack;
    }

    public ResourcePack getPack() {
        return pack;
    }

    @Override
    public InputStream getStream(IResource resource) {
        return null;
    }

    @Override
    public int getPriority() {
        return priority;
    }
}
