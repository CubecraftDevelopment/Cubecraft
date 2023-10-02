package ink.flybird.cubecraft.resource.provider;

import ink.flybird.cubecraft.resource.item.IResource;

import java.io.InputStream;

public final class InternalResourceLoader extends ResourceLoader {
    @Override
    public InputStream getStream(IResource resource) {
        InputStream inputStream = this.getClass().getResourceAsStream(resource.getAbsolutePath());
        if (inputStream == null) {
            inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(resource.getAbsolutePath());
        }
        return inputStream;
    }

    @Override
    public int getPriority() {
        return -2;
    }
}
