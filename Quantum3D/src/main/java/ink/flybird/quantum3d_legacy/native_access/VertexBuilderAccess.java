package ink.flybird.quantum3d_legacy.native_access;

public final class VertexBuilderAccess {
    static native long create(int capacity, int drawMode);

    static native void free(long handle);


    static native void begin(long handle);

    static native void end(long handle);


    static native void vertex(long handle, double x, double y, double z);

    static native void color(long handle, double r, double g, double b, double a);

    static native void tex(long handle, double u, double v);

    static native void normal(long handle, double n, double f, double l);


    static native void uploadPointer(long handle);

    static native void uploadBuffer(long handle, int buffer);

    static native void uploadInterleaved(long handle);
}
