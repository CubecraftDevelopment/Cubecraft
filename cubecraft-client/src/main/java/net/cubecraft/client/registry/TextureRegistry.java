package net.cubecraft.client.registry;

import me.gb2022.commons.event.SubscribedEvent;
import net.cubecraft.event.resource.ResourceLoadFinishEvent;
import me.gb2022.commons.container.Vector;
import me.gb2022.commons.event.EventHandler;
import me.gb2022.commons.registry.FieldRegistry;
import me.gb2022.commons.registry.FieldRegistryHolder;
import me.gb2022.quantum3d.texture.Texture2D;
import me.gb2022.quantum3d.texture.Texture2DTileMap;

@FieldRegistryHolder(value = "cubecraft")
public interface TextureRegistry {
    @FieldRegistry("studio_logo")
    Texture2D STUDIO_LOGO = new Texture2D(false, false);

    @FieldRegistry("game_logo")
    Texture2D GAME_LOGO = new Texture2D(false, false);

    @FieldRegistry("image_bg")
    Texture2D IMAGE_BG = new Texture2D(false, false);

    @FieldRegistry("ascii_page")
    Texture2D ASCII_PAGE = new Texture2D(false, false);

    @FieldRegistry("toast")
    Texture2D TOAST = new Texture2D(false, false);

    @FieldRegistry("terrain_tilemap")
    Vector<Texture2DTileMap> TERRAIN_TILEMAP=new Vector<>(null);

    @EventHandler
    @SubscribedEvent("client:startup")
    static void onResourceLoadComplete(ResourceLoadFinishEvent event) {
        STUDIO_LOGO.load(ResourceRegistry.STUDIO_LOGO);
        GAME_LOGO.load(ResourceRegistry.GAME_LOGO);
        ASCII_PAGE.load(ResourceRegistry.ASCII_PAGE);
    }
}
