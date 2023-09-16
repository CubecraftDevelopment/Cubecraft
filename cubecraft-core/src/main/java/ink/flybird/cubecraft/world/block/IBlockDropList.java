package ink.flybird.cubecraft.world.block;

public interface IBlockDropList {
    String[] dropChanced(BlockState bs);
    String[] dropSilkTouch(BlockState bs);
    String[] dropExplosion(BlockState bs);
    String[] drop(BlockState bs);
}
