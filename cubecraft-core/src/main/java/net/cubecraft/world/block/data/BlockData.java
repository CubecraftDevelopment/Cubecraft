package net.cubecraft.world.block.data;

public interface BlockData<I> {
    byte set(byte meta, I value);

    I get(byte meta);
}
