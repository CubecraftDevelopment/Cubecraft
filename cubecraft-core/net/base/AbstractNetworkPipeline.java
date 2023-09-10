package io.flybird.cubecraft.net.base;

import io.flybird.cubecraft.net.NetWorkEventBus;
import io.flybird.cubecraft.net.INetHandler;
import ink.flybird.fcommon.event.CachedEventBus;
import ink.flybird.fcommon.event.EventBus;


public abstract class AbstractNetworkPipeline {
    protected final NetWorkEventBus packetEventBus =new NetWorkEventBus();
    protected final EventBus eventBus =new SimpleEventBus();

    public void registerNetHandler(INetHandler handler){
        this.packetEventBus.registerEventListener(handler);
    }

    public void unregisterNetHandler(INetHandler handler){
        this.packetEventBus.unregisterEventListener(handler);
    }

    public void registerNetHandler(Object listener){
        this.eventBus.registerEventListener(listener);
    }

    public void unregisterNetHandler(Object listener){
        this.eventBus.unregisterEventListener(listener);
    }

    public NetWorkEventBus getPacketEventBus() {
        return packetEventBus;
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    public abstract void init(int thread);
}
