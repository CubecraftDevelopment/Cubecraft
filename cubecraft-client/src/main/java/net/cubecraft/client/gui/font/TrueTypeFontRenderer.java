package net.cubecraft.client.gui.font;

import me.gb2022.quantum3d.render.ShapeRenderer;
import me.gb2022.quantum3d.render.vertex.DrawMode;
import me.gb2022.quantum3d.render.vertex.VertexBuilder;
import me.gb2022.quantum3d.render.vertex.VertexBuilderUploader;
import me.gb2022.quantum3d.render.vertex.VertexFormat;
import me.gb2022.quantum3d.texture.Texture2D;
import me.gb2022.quantum3d.util.GLUtil;
import net.cubecraft.client.context.ClientGUIContext;
import net.cubecraft.client.registry.ClientSettings;
import net.cubecraft.text.IconFontComponent;
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
        VertexBuilder builder = ClientGUIContext.BUILDER_ALLOCATOR.create(VertexFormat.V3F_C4F_T2F, DrawMode.QUADS, 8);
        builder.allocate();
        return builder;
    }

    @Override
    public void gc() {
        if (this.compiled.size() > 114514) {
            Set<CompiledComponent> components = new HashSet<>(this.compiled.values());
            this.compiled.clear();
            components.forEach(CompiledComponent::destroy);
            components.clear();
        }
    }

    @Override
    public void resize() {
        this.compiled.clear();
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
        TextComponent start = text.getFirst();

        int width = this._width(start, 0);

        var scaleMod = ClientSettings.UISetting.getGUIScaleMod();

        int startX = -1;
        switch (alignment) {
            case LEFT -> startX = x;
            case MIDDLE -> startX = (int) (x - (float) width / 2 * scaleMod);
            case RIGHT -> startX = (int) (x - width * scaleMod);
        }

        _renderComponent(start, startX, y);
    }

    private int _width(TextComponent text, int current) {
        CompiledComponent compiled = this.getCompiled(text);

        current += (int) ((double) compiled.width() / RESOLUTION_SCALE / ClientSettings.UISetting.getGUIScaleMod());

        if (text.getNext() == null) {
            return current;
        }

        return _width(text.getNext(), current);
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

        _renderComponent(
                text.getNext(),
                (int) (x + (double) compiled.width() / RESOLUTION_SCALE * ClientSettings.UISetting.getGUIScaleMod()),
                y
        );
    }

    private static final class CompiledComponent {
        private static final BufferedImage V_HOLDER = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);

        static {

        }

        private final Texture2D texture;

        private CompiledComponent(Texture2D texture) {
            this.texture = texture;
        }

        public CompiledComponent() {
            this(null);
        }

        static CompiledComponent generate(Font fontFamily, TextComponent text) {
            var size = text.getSize();
            var scale = ClientSettings.UISetting.getGUIScaleMod();
            var v_graphics = V_HOLDER.createGraphics();
            var font = createStyledFont(
                    fontFamily.deriveFont((size * RESOLUTION_SCALE * scale)),
                    text.isBold(),
                    text.isItalic(),
                    text.isUnderline(),
                    text.isDelete()
            );

            v_graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            v_graphics.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_DEFAULT);
            v_graphics.setFont(font);

            var fm = v_graphics.getFontMetrics(font);
            var wrap = text.getContent();
            var width = fm.stringWidth(wrap);
            var height = fm.getHeight();

            if (width == 0 || height == 0) {
                return new CompiledComponent();
            }

            var image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            var g = image.createGraphics();

            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_DEFAULT);
            g.setFont(font);
            g.setColor(new Color(text.getColor()));
            g.drawString(wrap, 0, (int) (size * RESOLUTION_SCALE * scale));
            Texture2D tex = new Texture2D(false, false);
            tex.generateTexture();
            tex.load(image);

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