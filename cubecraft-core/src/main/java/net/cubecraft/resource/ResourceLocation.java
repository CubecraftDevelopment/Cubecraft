package net.cubecraft.resource;

public record ResourceLocation(ResourceType type, String namespace, ResourceLocType folder, String relativePos) {

    //block
    public static ResourceLocation blockTexture(String namespace, String relativePos) {
        return new ResourceLocation(ResourceType.RESOURCE, namespace, ResourceLocType.BLOCK_TEXTURE, relativePos);
    }

    public static ResourceLocation language(String namespace, String rel) {
        return new ResourceLocation(ResourceType.RESOURCE, namespace, ResourceLocType.TEXT, rel);
    }

    public static ResourceLocation blockColorMap(String all) {
        return new ResourceLocation(ResourceType.RESOURCE, all.split(":")[0], ResourceLocType.BLOCK_COLOR_MAP, all.split(":")[1]);
    }

    public static ResourceLocation blockTexture(String all) {
        return blockTexture(all.split(":")[0], all.split(":")[1]);
    }

    public static ResourceLocation blockModel(String all) {
        return new ResourceLocation(ResourceType.RESOURCE, all.split(":")[0], ResourceLocType.BLOCK_MODEL, all.split(":")[1]);
    }

    //ui
    public static ResourceLocation uiScreen(String location) {
        String namespace = location.split(":")[0];
        String relativePos = location.split(":")[1];
        return new ResourceLocation(ResourceType.RESOURCE, namespace, ResourceLocType.UI_SCREEN, relativePos);
    }

    public static ResourceLocation uiRenderController(String namespace, String relativePos) {
        return new ResourceLocation(ResourceType.RESOURCE, namespace, ResourceLocType.UI_RENDER_CONTROLLER, relativePos);
    }

    public static ResourceLocation uiTexture(String namespace, String relativePos) {
        return new ResourceLocation(ResourceType.RESOURCE, namespace, ResourceLocType.UI_TEXTURES, relativePos);
    }

    public static ResourceLocation font(String namespace, String rel) {
        return new ResourceLocation(ResourceType.RESOURCE, namespace, ResourceLocType.FONT, rel);
    }

    public static ResourceLocation worldRendererSetting(String all) {
        return new ResourceLocation(ResourceType.RESOURCE, all.split(":")[0], ResourceLocType.WORLD_RENDERER, all.split(":")[1]);
    }

    public String format() {
        return type.getName() + namespace + folder.getName() + relativePos;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ResourceLocation r) {
            return r.format().equals(this.format());
        } else {
            return false;
        }
    }

    enum ResourceLocType {
        BLOCK_TEXTURE("/texture/block/"),
        BLOCK_MODEL("/model/block/"),
        ENTITY_TEXTURE("/texture/entity/"),
        ENTITY_MODEL("/model/entity/"),
        UI_RENDER_CONTROLLER("/model/ui/"),
        UI_TEXTURES("/texture/gui/"),
        UI_SCREEN("/ui/"),
        BLOCK_COLOR_MAP("/misc/colormap/"),

        BLOCK_DATA("/block/"),
        ENTITY_DATA("/entity/"),
        EMPTY("/"),
        TEXT("/text/"),
        FONT("/font/"),
        WORLD_RENDERER("/model/world/");

        final String name;

        ResourceLocType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }


    enum ResourceType {
        RESOURCE("/asset/"),
        DATA("/data/");

        final String name;

        ResourceType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}

