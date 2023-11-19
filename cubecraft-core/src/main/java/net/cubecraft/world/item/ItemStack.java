package net.cubecraft.world.item;

import ink.flybird.fcommon.math.MathHelper;
import ink.flybird.fcommon.nbt.NBTDataIO;
import ink.flybird.fcommon.nbt.NBTTagCompound;
import net.cubecraft.ContentRegistries;

import java.util.Objects;

public class ItemStack implements NBTDataIO {
    private String type;
    private int counts;
    private NBTTagCompound nbt;

    public void merge(ItemStack another) {
        Item item = ContentRegistries.ITEM.get(this.type);
        if (Objects.equals(another.getType(), this.type) && another.nbt != null && this.nbt != null) {
            //物品id一致，且双方均无nbt，且该物品可堆叠
            int i = MathHelper.clamp(another.getCounts(), 0, Integer.MAX_VALUE);
            another.counts -= (64 - i);
            this.counts += 64 - i;
        }
    }

    public int getCounts() {
        return counts;
    }

    public void setCounts(int counts) {
        this.counts = counts;
    }

    public String getType() {
        return type;
    }

    public NBTTagCompound getNbt() {
        return nbt;
    }

    @Override
    public NBTTagCompound getData() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("type", this.type);
        tag.setInteger("count", this.counts);
        tag.setCompoundTag("nbt", this.nbt);
        return tag;
    }

    @Override
    public void setData(NBTTagCompound tag) {
        this.nbt = tag.getCompoundTag("nbt");
        this.counts = tag.getInteger("count");
        this.type = tag.getString("type");
    }
}
