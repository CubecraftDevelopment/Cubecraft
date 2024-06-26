package net.cubecraft.world.item;

import me.gb2022.commons.math.MathHelper;
import me.gb2022.commons.nbt.NBTTagCompound;
import net.cubecraft.ContentRegistries;
import net.cubecraft.world.item.property.IntegerProperty;

import java.util.Objects;

public final class ItemStack {
    private String type;
    private int amount;
    private NBTTagCompound nbt;

    public ItemStack(String id, int amount, NBTTagCompound tag) {
        this.setType(id);
        this.setAmount(amount);
        this.setNbt(tag);
    }

    public ItemStack(String id, int amount) {
        this(id, amount, null);
    }

    public ItemStack(NBTTagCompound tag) {
        this.nbt = tag.getCompoundTag("nbt");
        this.amount = tag.getInteger("count");
        this.type = tag.getString("type");
    }

    public void merge(ItemStack another) {
        if (!this.isSameItem(another)) {
            return;
        }

        Item item = ContentRegistries.ITEM.get(this.type);
        int i = MathHelper.clamp(another.getAmount(), 0,
                item.getProperty("cubecraft:max_stack_count", IntegerProperty.class).get(this));
        another.amount -= (64 - i);
        this.amount += 64 - i;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public NBTTagCompound getNbt() {
        return nbt;
    }

    public void setNbt(NBTTagCompound nbt) {
        this.nbt = nbt;
    }

    public NBTTagCompound serialize() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("type", this.type);
        tag.setInteger("count", this.amount);
        tag.setCompoundTag("nbt", this.nbt);
        return tag;
    }

    public boolean isSameItem(ItemStack stack) {
        if (stack == null) {
            return false;
        }
        if (!Objects.equals(stack.getType(), this.getType())) {
            return false;
        }
        //todo:compare NBT
        return stack.getNbt() == null && this.nbt == null;
    }
}
