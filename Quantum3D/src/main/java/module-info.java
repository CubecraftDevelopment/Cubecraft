open module quantum3d_legacy {
    requires org.lwjgl;
    requires org.joml;
    requires fcommon.main;
    requires org.lwjgl.opengl;
    requires org.lwjgl.glfw;
    requires org.jetbrains.annotations;
    requires java.desktop;
    exports ink.flybird.quantum3d_legacy;
    exports ink.flybird.quantum3d_legacy.compile;
    exports ink.flybird.quantum3d_legacy.culling;
    exports ink.flybird.quantum3d_legacy.draw;
    exports ink.flybird.quantum3d_legacy.drawcall;
    exports ink.flybird.quantum3d_legacy.memory;
    exports ink.flybird.quantum3d_legacy.textures;
    exports ink.flybird.quantum3d_legacy.platform;
}