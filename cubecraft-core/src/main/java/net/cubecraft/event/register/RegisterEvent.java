package net.cubecraft.event.register;

public interface RegisterEvent<I> {
    String getId();

    I getObject();
}
