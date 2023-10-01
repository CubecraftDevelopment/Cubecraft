package ink.flybird.cubecraft.client.internal.handler;

import ink.flybird.cubecraft.client.ClientRenderContext;
import ink.flybird.cubecraft.client.ClientSharedContext;
import ink.flybird.cubecraft.client.CubecraftClient;
import ink.flybird.cubecraft.client.event.ClientResourceReloadEvent;
import ink.flybird.cubecraft.client.render.model.block.BlockModel;
import ink.flybird.cubecraft.client.render.model.block.BlockModelComponent;
import ink.flybird.cubecraft.client.render.model.block.BlockModelFace;
import ink.flybird.cubecraft.client.render.model.block.Cube;
import ink.flybird.cubecraft.client.render.block.IBlockRenderer;
import ink.flybird.cubecraft.client.resources.item.ImageResource;
import ink.flybird.cubecraft.client.resources.ResourceLoadHandler;
import ink.flybird.cubecraft.client.resources.ResourceLoadStage;
import ink.flybird.cubecraft.client.resources.ResourceLocation;
import ink.flybird.cubecraft.client.internal.renderer.font.LegacyFontRenderer;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import ink.flybird.cubecraft.EnvironmentPath;
import ink.flybird.cubecraft.SharedContext;
import ink.flybird.quantum3d_legacy.textures.Texture2D;
import ink.flybird.quantum3d_legacy.textures.Texture2DTileMap;
import ink.flybird.quantum3d_legacy.textures.TextureStateManager;
import ink.flybird.fcommon.I18nHelper;
import ink.flybird.fcommon.container.NameSpaceMap;

import ink.flybird.fcommon.logging.Logger;
import ink.flybird.fcommon.logging.SimpleLogger;


import java.awt.*;
import java.io.File;
import java.util.*;

public class ResourceLoader {
    public final HashSet<ImageResource> textureList = new HashSet<>();
    final Logger logger = new SimpleLogger("ResourceLoader");

    @ResourceLoadHandler(stage = ResourceLoadStage.BLOCK_MODEL)
    public void loadBlockModel(ClientResourceReloadEvent e) {
        SharedContext.GSON_BUILDER.registerTypeAdapter(BlockModel.class, new BlockModel.JDeserializer());
        SharedContext.GSON_BUILDER.registerTypeAdapter(BlockModelFace.class, new BlockModelFace.JDeserializer());
        SharedContext.GSON_BUILDER.registerTypeAdapter(BlockModelComponent.class, new Cube.JDeserializer());

        try {
            for (IBlockRenderer renderer : ((NameSpaceMap<? extends IBlockRenderer>) ClientRenderContext.BLOCK_RENDERER).itemList()) {
                if (renderer != null) {
                    renderer.initializeRenderer(textureList);
                }
            }
        } catch (Exception ex) {
            this.logger.exception(ex);
        }
    }

    @ResourceLoadHandler(stage = ResourceLoadStage.BLOCK_TEXTURE)
    public void loadBlockTexture(ClientResourceReloadEvent e) {
        try {
            ImageResource[] f = textureList.toArray(new ImageResource[0]);

            for (ImageResource r : f) {
                if(r==null){
                    continue;
                }
                ClientSharedContext.RESOURCE_MANAGER.loadResource(r);
            }

            Texture2DTileMap terrain = ClientRenderContext.TEXTURE.getTexture2DTileMapContainer().set("cubecraft:terrain", Texture2DTileMap.autoGenerate(f, false));

            terrain.generateTexture();
            terrain.completePlannedLoad(e.client(), 0, 100);

            File f2 = new File(EnvironmentPath.CACHE_FOLDER+"/terrain.png");
            terrain.export(f2);

            terrain.upload();


            /*
            if (ClientMain.getStartGameArguments().getValueAsBoolean("debug", true)) {


            }

             */

            TextureStateManager.setTextureMipMap(terrain, true);
            TextureStateManager.setTextureClamp(terrain, true);
        } catch (Exception ex) {
            this.logger.exception(ex);
        }
    }

    @ResourceLoadHandler(stage = ResourceLoadStage.COLOR_MAP)
    public void loadColorMap(ClientResourceReloadEvent e) {
        try {
            Collection<String> s = ClientRenderContext.COLOR_MAP.idList();
            for (String s2 : s) {
                ClientRenderContext.COLOR_MAP.get(s2).load();
            }
        } catch (Exception ex) {
            this.logger.exception(ex);
        }
    }

