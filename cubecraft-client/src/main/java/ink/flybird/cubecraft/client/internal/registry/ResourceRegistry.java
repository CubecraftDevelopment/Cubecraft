package ink.flybird.cubecraft.client.internal.registry;

import ink.flybird.cubecraft.client.resources.Load;
import ink.flybird.cubecraft.client.resources.item.ImageResource;
import ink.flybird.cubecraft.client.resources.item.RawResource;
import ink.flybird.cubecraft.client.resources.item.TextResource;
import ink.flybird.fcommon.registry.FieldRegistry;
import ink.flybird.fcommon.registry.FieldRegistryHolder;
import ink.flybird.quantum3d_legacy.textures.ITextureImage;

@FieldRegistryHolder("cubecraft")
public interface ResourceRegistry {
    @Load("client:startup")
    @FieldRegistry("studio_logo")
    ImageResource STUDIO_LOGO = new ImageResource("cubecraft:/texture/gui/logo/studio_logo.png");

    @Load("client:startup")
    @FieldRegistry("game_logo")
    ImageResource GAME_LOGO = new ImageResource("cubecraft:/texture/gui/logo/game_logo.png");

    @Load("client:startup")
    @FieldRegistry("game_icon")
    RawResource GAME_ICON = new RawResource("cubecraft:/texture/gui/logo/game_icon.png");

    @FieldRegistry("image_bg")
    ImageResource IMAGE_BG = new ImageResource("cubecraft:/texture/gui/bg.png");

    @FieldRegistry("splash_text")
    TextResource SPLASH_TEXT = new TextResource("cubecraft:/texture/splash.txt");


    @FieldRegistry("ascii_page")
    ImageResource ASCII_PAGE = new ImageResource("cubecraft:/texture/font/unicode_page_00.png");

    @FieldRegistry("toast")
    ITextureImage TOAST = new ImageResource("cubecraft:/texture/gui/controls/toast.png");
}