package ink.flybird.cubecraft.client.internal.registry;

import ink.flybird.fcommon.registry.FieldRegistry;
import ink.flybird.fcommon.registry.FieldRegistryHolder;
import ink.flybird.quantum3d.textures.Texture;
import ink.flybird.quantum3d.textures.Texture2D;

//todo:资源格式统一，UI动画
@FieldRegistryHolder(value = "cubecraft")
public interface TextureRegistry {
    @FieldRegistry("studio_logo")
    Texture2D STUDIO_LOGO = new Texture2D(false, false).load(ResourceRegistry.STUDIO_LOGO);

    @FieldRegistry("game_logo")
    Texture2D GAME_LOGO = new Texture2D(false, false).load(ResourceRegistry.GAME_LOGO);

    @FieldRegistry("image_bg")
    Texture2D IMAGE_BG = new Texture2D(false, false).load(ResourceRegistry.IMAGE_BG);

    @FieldRegistry("ascii_page")
    Texture2D ASCII_PAGE = new Texture2D(false,false).load(ResourceRegistry.ASCII_PAGE);

    @FieldRegistry("toast")
    Texture TOAST = new Texture2D(false,false).load(ResourceRegistry.TOAST);
}
