package net.cubecraft.net.base;

import ink.flybird.fcommon.event.EventBus;
import ink.flybird.fcommon.event.SimpleEventBus;
import net.cubecraft.net.NetWorkEventBus;

public abstract class AbstractNetworkPipeline {
    protected final NetWorkEventBus packetEventBus =new NetWorkEventBus();
    protected final EventBus eventBus =new SimpleEventBus();

    public NetWorkEventBus getPacketEventBus() {
        return packetEventBus;
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    public abstract void init(int thread);
}
