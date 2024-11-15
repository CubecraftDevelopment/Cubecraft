package net.cubecraft.client.internal.handler;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import me.gb2022.commons.I18nHelper;
import me.gb2022.quantum3d.texture.Texture2DTileMap;
import me.gb2022.quantum3d.texture.TextureStateManager;
import net.cubecraft.SharedContext;
import net.cubecraft.client.ClientSharedContext;
import net.cubecraft.client.ClientRenderContext;
import net.cubecraft.client.registry.ClientSettingRegistry;
import net.cubecraft.client.registry.ColorMaps;
import net.cubecraft.client.render.Textures;
import net.cubecraft.client.render.chunk.container.ChunkLayerContainers;
import net.cubecraft.client.render.model.block.BlockModel;
import net.cubecraft.client.resource.TextureAsset;
import net.cubecraft.resource.MultiAssetContainer;
import net.cubecraft.resource.ResourceLocation;
import net.cubecraft.resource.ResourcePlugin;
import net.cubecraft.resource.item.IResource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.Locale;
import java.util.Objects;

public interface ClientAssetLoader {
    Logger LOGGER = LogManager.getLogger("ClientAssetLoader");

    static ResourcePlugin blockModelPlugin() {
        return (rm, s) -> {
            SharedContext.GSON_BUILDER.registerTypeAdapter(BlockModel.class, new BlockModel.JDeserializer());
            var container = new MultiAssetContainer<TextureAsset>();

            var resources = new HashSet<IResource>();

            for (var renderer : ClientRenderContext.BLOCK_RENDERERS.values()) {
                if (renderer == null) {
                    continue;
                }

                renderer.getResources(resources);
            }

            rm.loadBlocking(resources, "_block_model", true);

            for (var renderer : ClientRenderContext.BLOCK_RENDERERS.values()) {
                if (renderer == null) {
                    continue;
                }

                renderer.provideTileMapItems(container);
            }

            for (var layer : container.getChannels()) {
                var f = container.getChannel(layer);

                if (f.isEmpty()) {
                    f.add(new TextureAsset("cubecraft", "/block/fallback.png"));
                }

                var a = f.toArray(new TextureAsset[0]);

                rm.loadBlocking(f, "_block_texture", true);

                var texture = Texture2DTileMap.autoGenerate(a, false);

                texture.generateTexture();
                texture.completePlannedLoad(ClientSharedContext.getClient().getClientGUIContext(), 0, 100);
                texture.drawSection();
                texture.upload();

                var texReg = ChunkLayerContainers.REGISTRY.registered(layer);

                switch (layer){
                    case "cubecraft:alpha_block"->{
                        ClientRenderContext.TEXTURE.getTexture2DTileMapContainer().set("cubecraft:terrain", texture);
                        Textures.BLOCK_TEXTURES.register("cubecraft:block_simple", texture);
                        TextureStateManager.setTextureMipMap(texture, true);
                        TextureStateManager.setTextureClamp(texture, true);
                    }
                    case "cubecraft:transparent_block"->{
                        Textures.BLOCK_TEXTURES.register("cubecraft:block_transparent", texture);
                        TextureStateManager.setTextureMipMap(texture, true);
                        TextureStateManager.setTextureClamp(texture, true);
                    }
                    case "cubecraft:alpha_cutout"->{
                        Textures.BLOCK_TEXTURES.register("cubecraft:block_cutout", texture);
                        TextureStateManager.setTextureMipMap(texture, false);
                    }
                }
            }
        };
    }

    static ResourcePlugin languagePlugin() {
        return (rm, stage) -> {
            var language = "auto";
            var i18n = SharedContext.I18N;

            language = ClientSettingRegistry.LANGUAGE.getValue();

            if (Objects.equals(language, "auto")) {
                Locale locale = Locale.getDefault();
                String lang = locale.getLanguage();
                String reg = locale.getCountry();
                language = I18nHelper.covert(locale);
                LOGGER.info("detected user language:{}-{}", lang, reg);
            }

            i18n.setCurrentType(language);

            try {
                for (String s : rm.getNameSpaces()) {
                    JsonArray arr = JsonParser.parseString(rm.getResource(ResourceLocation.language(s, "language_index.json")).getAsText())
                            .getAsJsonArray();
                    for (JsonElement registry : arr) {
                        String type = registry.getAsJsonObject().get("type").getAsString();
                        String author = registry.getAsJsonObject().get("author").getAsString();
                        String display = registry.getAsJsonObject().get("author").getAsString();
                        String file = registry.getAsJsonObject().get("file").getAsString();

                        String data = rm.getResource(ResourceLocation.language(file.split(":")[0], file.split(":")[1])).getAsText();

                        if (!SharedContext.I18N.has(type)) {
                            SharedContext.I18N.createNew(type, display);
                        }
                        SharedContext.I18N.attach(type, data);
                        SharedContext.I18N.addAuthor(type, author);
                    }
                }
            } catch (Exception ex) {
                LOGGER.throwing(ex);
            }
        };
    }

    static ResourcePlugin colorMapPlugin() {
        return (rm, stage) -> {
            for (var id : ColorMaps.REGISTRY.names()) {
                LOGGER.debug("todo:load color map {}", id);
            }
        };
    }


    static void init() {
        var rm = ClientSharedContext.RESOURCE_MANAGER;
        rm.addNameSpace("cubecraft");

        rm.addPlugin("client:default", blockModelPlugin());
        rm.addPlugin("client:default", colorMapPlugin());
        rm.addPlugin("client:default", languagePlugin());
    }
}
