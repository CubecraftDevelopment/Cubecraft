package net.cubecraft.resource;

import net.cubecraft.resource.item.IResource;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class MultiAssetContainer<R extends IResource> {
    private final Map<String, Set<R>> channels = new HashMap<>();


    public void addResource(String target, R resource) {
        getChannel(target).add(resource);
    }

    public Set<R> getChannel(String channel) {
        return channels.computeIfAbsent(channel, k -> new HashSet<>());
    }

    public Set<String> getChannels() {
        return channels.keySet();
    }
}
