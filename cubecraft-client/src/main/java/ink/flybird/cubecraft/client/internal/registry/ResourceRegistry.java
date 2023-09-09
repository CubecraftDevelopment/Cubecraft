package ink.flybird.cubecraft.client.internal.registry;

import ink.flybird.cubecraft.client.ClientRenderContext;
import ink.flybird.cubecraft.client.resources.Load;
import ink.flybird.cubecraft.client.resources.resource.ImageResource;
import ink.flybird.cubecraft.client.resources.resource.RawResource;
import ink.flybird.cubecraft.client.resources.resource.TextResource;
import ink.flybird.fcommon.registry.FieldRegistry;
import ink.flybird.fcommon.registry.FieldRegistryHolder;
import ink.flybird.quantum3d.textures.ITextureImage;

@FieldRegistryHolder("cubecraft")
public interface ResourceRegistry {
    @Load("pre_load")
    @FieldRegistry("studio_logo")
    ImageResource STUDIO_LOGO = new ImageResource("cubecraft", "/gui/logo/studio_logo.png");

    @Load("pre_load")
    @FieldRegistry("game_logo")
    ImageResource GAME_LOGO = new ImageResource("cubecraft", "/gui/logo/game_logo.png");

    @Load("pre_load")
    @FieldRegistry("game_icon")
    RawResource GAME_ICON = new RawResource("cubecraft", "/gui/logo/game_icon.png");















    @FieldRegistry("splash_text")
    TextResource SPLASH_TEXT = new TextResource("cubecraft:/splash.txt");



    @FieldRegistry("image_bg")
    ImageResource IMAGE_BG = ClientRenderContext.RESOURCE_MANAGER.loadResource(new ImageResource("cubecraft:/gui/bg.png"));

    @FieldRegistry("ascii_page")
    ImageResource ASCII_PAGE = ClientRenderContext.RESOURCE_MANAGER.getResource("cubecraft", "/font/unicode_page_00.png", ImageResource.class);

    @FieldRegistry("toast")
    ITextureImage TOAST = ClientRenderContext.RESOURCE_MANAGER.getResource(
            "cubecraft",
            "/gui/controls/toast.png",
            ImageResource.class
    );


}