package me.gb2022.commons.container;

import java.util.HashMap;
import me.gb2022.commons.Initializable;
import me.gb2022.commons.event.EventBus;
import me.gb2022.commons.event.SimpleEventBus;
import me.gb2022.commons.registry.ItemRegisterEvent;

public class NameSpaceMap<I> extends HashMap<String, I> {
    private final SimpleEventBus eventBus = new SimpleEventBus();
    private final String split;
    private final I fallback;

    public NameSpaceMap(String split, I fallback) {
        this.split = split;
        this.fallback = fallback;
    }

    public void set(String namespace, String id, I item) {
        if (item instanceof Initializable init) {
            init.init();
        }

        this.set(namespace + this.split + id, item);
    }

    public void set(String all, I item) {
        if (item instanceof Initializable init) {
            init.init();
        }

        this.put(all, item);
        this.eventBus.callEvent(new ItemRegisterEvent(this, all, item));
    }

    public I get(String all) {
        if (all == null) {
            return this.fallback;
        } else {
            try {
                return super.get(all);
            } catch (ArrayIndexOutOfBoundsException var3) {
                return this.fallback;
            }
        }
    }

    public I get(String namespace, String id) {
        if (!this.containsKey(namespace + this.split + id)) {
            throw new RuntimeException("item not found:" + namespace + this.split + id);
        } else {
            return this.get(namespace + this.split + id);
        }
    }

    public SimpleEventBus getEventBus() {
        return this.eventBus;
    }
}

