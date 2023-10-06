package ink.flybird.cubecraft.client.registry;

import ink.flybird.cubecraft.client.ClientSharedContext;
import ink.flybird.cubecraft.client.gui.screen.Screen;
import ink.flybird.cubecraft.client.resource.FontAsset;
import ink.flybird.cubecraft.client.resource.TextureAsset;
import ink.flybird.cubecraft.client.resource.UIAsset;
import ink.flybird.cubecraft.event.resource.ResourceLoadFinishEvent;
import ink.flybird.cubecraft.resource.Load;
import ink.flybird.fcommon.event.EventHandler;
import ink.flybird.fcommon.registry.FieldRegistry;
import ink.flybird.fcommon.registry.FieldRegistryHolder;
import ink.flybird.quantum3d_legacy.textures.ITextureImage;

import java.util.Objects;

@FieldRegistryHolder("cubecraft")
public interface ResourceRegistry {
    //window
    @Load("client:startup")
    @FieldRegistry("studio_logo")
    TextureAsset STUDIO_LOGO = new TextureAsset("cubecraft:gui/logo/studio_logo.png");

    @Load("client:startup")
    @FieldRegistry("game_logo")
    TextureAsset GAME_LOGO = new TextureAsset("cubecraft:gui/logo/game_logo.png");

    @Load("client:startup")
    @FieldRegistry("game_icon")
    TextureAsset GAME_ICON = new TextureAsset("cubecraft:gui/logo/game_icon.png");

    //screen
    @FieldRegistry("image_bg")
    TextureAsset IMAGE_BG = new TextureAsset("cubecraft:gui/bg.png");

    @FieldRegistry("toast")
    ITextureImage TOAST = new TextureAsset("cubecraft:gui/controls/toast.png");

    @FieldRegistry("ascii_page")
    TextureAsset ASCII_PAGE = new TextureAsset("cubecraft:font/unicode_page_00.png");

    @FieldRegistry("text_font")
    FontAsset TEXT_FONT = new FontAsset("cubecraft:/text.ttf");

    @FieldRegistry("icon_font")
    FontAsset ICON_FONT = new FontAsset("cubecraft:/icon.ttf");

    //hud
    @FieldRegistry("action_bar_texture")
    TextureAsset ACTION_BAR = new TextureAsset("cubecraft:gui/container/actionbar.png");

    @FieldRegistry("pointer_texture")
    TextureAsset POINTER = new TextureAsset("cubecraft:gui/icon/pointer.png");


    @FieldRegistry("title_screen")
    UIAsset TITLE_SCREEN = new UIAsset("cubecraft:/title_screen.xml");

    @FieldRegistry("single_player_screen")
    UIAsset SINGLE_PLAYER_SCREEN = new UIAsset("cubecraft:/single_player_screen.xml");

    @FieldRegistry("multi_player_screen")
    UIAsset MULTI_PLAYER_SCREEN = new UIAsset("cubecraft:/multi_player_screen.xml");

    @FieldRegistry("pause_screen")
    UIAsset PAUSE_SCREEN = new UIAsset("cubecraft:/pause_screen.xml");


    @EventHandler
    static void onResourceLoadComplete(ResourceLoadFinishEvent event) {
        if (!Objects.equals(event.getStage(), "default")) {
            return;
        }
        ClientSharedContext.SMOOTH_FONT_RENDERER.setFontFamily(TEXT_FONT.getFont());
        ClientSharedContext.ICON_FONT_RENDERER.setFontFamily(ICON_FONT.getFont());
    }
}