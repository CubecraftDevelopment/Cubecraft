package net.cubecraft.event.resource;

import net.cubecraft.resource.item.IResource;

public class ResourceLoadItemEvent extends ResourceLoadEvent{
    private final IResource resource;

    public ResourceLoadItemEvent(String stage, IResource resource) {
        super(stage);
        this.resource = resource;
    }

    public IResource getResource() {
        return resource;
    }
}
