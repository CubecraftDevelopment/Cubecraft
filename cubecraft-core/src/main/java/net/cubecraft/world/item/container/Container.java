package net.cubecraft.world.item.container;

import me.gb2022.commons.nbt.NBTTagCompound;
import net.cubecraft.CoreRegistries;
import net.cubecraft.internal.item.ItemRegistry;
import net.cubecraft.world.item.Item;
import net.cubecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Iterator;

public abstract class Container implements Iterable<ItemStack> {
    protected final ItemStack[] stacks;

    public Container() {
        int nSlots = this.getCapacity();
        this.stacks = new ItemStack[nSlots];
    }

    public Container(NBTTagCompound tag) {
        this();
        this.setData(tag);
    }

    public static Item getItem(ItemStack stack) {
        if (stack == null) {
            return ItemRegistry.DUMMY;
        }
        return CoreRegistries.ITEMS.object(stack.getType());
    }

    protected abstract int getCapacity();

    public void exchangeSlot(int from, int dest) {
        ItemStack i = this.stacks[from];
        this.stacks[from] = this.stacks[dest];
        this.stacks[dest] = i;
    }

    public NBTTagCompound serialize() {
        NBTTagCompound tag = new NBTTagCompound();
        for (int i = 0; i < this.stacks.length; i++) {
            ItemStack stack = this.stacks[i];
            if (stack == null) {
                continue;
            }
            tag.setCompoundTag(String.valueOf(i), stack.serialize());
        }
        return tag;
    }

    public void setData(NBTTagCompound tag) {
        this.clear();
        for (int i = 0; i < this.stacks.length; i++) {
            if (!tag.hasKey(String.valueOf(i))) {
                continue;
            }
            this.stacks[i] = new ItemStack(tag.getCompoundTag(String.valueOf(i)));
        }
    }

    public void clear() {
        Arrays.fill(this.stacks, null);
    }

    public ItemStack get(int slot) {
        return this.stacks[slot];
    }

    public void set(int slot, ItemStack stack) {
        this.stacks[slot] = stack;
    }

    public ItemStack[] getContent() {
        return this.stacks;
    }

    @NotNull
    @Override
    public Iterator<ItemStack> iterator() {
        return new ContainerIterator(this);
    }

    public static class ContainerIterator implements Iterator<ItemStack> {
        private final Container container;
        int i = 0;

        public ContainerIterator(Container container) {
            this.container = container;
        }

        @Override
        public boolean hasNext() {
            return this.i < container.getCapacity();
        }

        @Override
        public ItemStack next() {
            return container.get(this.i++);
        }
    }
}
