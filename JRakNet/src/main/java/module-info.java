open module jraknet {
    requires fcommon.main;
    requires io.netty.transport;
    requires io.netty.buffer;
    requires io.netty.handler;
    exports com.whirvis.jraknet;
    exports com.whirvis.jraknet.client;
    exports com.whirvis.jraknet.client.peer;
    exports com.whirvis.jraknet.server;
    exports com.whirvis.jraknet.map;
    exports com.whirvis.jraknet.map.concurrent;
    exports com.whirvis.jraknet.identifier;
    exports com.whirvis.jraknet.peer;
    exports com.whirvis.jraknet.protocol;
    exports com.whirvis.jraknet.protocol.connection;
    exports com.whirvis.jraknet.protocol.message;
    exports com.whirvis.jraknet.protocol.message.acknowledge;
    exports com.whirvis.jraknet.protocol.login;
    exports com.whirvis.jraknet.protocol.status;
    exports com.whirvis.jraknet.stream;
}