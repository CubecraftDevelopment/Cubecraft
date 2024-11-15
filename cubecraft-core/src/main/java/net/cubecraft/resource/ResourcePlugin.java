package net.cubecraft.resource;

public interface ResourcePlugin {
    void onResourceReload(ResourceManager resourceManager, String stage);
}
