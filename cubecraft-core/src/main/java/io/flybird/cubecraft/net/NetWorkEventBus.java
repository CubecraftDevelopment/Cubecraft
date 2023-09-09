package io.flybird.cubecraft.net;

import java.util.ArrayList;

@Deprecated
public class NetWorkEventBus{
    private final ArrayList<Object> listeners=new ArrayList<>();

    @Deprecated
    public void callEvent(Object event,NetHandlerContext context) {
        throw new RuntimeException("not implemented!");
    }

    public void registerEventListener(Object el){
        //throw new RuntimeException("not implemented!");
    }

    public void unregisterEventListener(Object el){
        throw new RuntimeException("not implemented!");
    }

    public ArrayList<Object> getHandlers() {
        throw new RuntimeException("not implemented!");
    }
}
