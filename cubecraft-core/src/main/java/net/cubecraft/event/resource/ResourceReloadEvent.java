package net.cubecraft.event.resource;

import net.cubecraft.resource.ResourceManager;


@Deprecated
public record ResourceReloadEvent(ResourceManager resourceManager) {
}
