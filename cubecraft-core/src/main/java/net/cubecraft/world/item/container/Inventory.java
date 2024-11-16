package net.cubecraft.world.item.container;

import me.gb2022.commons.math.hitting.Hittable;
import net.cubecraft.world.block.access.IBlockAccess;
import net.cubecraft.world.entity.Entity;
import net.cubecraft.world.item.ItemStack;

import java.util.Objects;

public abstract class Inventory extends Container {
    int activeSlot = 0;

    public void selectItem(Hittable obj, int targetSlot) {
        String id;
        if (obj instanceof IBlockAccess b) {
            id = b.getBlock().getID();
        } else {
            id = ((Entity) obj).getID() + Entity.AUTO_REGISTER_SPAWN_EGG_ID;
        }
        if (get(targetSlot) != null) {
            if (Objects.equals(get(targetSlot).getType(), id)) {
                return;
            }
        }
        int i = 0;

        //todo creative
        stacks[targetSlot]=new ItemStack(id,64);

        for (ItemStack stack : this) {
            if (stack != null && Objects.equals(stack.getType(), id)) {
                exchangeSlot(i, targetSlot);
                return;
            }
            i++;
        }
    }

    public ItemStack getActive() {
        return this.get(this.activeSlot);
    }

    public int getActiveSlotId() {
        return this.activeSlot;
    }

    public void setActiveSlot(int activeSlot) {
        this.activeSlot = activeSlot;
    }
}



