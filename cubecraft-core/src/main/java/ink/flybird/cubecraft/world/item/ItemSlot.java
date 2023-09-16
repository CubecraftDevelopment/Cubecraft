package ink.flybird.cubecraft.world.item;

public class ItemSlot {
    private ItemStack stack;

    public void place(ItemStack stack){
        if(this.stack==null){
            this.stack=stack;
        }else{
            this.stack.merge(stack);
        }
    }

    public void use(){

    }

    public boolean isEmpty() {
        return this.stack==null;
    }

    public ItemStack getStack() {
        return this.stack;
    }

    public void setStack(ItemStack stack) {
        this.stack=stack;
    }
}
