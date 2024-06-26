package net.cubecraft.client.gui.node;


import net.cubecraft.client.ClientSharedContext;
import net.cubecraft.client.resource.TextureAsset;
import me.gb2022.commons.registry.TypeItem;
import ink.flybird.quantum3d_legacy.ShapeRenderer;
import ink.flybird.quantum3d_legacy.textures.Texture2D;
import org.w3c.dom.Element;

@TypeItem("image")
public class Image extends Component {
    private final Texture2D texture = new Texture2D(false, false);
    public HorizontalClipping hClip;
    public VerticalClipping vClip;

    @Override
    public void init(Element element) {
        this.hClip = HorizontalClipping.from(element.getAttribute("h-clip"));
        this.vClip = VerticalClipping.from(element.getAttribute("v-clip"));
        String file = element.getTextContent();
        this.texture.generateTexture();

        TextureAsset resource = new TextureAsset(file.trim().split(":")[0], file.trim().split(":")[1]);

        ClientSharedContext.RESOURCE_MANAGER.loadResource(resource);

        this.texture.load(resource);
        this.texture.bind();
        super.init(element);
    }

    @Override
    public void render(float interpolationTime) {
        this.texture.bind();
        float u0 = 0, u1 = 0, v0 = 0, v1 = 0;
        switch (this.vClip) {
            case UP -> {
                v0 = 0;
                v1 = this.getLayout().getAbsoluteHeight() * 1.0f / this.texture.getHeight();
            }
            case MIDDLE -> {
                v0 = (this.texture.getHeight() / 2.0f - this.getLayout().getAbsoluteHeight() / 2.0f) / this.texture.getHeight();
                v1 = (this.texture.getHeight() / 2.0f + this.getLayout().getAbsoluteHeight() / 2.0f) / this.texture.getHeight();
            }
            case DOWN -> {
                v0 = (this.texture.getHeight() / 2.0f - this.getLayout().getAbsoluteHeight() / 2.0f) / this.texture.getHeight();
                v1 = 1;
            }
            case SCALE -> {
                v0 = 0;
                v1 = 1;
            }
        }
        switch (this.hClip) {
            case LEFT -> {
                u0 = 0;
                u1 = (this.getLayout().getAbsoluteWidth() * 1.0f / this.texture.getWidth()) / this.texture.getWidth();
            }
            case MIDDLE -> {
                u0 = (this.texture.getWidth() / 2.0f - this.getLayout().getAbsoluteWidth() / 2.0f) / this.texture.getWidth();
                u1 = (this.texture.getWidth() / 2.0f + this.getLayout().getAbsoluteWidth() / 2.0f) / this.texture.getWidth();
            }
            case RIGHT -> {
                u0 = (this.texture.getWidth() / 2.0f - this.getLayout().getAbsoluteWidth() / 2.0f) / this.texture.getWidth();
                u1 = 1;
            }
            case SCALE -> {
                u0 = 0;
                u1 = 1;
            }
        }
        ShapeRenderer.setColor(0xFFFFFF);
        ShapeRenderer.begin();

        ShapeRenderer.drawRectUV(getLayout().getAbsoluteX(),
                getLayout().getAbsoluteX() + getLayout().getAbsoluteWidth(),
                getLayout().getAbsoluteY(),
                getLayout().getAbsoluteY() + getLayout().getAbsoluteHeight(),
                0, u0, u1, v0, v1);
        ShapeRenderer.end();
    }

    public enum VerticalClipping {
        UP,
        MIDDLE,
        DOWN,
        SCALE;

        public static VerticalClipping from(String attribute) {
            return switch (attribute) {
                case "up" -> UP;
                case "middle" -> MIDDLE;
                case "down" -> DOWN;
                case "scale" -> SCALE;
                default -> throw new IllegalArgumentException("no matched constant named %s".formatted(attribute));
            };
        }
    }

    public enum HorizontalClipping {
        LEFT,
        MIDDLE,
        RIGHT,
        SCALE;

        public static HorizontalClipping from(String attribute) {
            return switch (attribute) {
                case "left" -> LEFT;
                case "middle" -> MIDDLE;
                case "right" -> RIGHT;
                case "scale" -> SCALE;
                default -> throw new IllegalArgumentException("no matched constant named %s".formatted(attribute));
            };
        }

    }
}
