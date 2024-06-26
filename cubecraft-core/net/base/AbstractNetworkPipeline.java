package ink.flybird.cubecraft.net.base;

import ink.flybird.cubecraft.net.NetWorkEventBus;
import ink.flybird.cubecraft.net.INetHandler;
import me.gb2022.commons.event.CachedEventBus;
import me.gb2022.commons.event.EventBus;


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
