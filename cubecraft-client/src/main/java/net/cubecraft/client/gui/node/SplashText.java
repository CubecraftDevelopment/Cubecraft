package net.cubecraft.client.gui.node;

import com.google.gson.Gson;
import net.cubecraft.client.ClientSharedContext;
import net.cubecraft.client.gui.GUIRegistry;
import net.cubecraft.client.gui.font.FontAlignment;
import ink.flybird.fcommon.file.DocumentUtil;
import ink.flybird.fcommon.math.MathHelper;
import ink.flybird.fcommon.registry.TypeItem;
import org.lwjgl.opengl.GL11;
import org.w3c.dom.Element;

import java.util.Random;

@TypeItem("splash")
public class SplashText extends Component {
    int rotation;
    boolean bobbing;
    String text;
    int color;

    @Override
    public void init(Element element) {
        super.init(element);
        String[] splash;
        splash = new Gson().fromJson(ClientSharedContext.RESOURCE_MANAGER.getResource(element.getTextContent().trim()).getAsText(), String[].class);
        this.text = splash[new Random().nextInt(splash.length)];
        this.bobbing = DocumentUtil.getAttributeB(element, "bobbing", false);
        this.rotation = DocumentUtil.getAttributeI(element, "rotation", 0);
        this.color = MathHelper.hex2Int(DocumentUtil.getAttribute(element, "color", "ffffff"));
    }

    @Override
    public void render(float interpolationTime) {
        GL11.glPushMatrix();
        GL11.glTranslatef(this.getLayout().getAbsoluteX(), this.getLayout().getAbsoluteY(), 0);
        double sin = Math.sin(System.currentTimeMillis() / 300d) * 0.1 + 1.1;
        if (this.bobbing) {
            GL11.glScaled(sin, sin, sin);
        }
        GL11.glRotatef(this.rotation, 0, 0, 1);
        GUIRegistry.SMOOTH_FONT_RENDERER.renderShadow(this.text, 0, 0, this.color, getLayout().getAbsoluteHeight(), FontAlignment.MIDDLE);
        GL11.glPopMatrix();
    }
}
