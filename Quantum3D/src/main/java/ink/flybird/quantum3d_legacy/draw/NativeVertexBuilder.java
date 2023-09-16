package ink.flybird.quantum3d_legacy.draw;

public class NativeVertexBuilder extends VertexBuilder{
    protected NativeVertexBuilder(int size, DrawMode drawMode) {
        super(size, drawMode);
    }

    @Override
    public void uploadPointer() {

    }

    @Override
    public void uploadInterLeaved() {

    }

    @Override
    public void uploadBuffer(int buffer) {

    }


    @Override
    public native void vertex(double x,double y,double z);

    @Override
    public native void color(float r,float g,float b,float a);

    @Override
    public native void normal(float n,float f,float l);

    @Override
    public native void tex(float u,float v);
}

