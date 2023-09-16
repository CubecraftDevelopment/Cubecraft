package ink.flybird.cubecraft.extansion;

import ink.flybird.cubecraft.mod.ClientSideInitializeEvent;
import ink.flybird.cubecraft.mod.ContentInitializeEvent;
import ink.flybird.cubecraft.mod.PostInitializeEvent;
import ink.flybird.cubecraft.mod.ModPreInitializeEvent;
import ink.flybird.cubecraft.mod.ServerSideInitializeEvent;

@Deprecated
public enum ExtensionInitializationOperation {
    PRE_INIT("pre initialization", ModPreInitializeEvent.class),
    CONTENT("content initialization", ContentInitializeEvent.class),
    CLIENT_SIDE("client side initialization", ClientSideInitializeEvent.class),
    SERVER_SIDE("server side initialization", ServerSideInitializeEvent.class),
    POST_INIT("post initialization", PostInitializeEvent.class);

    final String name;
    final Class<?> evt;

    ExtensionInitializationOperation(String name, Class<?> evt) {
        this.name = name;
        this.evt = evt;
    }

    public static ExtensionInitializationOperation[] getClientOperationList() {
        return new ExtensionInitializationOperation[]{PRE_INIT, CLIENT_SIDE, SERVER_SIDE, CONTENT, POST_INIT};
    }

    public static ExtensionInitializationOperation[] getServerOperations() {
        return new ExtensionInitializationOperation[]{PRE_INIT, SERVER_SIDE, CONTENT, POST_INIT};
    }

    public String getName() {
        return name;
    }

    public Object createEvent() {
        try {
            return this.evt.getConstructor().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
