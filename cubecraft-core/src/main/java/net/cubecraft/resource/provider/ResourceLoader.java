package net.cubecraft.resource.provider;

import net.cubecraft.resource.item.IResource;

import java.io.InputStream;

public abstract class ResourceLoader {
    public boolean load(IResource resource) {
        InputStream stream = this.getStream(resource);
        if (stream == null) {
            return false;
        }
        try {
            resource.loadResource(stream);
            stream.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    public int getPriority() {
        return 0;
    }

    public abstract InputStream getStream(IResource resource);
}
