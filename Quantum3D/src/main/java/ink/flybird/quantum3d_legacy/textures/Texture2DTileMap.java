package ink.flybird.quantum3d_legacy.textures;

import ink.flybird.fcommon.logging.Logger;
import ink.flybird.fcommon.threading.TaskProgressUpdateListener;
import ink.flybird.quantum3d_legacy.BufferAllocation;
import ink.flybird.fcommon.logging.SimpleLogger;
import ink.flybird.fcommon.math.AABB2D;
import ink.flybird.fcommon.math.MathHelper;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class Texture2DTileMap extends Texture2D {
    public static final Logger logger = new SimpleLogger("texture_tilemap");
    public final ArrayList<Section> sections = new ArrayList<>();
    public final int minStepX, minStepY;
    private final HashMap<String, Section> map = new HashMap<>();
    private final ArrayList<ITextureImage> plannedLoad = new ArrayList<>();
    private final int sectionSizeH;
    private final int sectionSizeV;
    private BufferedImage img;
    private int counter = 0;

    public Texture2DTileMap(boolean mipMap, int sectionSizeH, int sectionSizeV, int minStepX, int minStepY) {
        super(false, false);
        this.sectionSizeH = sectionSizeH;
        this.sectionSizeV = sectionSizeV;
        this.minStepX = minStepX;
        this.minStepY = minStepY;
    }

    public static Texture2DTileMap autoGenerate(ITextureImage[] file, boolean mipMap) {
        int maxSizeH = 1;
        int maxSizeV = 1;
        int minStepX = 1000000, minStepY = 1000000;
        int step = 0;
        for (ITextureImage f : file) {
            step++;
            if (f == null) {
                logger.warn("failed to register texture: " + step);
                continue;
            }
            BufferedImage testImg = f.getAsImage();
            if (testImg != null) {
                int i = testImg.getWidth();
                int i2 = testImg.getHeight();
                if (i > maxSizeH) maxSizeH = i;
                if (i2 > maxSizeV) maxSizeV = i2;
                int i3 = testImg.getWidth();
                int i4 = testImg.getHeight();
                if (i3 < minStepX) minStepX = i3;
                if (i4 < minStepY) minStepY = i4;
            }
        }
        Texture2DTileMap map=new Texture2DTileMap(mipMap, maxSizeH, maxSizeV, minStepX, minStepY);
        map.plannedLoad.addAll(List.of(file));
        return map;
    }

    @Override
    public Texture2DTileMap load(ITextureImage image) {
        Image img = new Image(image.getName(), image.getAsImage());
        if (img.image != null) {
            this.tryPutImage(img);
        }
        return this;
    }

    public void tryPutImage(Image image) {
        for (Section section : sections) {
            if (section.getAvailableSpace() > image.getPixels() / (float) this.sectionSizeH / (float) this.sectionSizeV) {
                if (section.tryPutImage(image)) {
                    this.map.put(image.s, section);
                    return;
                }
            }
        }
        Section section = new Section(sectionSizeH, sectionSizeV, this, counter);
        this.sections.add(section);
        section.tryPutImage(image);
        this.map.put(image.s, section);
        counter++;
    }

    public void drawSection() {
        int columnCount = (16384 / sectionSizeH);
        int w = this.sections.size() % columnCount;
        int h = this.sections.size() / columnCount;
        this.width = this.sections.size() > columnCount ? columnCount * sectionSizeH : w * sectionSizeH;
        this.height = (h == 0 ? sectionSizeV : sectionSizeV * h);
        this.img = new BufferedImage(width, height, 2);
        int x = 0, y = 0;
        for (Section s : this.sections) {
            this.img.getGraphics().drawImage(s.image, x * sectionSizeH, y * sectionSizeV, null);
            x++;
            if (x > columnCount) {
                x = 0;
                y++;
            }
        }
    }

    public float exactTextureU(String tex, double u) {
        Section s = this.map.get(tex);
        if (s == null) {
            return 0;
        }
        return (float) s.exactTextureU(tex, u);
    }

    public float exactTextureV(String tex, double v) {
        Section s = this.map.get(tex);
        if (s == null) {
            return 0;
        }
        return (float) s.exactTextureV(tex, v);
    }

    public void register(ITextureImage file) {
        for (ITextureImage img : this.plannedLoad) {
            if (Objects.equals(img.getName(), file.getName())) return;
        }
        plannedLoad.add(file);
    }

    public void completePlannedLoad(TaskProgressUpdateListener l, int start, int end) {
        int i = 0;
        for (ITextureImage s : plannedLoad) {
            this.load(s);
            i++;
            l.onProgressChange((int) MathHelper.scale(i, start, end, 0, plannedLoad.size()));
        }
        plannedLoad.clear();
    }

    public void export(File f) {
        this.drawSection();
        try {
            ImageIO.write(this.img, "png", f);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void upload() {
        this.drawSection();
        ByteBuffer buffer = ImageUtil.getByteFromBufferedImage_RGBA(img);
        GL11.glTexImage2D(this.getBindingType(), 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
        BufferAllocation.free(buffer);
    }

    public static class Section {
        final boolean[][] usage;
        private final BufferedImage image;
        private final int sizeH;
        private final int sizeV;
        private final HashMap<String, AABB2D> tex = new HashMap<>();
        private final Texture2DTileMap parent;
        private final int id;

        public Section(int sizeH, int sizeV, Texture2DTileMap parent, int id) {
            this.sizeH = sizeH;
            this.sizeV = sizeV;
            this.id = id;
            this.image = new BufferedImage(sizeH, sizeV, BufferedImage.TYPE_INT_ARGB);
            this.parent = parent;
            this.usage = new boolean[sizeH][sizeV];
        }

        public boolean tryPutImage(Image image) {
            AABB2D aabb = image.generateBounding();
            for (int i = 0; i < sizeH - image.image.getWidth() + 1; i++) {
                for (int j = 0; j < sizeV - image.image.getHeight() + 1; j++) {
                    AABB2D movedPos = new AABB2D(aabb).move(i, j);
                    boolean anyIntersect = false;
                    for (AABB2D test : tex.values()) {
                        if (test.intersect(movedPos)) {
                            anyIntersect = true;
                        }
                    }
                    if (!anyIntersect) {
                        this.image.getGraphics().drawImage(image.image, i, j, null);
                        this.tex.put(image.s, movedPos);
                        for (int x = i; x < i + image.image.getWidth(); x++) {
                            for (int y = j; y < j + image.image.getHeight(); y++) {
                                this.usage[x][y] = true;
                            }
                        }
                        return true;
                    }
                }
            }
            return false;
        }

        public float getAvailableSpace() {
            int all = this.usage.length * this.usage.length;
            int f = 0;
            for (boolean[] booleans : this.usage) {
                for (int y = 0; y < this.usage.length; y++) {
                    f += booleans[y] ? 1 : 0;
                }
            }
            return 1 - f / (float) all;
        }

        public double exactTextureU(String tex, double u) {
            AABB2D aabb = this.tex.get(tex);
            int columnCount = (16384 / sizeH);
            int off = this.id % columnCount;
            return ((aabb.getX1() - aabb.getX0()) * u + aabb.getX0() + this.sizeH * off) / this.parent.getWidth();
        }

        public double exactTextureV(String tex, double v) {
            AABB2D aabb = this.tex.get(tex);
            int columnCount = (16384 / sizeV);
            int off = this.id / columnCount;
            return ((aabb.getY1() - aabb.getY0()) * v + aabb.getY0() + this.sizeV * off) / this.parent.getHeight();
        }
    }

    public record Image(String s, BufferedImage image) {
        public AABB2D generateBounding() {
            return new AABB2D(0, 0, image.getWidth(), image.getHeight());
        }

        public int getPixels() {
            return image.getWidth() * image.getHeight();
        }
    }
}
