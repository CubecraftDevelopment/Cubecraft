package net.cubecraft.resource.provider;

import net.cubecraft.resource.item.IResource;
import net.cubecraft.SharedContext;

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
