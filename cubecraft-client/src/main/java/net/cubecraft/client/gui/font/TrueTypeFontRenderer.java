package net.cubecraft.client.gui.font;

import ink.flybird.quantum3d_legacy.GLUtil;
import ink.flybird.quantum3d_legacy.textures.Texture2D;
import me.gb2022.quantum3d.render.ShapeRenderer;
import me.gb2022.quantum3d.render.vertex.DrawMode;
import me.gb2022.quantum3d.render.vertex.VertexBuilder;
import me.gb2022.quantum3d.render.vertex.VertexBuilderUploader;
import me.gb2022.quantum3d.render.vertex.VertexFormat;
import net.cubecraft.client.context.ClientGUIContext;
import net.cubecraft.text.TextComponent;

import java.awt.*;
import java.awt.font.TextAttribute;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class TrueTypeFontRenderer implements FontRenderer {
    public static final int RESOLUTION_SCALE = 2;
    private static final VertexBuilder BUILDER = createSharedBuilder();

    private final HashMap<TextComponent, CompiledComponent> compiled = new HashMap<>();
    private Font fontFamily = new Font("System", Font.PLAIN, 12);

    public static VertexBuilder createSharedBuilder() {
        VertexBuilder builder = ClientGUIContext.BUILDER_ALLOCATOR.allocate(VertexFormat.V3F_C4F_T2F, DrawMode.QUADS, 8);
        builder.allocate();
        return builder;
    }

    public void gc() {
        if (this.compiled.size() > 114514) {
            Set<CompiledComponent> components = new HashSet<>(this.compiled.values());
            this.compiled.clear();
            components.forEach(CompiledComponent::destroy);
            components.clear();
        }
    }

    public Font getFontFamily() {
        return this.fontFamily;
    }

    public void setFontFamily(Font fontFamily) {
        this.fontFamily = fontFamily;
    }

    @Override
    public void render(TextComponent text, int x, int y, double z, FontAlignment alignment) {
        if (text == null || text.isEmpty()) {
            return;
        }

        int width = this._width(text, 0);

        int startX = -1;
        switch (alignment) {
            case LEFT -> startX = x;
            case MIDDLE -> startX = (int) (x - width / 2f);
            case RIGHT -> startX = x - width;
        }

        _renderComponent(text, startX, y);
    }

    private int _width(TextComponent text, int current) {
        CompiledComponent compiled = this.getCompiled(text);

        current += compiled.width() / RESOLUTION_SCALE;

        if (text.getNext() == null) {
            return current;
        }

        return _width(text, current);
    }

    private CompiledComponent getCompiled(TextComponent text) {
        if (!this.compiled.containsKey(text)) {
            this.compiled.put(text, CompiledComponent.generate(this.fontFamily, text));
        }

        return this.compiled.get(text);
    }

    private void _renderComponent(TextComponent text, int x, int y) {
        CompiledComponent compiled = this.getCompiled(text);
        compiled.render(x, y);

        if (text.getNext() == null) {
            return;
        }

        _renderComponent(text.getNext(), x + compiled.width(), y);
    }


    private static final class CompiledComponent {
        private final Texture2D texture;

        private CompiledComponent(Texture2D texture) {
            this.texture = texture;
        }

        public CompiledComponent() {
            this(null);
        }

        static CompiledComponent generate(Font fontFamily, TextComponent text) {
            float size = text.getSize();

            Font font = fontFamily.deriveFont(size);

            BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
            FontMetrics fm = img.getGraphics().getFontMetrics(font);

            String wrap = text.getText();

            int width = (int) fm.getStringBounds(wrap, img.getGraphics()).getWidth();
            int height = (int) fm.getStringBounds(wrap, img.getGraphics()).getHeight();

            if (width == 0 || height == 0) {
                return new CompiledComponent();
            }

            img = new BufferedImage(Math.abs(width) * RESOLUTION_SCALE, Math.abs(height) * RESOLUTION_SCALE, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = img.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_DEFAULT);
            g.setFont(createStyledFont(font.deriveFont(size * RESOLUTION_SCALE), text.isBold(), text.isItalic(), text.isUnderline(), text.isDelete()));
            g.setColor(new Color(text.getColor()));
            g.drawString(wrap, 0, size * RESOLUTION_SCALE);
            Texture2D tex = new Texture2D(false, false);
            tex.generateTexture();
            tex.load(img);

            return new CompiledComponent(tex);
        }

        public static Font createStyledFont(Font baseFont, boolean bold, boolean italic, boolean underline, boolean strikethrough) {
            Map<TextAttribute, Object> attributes = new HashMap<>(baseFont.getAttributes());

            // 设置粗体和斜体
            int style = Font.PLAIN;
            if (bold) {
                style |= Font.BOLD;
            }
            if (italic) {
                style |= Font.ITALIC;
            }
            baseFont = baseFont.deriveFont(style);

            // 设置下划线
            if (underline) {
                attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
            }

            // 设置删除线
            if (strikethrough) {
                attributes.put(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
            }

            // 创建并返回新的字体
            return baseFont.deriveFont(attributes);
        }

        private void destroy() {
            if (this.texture == null) {
                return;
            }
            this.texture.destroy();
        }

        public int width() {
            return this.texture == null ? 0 : this.texture.getWidth();
        }

        public int height() {
            return this.texture == null ? 0 : this.texture.getHeight();
        }

        private void render(float baseX, float baseY) {
            if (this.texture == null) {
                return;
            }

            BUILDER.reset();
            BUILDER.setColor(1, 1, 1, 1f);

            float x1 = baseX + (float) this.texture.getWidth() / RESOLUTION_SCALE;
            float y1 = baseY + (float) this.texture.getHeight() / RESOLUTION_SCALE;

            ShapeRenderer.drawRectUV(BUILDER, baseX, x1, baseY, y1, 0, 0, 1, 0, 1);

            this.texture.bind();
            GLUtil.enableBlend();
            VertexBuilderUploader.uploadPointer(BUILDER);
            this.texture.unbind();
        }
    }
}