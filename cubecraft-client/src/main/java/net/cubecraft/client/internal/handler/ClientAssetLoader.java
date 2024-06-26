package net.cubecraft.client.internal.handler;

import net.cubecraft.client.ClientSharedContext;
import net.cubecraft.client.context.ClientRenderContext;
import net.cubecraft.event.resource.ResourceReloadEvent;
import net.cubecraft.client.render.model.block.BlockModel;
import net.cubecraft.client.render.model.block.BlockModelComponent;
import net.cubecraft.client.render.block.IBlockRenderer;
import net.cubecraft.client.resource.TextureAsset;
import net.cubecraft.resource.ResourceLoadHandler;
import net.cubecraft.resource.ResourceLoadStage;
import net.cubecraft.resource.ResourceLocation;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import net.cubecraft.EnvironmentPath;
import net.cubecraft.SharedContext;

import ink.flybird.quantum3d_legacy.textures.Texture2DTileMap;
import ink.flybird.quantum3d_legacy.textures.TextureStateManager;
import me.gb2022.commons.I18nHelper;
import me.gb2022.commons.container.NameSpaceMap;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.io.File;
import java.util.*;

public class ClientAssetLoader {
    public final HashSet<TextureAsset> textureList = new HashSet<>();
    final Logger logger = LogManager.getLogger("client_asset_loader");

    @ResourceLoadHandler(stage = ResourceLoadStage.BLOCK_MODEL)
    public void loadBlockModel(ResourceReloadEvent e) {
        SharedContext.GSON_BUILDER.registerTypeAdapter(BlockModel.class, new BlockModel.JDeserializer());
        try {
            for (IBlockRenderer renderer : ((NameSpaceMap<? extends IBlockRenderer>) ClientRenderContext.BLOCK_RENDERER).values()) {
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

            Texture2DTileMap terrain = net.cubecraft.client.context.ClientRenderContext.TEXTURE.getTexture2DTileMapContainer().set(
                    "cubecraft:terrain", Texture2DTileMap.autoGenerate(f, false)
            );

            terrain.generateTexture();
            terrain.completePlannedLoad(ClientSharedContext.getClient().getClientGUIContext(), 0, 100);

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
            Collection<String> s = net.cubecraft.client.context.ClientRenderContext.COLOR_MAP.keySet();
            for (String s2 : s) {
                ClientRenderContext.COLOR_MAP.get(s2).load();
            }
        } catch (Exception ex) {
            this.logger.error(ex);
        }
    }

    @ResourceLoadHandler(stage = ResourceLoadStage.LANGUAGE)
    public void loadLanguage(ResourceReloadEvent e) {
        String f = "auto";//todo:setting
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