    @ResourceLoadHandler(stage = ResourceLoadStage.LANGUAGE)
    public void loadLanguage(ClientResourceReloadEvent e) {
        String f = CubecraftClient.CLIENT.getGameSetting().getValue("client.language", "zh-cn");
        if (Objects.equals(f, "auto")) {
            Locale locale = Locale.getDefault();
            String lang = locale.getLanguage();
            String reg = locale.getCountry();
            f = I18nHelper.covert(locale);
            logger.info("detected user language:%s-%s".formatted(lang, reg));
        }
        SharedContext.I18N.setCurrentType(f);
        try {
            for (String s : e.resourceManager().getNameSpaces()) {
                JsonArray arr = JsonParser.parseString(e.resourceManager().getResource(ResourceLocation.language(s, "language_index.json")).getAsText()).getAsJsonArray();
                for (JsonElement registry : arr) {
                    String type = registry.getAsJsonObject().get("type").getAsString();
                    String author = registry.getAsJsonObject().get("author").getAsString();
                    String display = registry.getAsJsonObject().get("author").getAsString();
                    String file = registry.getAsJsonObject().get("file").getAsString();

                    String data = e.resourceManager().getResource(ResourceLocation.language(file.split(":")[0], file.split(":")[1])).getAsText();

                    if (!SharedContext.I18N.has(type)) {
                        SharedContext.I18N.createNew(type, display);
                    }
                    SharedContext.I18N.attach(type, data);
                    SharedContext.I18N.addAuthor(type, author);
                }
            }
        } catch (Exception ex) {
            this.logger.exception(ex);
        }
    }

    @ResourceLoadHandler(stage = ResourceLoadStage.FONT_TEXTURE)
    public void loadFontTexture(ClientResourceReloadEvent e) {
        ClientSharedContext.ICON_FONT_RENDERER.setFontFamily(e.resourceManager().getResource(ResourceLocation.font("cubecraft", "SegMDL2.ttf")).getAsFont().deriveFont(Font.BOLD));
        ClientSharedContext.SMOOTH_FONT_RENDERER.setFontFamily(e.resourceManager().getResource(ResourceLocation.font("cubecraft", "OPPOSans.ttf")).getAsFont().deriveFont(Font.PLAIN));

        if (CubecraftClient.CLIENT.getGuiManager().isLegacyFontRequested()) {
            try {
                long last = System.currentTimeMillis();
                for (int i = 0; i < 256; i++) {
                    if (i >= 241 && i <= 248 || i >= 216 && i <= 239 || i == 8 || i == 0xf0) {
                        continue;
                    }
                    String s2 = Integer.toHexString(i);
                    if (s2.length() == 1) {
                        s2 = "0" + Integer.toHexString(i);
                    }
                    LegacyFontRenderer.textures[i] = new Texture2D(false, true);
                    LegacyFontRenderer.textures[i].generateTexture();
                    LegacyFontRenderer.textures[i].load(ClientSharedContext.RESOURCE_MANAGER.getResource("/resource/cubecraft/texture/font/unicode_page_%s.png".formatted(s2)));
                    TextureStateManager.setTextureMipMap(LegacyFontRenderer.textures[i], true);
                    if (System.currentTimeMillis() - last > 16) {
                        System.gc();
                        last = System.currentTimeMillis();
                        e.client().onProgressChange((int) ((float) i / 256 * 100));
                        e.client().onProgressStageChanged("loadingFontTexture:/resource/texture/font/unicode_page_%s.png(%d/256)".formatted(s2, i));
                        e.client().refreshScreen();
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                this.logger.exception(ex);
            }
        }
    }

    @ResourceLoadHandler(stage = ResourceLoadStage.UI_CONTROLLER)
    public void loadUIController(ClientResourceReloadEvent e) {
        CubecraftClient.CLIENT.getGuiManager().initialize();
    }

    @ResourceLoadHandler(stage = ResourceLoadStage.DETECT)
    public void detectResourceLoader(ClientResourceReloadEvent e) {
        e.resourceManager().addNameSpace("cubecraft");
    }
}
