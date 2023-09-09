package ink.flybird.cubecraft.client.gui;

import ink.flybird.cubecraft.client.CubecraftClient;
import ink.flybird.cubecraft.client.gui.base.DisplayScreenInfo;
import ink.flybird.cubecraft.client.gui.base.Popup;
import ink.flybird.cubecraft.client.gui.font.FontAlignment;
import ink.flybird.quantum3d.device.Window;
import ink.flybird.quantum3d.draw.VertexBuilder;
import ink.flybird.quantum3d.draw.VertexBuilderAllocator;
import ink.flybird.cubecraft.client.internal.registry.TextureRegistry;
import ink.flybird.fcommon.GameSetting;
import ink.flybird.quantum3d.GLUtil;
import ink.flybird.quantum3d.ShapeRenderer;
import ink.flybird.quantum3d.textures.Texture2D;
import ink.flybird.quantum3d.textures.TextureStateManager;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ScreenUtil {
    private static final ArrayList<Popup> popupList=new ArrayList<>();
    static GameSetting gameSetting=null;

    public static void init(CubecraftClient client){
        gameSetting=client.getGameSetting();
    }

    public static void renderPictureBackground(Window window){
        float scale= gameSetting.getValueAsInt("client.render.gui_scale",2);
        TextureRegistry.IMAGE_BG.bind();
        ShapeRenderer.begin();
        ShapeRenderer.drawRectUV(0, window.getWidth()/ scale,0,window.getHeight()/scale,-1, 0,1,0,1);
        ShapeRenderer.end();
        TextureRegistry.IMAGE_BG.unbind();
    }

    public static void renderMask(Window window){
        GLUtil.enableBlend();
        float scale= gameSetting.getValueAsInt("client.render.gui_scale",2);
        ShapeRenderer.setColor(0,0,0,127);
        ShapeRenderer.drawRect(0,window.getWidth()/ scale,0,window.getHeight()/scale,-1,-1);
    }

    public static void createPopup(String title, String subTitle, int time, int type){
        popupList.add(new Popup(title,subTitle,time,type));
    }

    public static void tickToasts(){
        Iterator<Popup> p= popupList.iterator();
        while (p.hasNext()){
            Popup pop=p.next();
            pop.tick();
            if(pop.remaining<=0){
                p.remove();
            }
        }
    }

    public static void renderToasts(DisplayScreenInfo info, float interpolationTime){
        TextureRegistry.TOAST.bind();
        int yPop=0;
        for (Popup p: ((List<Popup>) popupList.clone())){

            GL11.glPushMatrix();
            GL11.glTranslated(info.scrWidth()-200-16+p.getPos(interpolationTime),yPop,0);
            p.render();
            GL11.glPopMatrix();
            yPop+=50;
        }
        TextureRegistry.TOAST.unbind();
    }

    public static void drawFontASCII(String s, int x, int y, int color, int size, FontAlignment alignment){
        if(s==null){
            return;
        }
        GLUtil.enableBlend();
        char[] rawData = s.toCharArray();
        int contWidth = 0;
        for (char c : rawData) {
            int pageCode = (int) Math.floor(c / 256.0f);
            String s2 = Integer.toHexString(pageCode);
            if (c == ' ') {
                contWidth += size;
            } else if (s2.equals("0")) {
                contWidth += size / 2;
            } else {
                contWidth += size;
            }
        }
        int charPos_scr = 0;
        switch (alignment) {
            case LEFT -> charPos_scr = x;
            case MIDDLE -> charPos_scr = (int) (x - contWidth / 2.0f);
            case RIGHT -> charPos_scr = x - contWidth;
        }
        TextureRegistry.ASCII_PAGE.bind();
        for (char c : rawData) {
            VertexBuilder builder = VertexBuilderAllocator.createByPrefer(4);
            int charPos_Page = c % 256;
            int charPos_V = charPos_Page / 16;
            int charPos_H = charPos_Page % 16;
            if (c == 0x0020) {
                charPos_scr += size * 0.75;
            }
            else if (c == 0x000d) {
                charPos_scr = 0;
            }
            else {
                float x0 = charPos_scr, x1 = charPos_scr + size, y1 = y + size,
                        u0 = charPos_H / 16.0f, u1 = charPos_H / 16f + 0.0625f,
                        v0 = charPos_V / 16.0f, v1 = charPos_V / 16f + 0.0625f;
                builder.begin();
                builder.color(color);
                ShapeRenderer.drawRectUV(builder,x0, x1, (float) y, y1, 0, u0,u1,v0,v1);
                builder.end();
                builder.uploadPointer();
                charPos_scr += size*0.5f;
            }
            builder.free();
        }
        TextureRegistry.ASCII_PAGE.unbind();
    }

    public static void renderTileBackground() {
        //todo:add minecraft themed tile background rendering
    }

    public static void renderPictureBackgroundBlur(Window window) {
        float scale= gameSetting.getValueAsInt("client.render.gui_scale",2);
        Texture2D tex= TextureRegistry.IMAGE_BG;
        TextureStateManager.setTextureBlur(tex,true,3);
        tex.bind();
        ShapeRenderer.begin();
        ShapeRenderer.drawRectUV(0, window.getWidth()/ scale,0,window.getHeight()/scale,-1, 0,1,0,1);
        ShapeRenderer.end();
        TextureStateManager.setTextureBlur(tex,false,0);
        tex.unbind();
    }
}
