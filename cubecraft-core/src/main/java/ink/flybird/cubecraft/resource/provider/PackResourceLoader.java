package ink.flybird.cubecraft.resource.provider;

import ink.flybird.cubecraft.resource.ResourcePack;
import ink.flybird.cubecraft.resource.item.IResource;

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
