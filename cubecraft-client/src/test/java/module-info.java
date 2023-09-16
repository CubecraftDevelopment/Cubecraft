module ink.flybird.cubecraft.client{
    requires java.management;
    requires java.naming;
    requires java.logging;
    requires java.xml;
    requires java.desktop;
    requires jdk.sctp;
    requires org.joml;
    requires jdk.unsupported;
    requires fcommon.main;
    requires quantum3d.lwjgl;
    requires quantum3d.std;
    requires cubecraft.core;
    requires com.google.gson;
    requires org.lwjgl.glfw;
    requires quantum3d_legacy;
    requires org.lwjgl.opengl;
    requires jraknet;
    requires io.netty.buffer;
    requires io.netty.transport;
    requires cubecraft.server;

    exports ink.flybird.cubecraft.client.gui to fcommon.main;
    exports ink.flybird.cubecraft.client.world to fcommon.main;
    exports ink.flybird.cubecraft.client.internal to fcommon.main;

    exports ink.flybird.cubecraft.client.internal.registry to cubecraft.core;
}