package me.gb2022.quantum3d.render.texture;

public interface Texture {
    void bind();

    void unbind();

    void allocate();

    void delete();

    String getName();
}
