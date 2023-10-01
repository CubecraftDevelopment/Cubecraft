package ink.flybird.cubecraft.client.internal.registry;

import ink.flybird.cubecraft.client.ClientSharedContext;
import ink.flybird.cubecraft.client.event.resource.ResourceLoadFinishEvent;
import ink.flybird.fcommon.event.EventHandler;
import ink.flybird.fcommon.event.SubscribedEvent;
import ink.flybird.fcommon.registry.FieldRegistry;
import ink.flybird.fcommon.registry.FieldRegistryHolder;
import ink.flybird.quantum3d_legacy.textures.Texture;
import ink.flybird.quantum3d_legacy.textures.Texture2D;

import java.util.Objects;

//todo:资源格式统一，UI动画
@FieldRegistryHolder(value = "cubecraft")
public interface TextureRegistry {
    @FieldRegistry("studio_logo")
    Texture2D STUDIO_LOGO = new Texture2D(false, false);
    @FieldRegistry("game_logo")
    Texture2D GAME_LOGO = new Texture2D(false, false);

    @FieldRegistry("image_bg")
    Texture2D IMAGE_BG = new Texture2D(false, false);

    @FieldRegistry("ascii_page")
    Texture2D ASCII_PAGE = new Texture2D(false,false);

    @FieldRegistry("toast")
    Texture TOAST = new Texture2D(false,false);

    @EventHandler
    static void onResourceLoadComplete(ResourceLoadFinishEvent event){
        if(Objects.equals(event.getStage(), "startup")) {
            STUDIO_LOGO.load(ResourceRegistry.STUDIO_LOGO);
            GAME_LOGO.load(ResourceRegistry.GAME_LOGO);
            return;
        }
        TOAST.load(ResourceRegistry.TOAST);
        IMAGE_BG.load(ResourceRegistry.IMAGE_BG);
        ASCII_PAGE.load(ResourceRegistry.ASCII_PAGE);
    }
}
