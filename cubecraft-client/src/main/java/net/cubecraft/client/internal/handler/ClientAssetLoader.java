package net.cubecraft.client.internal.handler;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import ink.flybird.quantum3d_legacy.textures.Texture2DTileMap;
import ink.flybird.quantum3d_legacy.textures.TextureStateManager;
import me.gb2022.commons.I18nHelper;
import net.cubecraft.SharedContext;
import net.cubecraft.client.ClientSettingRegistry;
import net.cubecraft.client.ClientSharedContext;
import net.cubecraft.client.context.ClientRenderContext;
import net.cubecraft.client.registry.ColorMaps;
import net.cubecraft.client.render.Textures;
import net.cubecraft.client.render.model.block.BlockModel;
import net.cubecraft.client.resource.TextureAsset;
import net.cubecraft.event.resource.ResourceReloadEvent;
import net.cubecraft.resource.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.Locale;
import java.util.Objects;

public class ClientAssetLoader {
    public static final Logger LOGGER = LogManager.getLogger("ClientAssetLoader");

    public static ResourceReloadListener blockModelPlugin() {
        return (rm, s) -> {
            SharedContext.GSON_BUILDER.registerTypeAdapter(BlockModel.class, new BlockModel.JDeserializer());
            var container = new MultiAssetContainer<TextureAsset>();

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

                for (TextureAsset r : a) {
                    if (r == null) {
                        continue;
                    }
                    rm.loadResource(r);
                }

                var texture = Texture2DTileMap.autoGenerate(a, false);

                texture.generateTexture();
                texture.completePlannedLoad(ClientSharedContext.getClient().getClientGUIContext(), 0, 100);
                texture.drawSection();
                texture.upload();

                if (Objects.equals(layer, "cubecraft:alpha_block")) {
                    ClientRenderContext.TEXTURE.getTexture2DTileMapContainer().set("cubecraft:terrain", texture);
                    Textures.BLOCK_TEXTURES.register("cubecraft:block_simple", texture);
                    TextureStateManager.setTextureMipMap(texture, true);
                    TextureStateManager.setTextureClamp(texture, true);
                }

                if (Objects.equals(layer, "cubecraft:alpha_cutout")) {
                    Textures.BLOCK_TEXTURES.register("cubecraft:block_cutout", texture);
                    TextureStateManager.setTextureMipMap(texture, false);
                }
            }
        };
    }

    public static ResourceReloadListener languagePlugin() {
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

    public static ResourceReloadListener colorMapPlugin() {
        return (rm, stage) -> {
            for (var id : ColorMaps.REGISTRY.names()) {
                LOGGER.debug("todo:load color map {}", id);
            }
        };
    }


    public static void init() {
        var rm = ClientSharedContext.RESOURCE_MANAGER;

        rm.addPlugin(blockModelPlugin());
        rm.addPlugin(languagePlugin());
        rm.addPlugin(colorMapPlugin());
    }


    @ResourceLoadHandler(stage = ResourceLoadStage.DETECT)
    public void detectResourceLoader(ResourceReloadEvent e) {
        e.resourceManager().addNameSpace("cubecraft");
    }
}
