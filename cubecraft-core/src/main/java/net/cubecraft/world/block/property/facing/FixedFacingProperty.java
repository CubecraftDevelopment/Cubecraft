package net.cubecraft.world.block.property.facing;

import net.cubecraft.world.block.EnumFacing;
import net.cubecraft.world.block.access.IBlockAccess;

public class FixedFacingProperty extends FacingProperty {
    private final EnumFacing[] aabbs;

    public FixedFacingProperty(EnumFacing... aabbs) {
        this.aabbs = aabbs;
    }

    @Override
    public EnumFacing[] get(IBlockAccess access) {
        return aabbs;
    }
}
