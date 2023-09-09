package ink.flybird.quantum3d.pipeline;

import org.lwjgl.opengl.GL20;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class Shader {
    private ArrayList<String> sources=new ArrayList<>();
    private int glHandle;
    private final ShaderType type;

    protected Shader(ShaderType type) {
        this.type = type;
    }

    public void allocate(){
        this.glHandle= GL20.glCreateShader(this.getType().glID);
    }

    public void addSource(InputStream stream){
        try {
            this.sources.add(new String(stream.readAllBytes()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void uploadSource(){
        GL20.glShaderSource(this.glHandle,this.sources.toArray(new String[0]));
    }

    public void clearSource(){
        this.sources.clear();
    }

    public void compile(){
        GL20.glCompileShader(this.glHandle);
    }

    public void destroy(){
        GL20.glDeleteShader(this.glHandle);
    }

    public ArrayList<String> getSources() {
        return this.sources;
    }

    public int getGlHandle() {
        return this.glHandle;
    }

    public ShaderType getType(){
        return this.type;
    }
}
