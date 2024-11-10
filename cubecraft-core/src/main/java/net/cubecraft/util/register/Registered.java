package net.cubecraft.util.register;

public class Registered<I> {
    public static final Registered<?> DUMMY = new Registered<>(null, "_dummy", 0);
    private final String name;
    private final int allocatedId;
    private I object;

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

    protected void _set(I object) {
        this.object = object;
    }

    public boolean isUnknown() {
        return allocatedId == -1;
    }
}
