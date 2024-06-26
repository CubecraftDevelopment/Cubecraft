package net.cubecraft.resource.provider;

import net.cubecraft.resource.ResourcePack;
import net.cubecraft.resource.item.IResource;

import java.io.IOException;
import java.io.InputStream;

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
        try {
            return this.pack.getInput(resource.getAbsolutePath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getPriority() {
        return priority;
    }
}
