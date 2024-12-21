package net.cubecraft.client;

import me.gb2022.commons.query.SimpleQueryHandler;
import me.gb2022.quantum3d.texture.TextureManager;
import net.cubecraft.resource.ResourceManager;
import net.cubecraft.util.setting.GameSetting;

public interface ClientContext {
    SimpleQueryHandler QUERY_HANDLER = new SimpleQueryHandler();
    ResourceManager RESOURCE_MANAGER = new ResourceManager();
    TextureManager TEXTURE = new TextureManager();
}
