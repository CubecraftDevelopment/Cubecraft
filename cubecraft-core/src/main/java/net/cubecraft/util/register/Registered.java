package net.cubecraft.util.register;

public final class Registered<I> {
    public static final Registered<?> DUMMY = new Registered<>(null, "_dummy", -1);

    private final I object;
    private final String name;
    private final int allocatedId;

    public Registered(I object, String name, int allocatedId) {
        this.object = object;
        this.name = name;
        this.allocatedId = allocatedId;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return allocatedId;
    }

    public I get() {
        return object;
    }

    public boolean isUnknown(){
        return allocatedId == -1;
    }
}
