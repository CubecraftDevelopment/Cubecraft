package ink.flybird.cubecraft.client.resources.provider;

import ink.flybird.cubecraft.client.resources.resource.IResource;
import ink.flybird.cubecraft.register.SharedContext;

import java.io.InputStream;

public final class ModResourceLoader extends ResourceLoader {
    @Override
    public InputStream getStream(IResource resource) {
        return SharedContext.CLASS_LOADER.getResourceAsStream(resource.getAbsolutePath());
    }

    @Override
    public int getPriority() {
        return -1;
    }
}
