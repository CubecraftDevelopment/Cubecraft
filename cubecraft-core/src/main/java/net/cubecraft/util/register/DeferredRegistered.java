package net.cubecraft.util.register;

public class DeferredRegistered<I> extends Registered<I> {
    private Registered<I> wrapped = null;

    public DeferredRegistered() {
        super(null, "", 0);
    }

    public void inject(Registered<I> wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public boolean isUnknown() {
        return this.wrapped == null || this.wrapped.isUnknown();
    }

    @Override
    public I get() {
        return this.isUnknown() ? null : this.wrapped.get();
    }

    @Override
    public int getId() {
        return this.isUnknown() ? -1 : this.wrapped.getId();
    }

    @Override
    public String getName() {
        return this.isUnknown() ? "unknown" : this.wrapped.getName();
    }
}
