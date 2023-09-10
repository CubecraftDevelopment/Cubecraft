package ink.flybird.cubecraft.client.resources.provider;

import ink.flybird.cubecraft.client.resources.ResourcePack;
import ink.flybird.cubecraft.client.resources.resource.IResource;

import java.io.InputStream;

//todo:resource loader,resource pack
public final class PackResourceLoader extends ResourceLoader {
    private final ResourcePack pack;

    public PackResourceLoader(ResourcePack pack) {
        this.pack = pack;
    }

    public ResourcePack getPack() {
        return pack;
    }

    @Override
    public InputStream getStream(IResource resource) {
        return null;
    }
}
