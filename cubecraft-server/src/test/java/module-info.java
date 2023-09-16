open module cubecraft.server {
    requires fcommon.main;
    requires cubecraft.core;
    requires com.google.gson;
    requires leveldb.api;
    requires jraknet;
    requires io.netty.buffer;
    exports ink.flybird.cubecraft.server;
    exports ink.flybird.cubecraft.server.event;
    exports ink.flybird.cubecraft.server.event.join;
    exports ink.flybird.cubecraft.server.internal;
    exports ink.flybird.cubecraft.server.internal.network;
    exports ink.flybird.cubecraft.server.internal.registries;
    exports ink.flybird.cubecraft.server.internal.service;
    exports ink.flybird.cubecraft.server.internal.thread;
    exports ink.flybird.cubecraft.server.net;
    exports ink.flybird.cubecraft.server.service;
    exports ink.flybird.cubecraft.server.world;
}