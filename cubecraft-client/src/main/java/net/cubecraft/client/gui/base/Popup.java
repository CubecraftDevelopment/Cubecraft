package net.cubecraft.client.gui.base;

import net.cubecraft.client.gui.GUIRegistry;
import net.cubecraft.client.gui.font.FontAlignment;
import net.cubecraft.client.registry.TextureRegistry;
import ink.flybird.quantum3d_legacy.GLUtil;
import ink.flybird.quantum3d_legacy.ShapeRenderer;
import ink.flybird.fcommon.math.MathHelper;

public class Popup {
    private int xo;
    private int x;
    public int remaining;

    private final int time;
    private final String title;
    private final String subTitle;

    public final int type;

    public static final int INFO=0;
    public static final int SUCCESS=1;
    public static final int ERROR=2;
    public static final int WARNING=3;


    public Popup(String title, String subTitle, int time, int type) {
        this.title = title;
        this.subTitle = subTitle;
        this.time=time;
        this.remaining=time;
        this.type =type;
    }

    public void render(){
        GLUtil.enableBlend();
        TextureRegistry.TOAST.bind();
        ShapeRenderer.drawRectUV(4,196,4,46,0, 0,1,
                type*42/198f,(type+1)*42/198f
        );

        ShapeRenderer.drawRectUV(8,38,8,38,0,
                type*30/198f,(type+1)*30/198f
                ,168/198f,1
        );
        TextureRegistry.TOAST.unbind();
        GUIRegistry.SMOOTH_FONT_RENDERER.render(title,40,12,0xffffff,12,0, FontAlignment.LEFT);
        GUIRegistry.SMOOTH_FONT_RENDERER.render(subTitle,40,28,0xffffff,8,0, FontAlignment.LEFT);
    }

    public int getTime() {
        return time;
    }

    public void tick() {
        remaining-=1;
        xo=x;
        if(getTime()-remaining<15){
            float i=15-(getTime()-remaining);
            x= (int) (i*i*13);
        }
        if(remaining<15){
            float i=15-remaining;
            x= (int) (i*i*13);
        }
    }

    public int getPos(float t){
        return (int) MathHelper.linearInterpolate(xo,x,t);
    }
}