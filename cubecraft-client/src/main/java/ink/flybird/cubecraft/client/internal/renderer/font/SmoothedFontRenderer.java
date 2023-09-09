package ink.flybird.cubecraft.client.internal.renderer.font;

import ink.flybird.cubecraft.client.gui.font.FontAlignment;
import ink.flybird.cubecraft.client.gui.font.IFontRenderer;
import ink.flybird.quantum3d.ShapeRenderer;
import ink.flybird.quantum3d.draw.VertexBuilder;
import ink.flybird.quantum3d.draw.VertexBuilderAllocator;
import ink.flybird.quantum3d.textures.Texture2D;
import ink.flybird.fcommon.container.CollectionUtil;
import ink.flybird.fcommon.math.MathHelper;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class SmoothedFontRenderer implements IFontRenderer {
    private final HashMap<String, AliasFontInfo> aliases = new HashMap<>();
    private final HashMap<String, Integer> lifetime = new HashMap<>();
    private Font fontFamily = new Font("System", Font.PLAIN, 8);

    public void renderShadow(String s, int x, int y, int color, int size, FontAlignment alignment) {
        render(s, (int) (x + 0.125 * size), (int) (y + 0.125 * size), MathHelper.clamp(color - 0xbbbbbb, 0, 0xFFFFFF), size, 0, alignment);
        render(s, x, y, color, size, 0, alignment);
    }

    public void render(String string, int x, int y, int color, int size, double z, FontAlignment alignment) {
        if (string == null || string.length() == 0) {
            return;
        }
        Font font = this.fontFamily.deriveFont((float) size);
        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        FontMetrics fm = img.getGraphics().getFontMetrics(font);
        int width = (int) fm.getStringBounds(string, img.getGraphics()).getWidth();
        int height = (int) fm.getStringBounds(string, img.getGraphics()).getHeight();
        if (width == 0 || height == 0) {
            return;
        }

        String id = "%s_%d".formatted(string, size);
        AliasFontInfo f = this.aliases.get(id);
        if (f == null) {
            f = createFontImage(string, size);
            this.aliases.put(f.genKey(), f);
        }
        this.lifetime.put(f.genKey(), 30);

        int charPos_scr = 1;
        switch (alignment) {
            case LEFT -> charPos_scr = x;
            case MIDDLE -> charPos_scr = (int) (x - f.w / 2f);
            case RIGHT -> charPos_scr = x - f.w;
        }

        f.tex.bind();
        int posFix = (f.h - f.size) / 2;
        VertexBuilder builder = VertexBuilderAllocator.createByPrefer(4);
        builder.begin();
        builder.color(color);
        ShapeRenderer.drawRectUV(builder, charPos_scr, charPos_scr + f.w, y - posFix, y + f.h * 1.05f - posFix, z, 0, 1, 0, 1);
        builder.end();
        builder.uploadPointer();
        builder.free();
    }

    AliasFontInfo createFontImage(String str, int size) {
        size *= 2;
        try {
            Font font = fontFamily.deriveFont((float) size);

            BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
            FontMetrics fm = img.getGraphics().getFontMetrics(font);
            int width = (int) fm.getStringBounds(str, img.getGraphics()).getWidth();
            int height = (int) fm.getStringBounds(str, img.getGraphics()).getHeight();

            img = new BufferedImage(Math.abs(width),Math.abs(height), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = img.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_DEFAULT);
            g.setFont(font);
            g.drawString(str, 0, size);
            Texture2D tex = new Texture2D(false, false);//a dedicated texture in size.
            tex.generateTexture();
            tex.load(img);

            return new AliasFontInfo(str, size / 2, width / 2, height / 2, tex);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void update() {
        List<String> removeList = new ArrayList<>();
        CollectionUtil.iterateMap(this.lifetime, (key, item) -> {
            if (item <= 0) {
                removeList.add(key);
                this.aliases.get(key).tex().destroy();
            } else {
                this.lifetime.put(key, item - 1);
            }
        });
        for (String s : removeList) {
            this.lifetime.remove(s);
            this.aliases.remove(s);
        }
    }

    public int getCacheSize() {
        return this.aliases.size();
    }

    public Font getFontFamily() {
        return fontFamily;
    }

    public void setFontFamily(Font fontFamily) {
        this.fontFamily = fontFamily;
    }

    public record AliasFontInfo(String s, int size, int w, int h, Texture2D tex) {
        String genKey() {
            return "%s_%d".formatted(this.s, this.size);
        }
    }
}