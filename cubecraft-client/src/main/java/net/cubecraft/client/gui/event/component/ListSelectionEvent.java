package net.cubecraft.client.gui.event.component;

import net.cubecraft.client.gui.node.ListView;

public final class ListSelectionEvent <E> extends ComponentEvent<ListView<E>> {
    private final E item;

    public ListSelectionEvent(ListView<E> component, E item) {
        super(component);
        this.item = item;
    }

    public E getItem() {
        return item;
    }
}
