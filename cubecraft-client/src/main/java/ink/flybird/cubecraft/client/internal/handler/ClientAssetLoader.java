package ink.flybird.cubecraft.client.internal.handler;

import ink.flybird.cubecraft.client.ClientRenderContext;
import ink.flybird.cubecraft.client.ClientSharedContext;
import ink.flybird.cubecraft.client.CubecraftClient;
import ink.flybird.cubecraft.event.resource.ResourceReloadEvent;
import ink.flybird.cubecraft.client.render.model.block.BlockModel;
import ink.flybird.cubecraft.client.render.model.block.BlockModelComponent;
import ink.flybird.cubecraft.client.render.model.block.BlockModelFace;
import ink.flybird.cubecraft.client.render.model.block.Cube;
import ink.flybird.cubecraft.client.render.block.IBlockRenderer;
import ink.flybird.cubecraft.client.resource.TextureAsset;
import ink.flybird.cubecraft.resource.ResourceLoadHandler;
import ink.flybird.cubecraft.resource.ResourceLoadStage;
import ink.flybird.cubecraft.resource.ResourceLocation;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import ink.flybird.cubecraft.EnvironmentPath;
import ink.flybird.cubecraft.SharedContext;

import ink.flybird.quantum3d_legacy.textures.Texture2DTileMap;
import ink.flybird.quantum3d_legacy.textures.TextureStateManager;
import ink.flybird.fcommon.I18nHelper;
import ink.flybird.fcommon.container.NameSpaceMap;

import ink.flybird.jflogger.ILogger;
import ink.flybird.jflogger.LogManager;

import java.io.File;
import java.util.*;

public class ClientAssetLoader {
    public final HashSet<TextureAsset> textureList = new HashSet<>();
    final ILogger logger = LogManager.getLogger("client_asset_loader");

    @ResourceLoadHandler(stage = ResourceLoadStage.BLOCK_MODEL)
    public void loadBlockModel(ResourceReloadEvent e) {
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
            this.logger.error(ex);
        }
    }


    @ResourceLoadHandler(stage = ResourceLoadStage.BLOCK_TEXTURE)
    public void loadBlockTexture(ResourceReloadEvent e) {
        try {
            TextureAsset[] f = textureList.toArray(new TextureAsset[0]);

            for (TextureAsset r : f) {
                if(r==null){
                    continue;
                }
                ClientSharedContext.RESOURCE_MANAGER.loadResource(r);
            }

            Texture2DTileMap terrain = ClientRenderContext.TEXTURE.getTexture2DTileMapContainer().set(
                    "cubecraft:terrain", Texture2DTileMap.autoGenerate(f, false)
            );

            terrain.generateTexture();
            terrain.completePlannedLoad(CubecraftClient.CLIENT, 0, 100);

            File f2 = new File(EnvironmentPath.CACHE_FOLDER+"/terrain.png");
            terrain.export(f2);

            terrain.upload();

            TextureStateManager.setTextureMipMap(terrain, true);
            TextureStateManager.setTextureClamp(terrain, true);
        } catch (Exception ex) {
            this.logger.error(ex);
        }
    }

    @ResourceLoadHandler(stage = ResourceLoadStage.COLOR_MAP)
    public void loadColorMap(ResourceReloadEvent e) {
        try {
            Collection<String> s = ClientRenderContext.COLOR_MAP.idList();
            for (String s2 : s) {
                ClientRenderContext.COLOR_MAP.get(s2).load();
            }
        } catch (Exception ex) {
            this.logger.error(ex);
        }
    }

    @ResourceLoadHandler(stage = ResourceLoadStage.LANGUAGE)
    public void loadLanguage(ResourceReloadEvent e) {
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
            this.logger.error(ex);
        }
    }


    @ResourceLoadHandler(stage = ResourceLoadStage.DETECT)
    public void detectResourceLoader(ResourceReloadEvent e) {
        e.resourceManager().addNameSpace("cubecraft");
    }
}
