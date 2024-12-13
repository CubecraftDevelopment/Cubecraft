package me.gb2022.quantum3d.render.texture;

import me.gb2022.commons.math.AABB2D;
import me.gb2022.commons.threading.TaskProgressCallback;
import me.gb2022.quantum3d.ITextureImage;
import me.gb2022.quantum3d.texture.ImageUtil;
import me.gb2022.quantum3d.util.BufferAllocation;
import org.lwjgl.opengl.GL11;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class OGLTilemapTexture2D extends OGLTexture2D implements TilemapTexture2D {
    public final ArrayList<Section> sections = new ArrayList<>();
    public final int minStepX, minStepY;
    private final HashMap<String, Section> map = new HashMap<>();
    private final ArrayList<ITextureImage> loadQueue = new ArrayList<>();
    private final int sectionSizeH;
    private final int sectionSizeV;
    private BufferedImage img;
    private int counter = 0;

    public OGLTilemapTexture2D(int sectionSizeH, int sectionSizeV, int minStepX, int minStepY) {
        this.sectionSizeH = sectionSizeH;
        this.sectionSizeV = sectionSizeV;
        this.minStepX = minStepX;
        this.minStepY = minStepY;
    }

    @Override
    public void addTexture(ITextureImage image) {
        if (this.exactTextureU(image.getName(), 0.0f) == -1.0f) {
            return;
        }
        Image img = new Image(image.getName(), image.getImage());
        if (img.image() == null) {
            return;
        }
        for (Section section : sections) {
            if (!(section.getAvailableSpace() > img.getPixels() / (float) this.sectionSizeH / (float) this.sectionSizeV)) {
                continue;
            }
            if (!section.tryPutImage(img)) {
                continue;
            }
            this.map.put(img.name(), section);
            return;
        }
        Section section = new Section(sectionSizeH, sectionSizeV, this, counter);
        this.sections.add(section);
        section.tryPutImage(img);
        this.map.put(img.name(), section);
        this.counter++;
    }

    @Override
    public float exactTextureU(String tex, double u) {
        Section s = this.map.get(tex);
        if (s == null) {
            return -1.0f;
        }
        return (float) s.exactTextureU(tex, u);
    }

    @Override
    public float exactTextureV(String tex, double v) {
        Section s = this.map.get(tex);
        if (s == null) {
            return -1.0f;
        }
        return (float) s.exactTextureV(tex, v);
    }

    @Override
    public void register(ITextureImage file) {
        for (ITextureImage img : this.loadQueue) {
            if (Objects.equals(img.getName(), file.getName())) return;
        }
        loadQueue.add(file);
    }

    @Override
    public void loadWithProgressUpdate(TaskProgressCallback l) {
        int i = 0;
        for (ITextureImage image : loadQueue) {
            this.addTexture(image);
            i++;
            l.onCallback(loadQueue.size(), i);
        }
        loadQueue.clear();
    }

    @Override
    public BufferedImage export() {
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
        return this.img;
    }

    @Override
    public void upload() {
        ByteBuffer buffer = ImageUtil.getByteFromBufferedImage_RGBA(img);
        GL11.glTexImage2D(this.getBindingType(), 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
        BufferAllocation.free(buffer);
    }

    @Override
    public void upload(ITextureImage image) {

    }


    public static class Section {
        final boolean[][] usage;
        private final BufferedImage image;
        private final int sizeH;
        private final int sizeV;
        private final HashMap<String, AABB2D> tex = new HashMap<>();
        private final OGLTilemapTexture2D parent;
        private final int id;

        public Section(int sizeH, int sizeV, OGLTilemapTexture2D parent, int id) {
            this.sizeH = sizeH;
            this.sizeV = sizeV;
            this.id = id;
            this.image = new BufferedImage(sizeH, sizeV, BufferedImage.TYPE_INT_ARGB);
            this.parent = parent;
            this.usage = new boolean[sizeH][sizeV];
        }

        public boolean tryPutImage(Image image) {
            AABB2D aabb = image.generateBounding();
            for (int i = 0; i < sizeH - image.image().getWidth() + 1; i++) {
                for (int j = 0; j < sizeV - image.image().getHeight() + 1; j++) {
                    AABB2D movedPos = new AABB2D(aabb).move(i, j);
                    boolean anyIntersect = false;
                    for (AABB2D test : tex.values()) {
                        if (test.intersect(movedPos)) {
                            anyIntersect = true;
                        }
                    }
                    if (!anyIntersect) {
                        this.image.getGraphics().drawImage(image.image(), i, j, null);
                        this.tex.put(image.name(), movedPos);
                        for (int x = i; x < i + image.image().getWidth(); x++) {
                            for (int y = j; y < j + image.image().getHeight(); y++) {
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

    public record Image(String name, BufferedImage image) {
        public AABB2D generateBounding() {
            return new AABB2D(0, 0, image.getWidth(), image.getHeight());
        }

        public int getPixels() {
            return image.getWidth() * image.getHeight();
        }
    }
}
