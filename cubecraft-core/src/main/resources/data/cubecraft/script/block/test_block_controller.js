void onRandomTick(IWorld world,Block block,var x,var y,var z){
    world.getBlockAccess(x,y,z).setID("cubecraft:stone")
}