package io.flybird.cubecraft.net;



import io.flybird.cubecraft.net.packet.PacketListener;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;

@Deprecated
public class NetWorkEventBus{
    private final ArrayList<Object> listeners=new ArrayList<>();

    public void callEvent(Object event,NetHandlerContext context) {
        for (Object el : this.listeners) {
            Method[] ms = el.getClass().getMethods();
            for (Method m : ms) {
                if (Arrays.stream(m.getAnnotations()).anyMatch(annotation -> annotation instanceof PacketListener)) {
                    if (m.getParameterCount() == 2 &&
                            m.getParameters()[0].getType() == event.getClass()&&
                            m.getParameters()[1].getType() == NetHandlerContext.class) {
                        try {
                            m.invoke(el, event,context);
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        }
    }

    public void registerEventListener(Object el){
        if(!this.listeners.contains(el)) {
            this.listeners.add(el);
        }
    }

    public void unregisterEventListener(Object el){
        this.listeners.remove(el);
    }

    public ArrayList<Object> getHandlers() {
        return this.listeners;
    }
}
