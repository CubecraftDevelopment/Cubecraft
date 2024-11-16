function onRandomTick(world, block, x, y, z) {
    world.getBlockAccess(x, y, z).setID("cubecraft:stone")
}