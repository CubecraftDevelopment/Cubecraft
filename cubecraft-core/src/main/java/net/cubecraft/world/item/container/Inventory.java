package net.cubecraft.world.item.container;

import ink.flybird.fcommon.math.hitting.Hittable;
import net.cubecraft.world.block.access.IBlockAccess;
import net.cubecraft.world.entity.Entity;
import net.cubecraft.world.item.ItemStack;

import javax.swing.*;
import java.util.Objects;

public abstract class Inventory extends Container {
    public void selectItem(Hittable obj, int targetSlot) {
        String id;
        if (obj instanceof IBlockAccess) {
            id = ((IBlockAccess) obj).getBlockID();
        } else {
            id = ((Entity) obj).getID() + Entity.AUTO_REGISTER_SPAWN_EGG_ID;
        }
        if (get(targetSlot) != null) {
            if (Objects.equals(get(targetSlot).getType(), id)) {
                return;
            }
        }
        int i = 0;
        for (ItemStack stack : this) {
            if (stack != null && Objects.equals(stack.getType(), id)) {
                exchangeSlot(i, targetSlot);
                return;
            }
            i++;
        }
    }

    public ItemStack getActive() {
        return null;
    }
}



