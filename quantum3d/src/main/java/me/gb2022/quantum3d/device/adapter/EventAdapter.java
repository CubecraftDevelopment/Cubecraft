package me.gb2022.quantum3d.device.adapter;

import me.gb2022.commons.event.EventBus;

/**
 * The base abstract class for event adapters that provide a bridge between specific event systems and the common EventBus.
 */
public abstract class EventAdapter {

    private final EventBus eventBus;

    /**
     * Constructs an EventAdapter with the specified EventBus.
     *
     * @param eventBus The EventBus instance to be used for event communication.
     */
    public EventAdapter(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    /**
     * Get the EventBus instance associated with this EventAdapter.
     *
     * @return The EventBus instance used by this EventAdapter.
     */
    public EventBus getEventBus() {
        return eventBus;
    }
}