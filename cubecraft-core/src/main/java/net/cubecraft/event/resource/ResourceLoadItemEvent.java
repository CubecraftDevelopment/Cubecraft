package net.cubecraft.event.resource;

import net.cubecraft.resource.item.IResource;

public class ResourceLoadItemEvent extends ResourceLoadEvent{
    private final IResource resource;
    private final String id;

    public ResourceLoadItemEvent(String stage, IResource resource, String id) {
        super(stage);
        this.resource = resource;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public IResource getResource() {
        return resource;
    }
}
