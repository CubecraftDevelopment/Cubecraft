package ink.flybird.cubecraft.client.gui.base;

import ink.flybird.cubecraft.client.ClientRenderContext;
import ink.flybird.cubecraft.client.gui.font.FontAlignment;
import io.flybird.cubecraft.register.SharedContext;
import ink.flybird.fcommon.file.FAMLDeserializer;
import ink.flybird.fcommon.file.XmlReader;
import ink.flybird.fcommon.math.MathHelper;
import com.google.gson.*;
import org.w3c.dom.Element;

import java.util.Random;

public class Text {
    private String text;
    private int color;
    private FontAlignment alignment;
    private final boolean isIcon;

    public Text(String text, int color, FontAlignment alignment, boolean isIcon) {
        this.text = text;
        this.color = color;
        this.alignment = alignment;
        this.isIcon = isIcon;
    }

    public Text(String text, int color, FontAlignment alignment){
        this(text,color,alignment,false);
    }

    public FontAlignment getAlignment() {
        return alignment;
    }

    public String getText() {
        return text;
    }

    public int getColor() {
        return color;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setAlignment(FontAlignment alignment) {
        this.alignment = alignment;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public static class XMLDeserializer implements FAMLDeserializer<Text> {
        @Override
        public Text deserialize(Element element, XmlReader reader) {
            return new Text(
                    getText(element.getAttribute("type"), element.getTextContent()),
                    MathHelper.hex2Int(element.getAttribute("color")),
                    FontAlignment.from(element.getAttribute("align")),
                    element.getAttribute("type").equals("icon"));
        }
    }


    private static String getText(String type, String src) {
        return switch (type) {
            case "raw" -> src;
            case "lang" -> SharedContext.I18N.get(src);
            case "icon" -> String.valueOf(((char) MathHelper.hex2Int(src)));
            case "random" -> {
                String[] splash;
                splash = new Gson().fromJson(ClientRenderContext.RESOURCE_MANAGER.getResource(src).getAsText(), String[].class);
                yield splash[new Random().nextInt(splash.length)];
            }
            default -> throw new IllegalArgumentException("no matched constant named %s".formatted(type));
        };
    }

    public static Text raw(String s){
        return new Text(s,0xFFFFFF,FontAlignment.MIDDLE);
    }

    public static Text translated(String s,Object... args){
        return new Text(SharedContext.I18N.get(s,args), 0xFFFFFF,FontAlignment.MIDDLE);
    }

    public static Text translated(String s,int color,Object... args){
        return new Text(SharedContext.I18N.get(s,args), color,FontAlignment.MIDDLE);
    }

    public static Text translated(String s,FontAlignment alignment,Object... args){
        return new Text(SharedContext.I18N.get(s,args), 0xFFFFFF,alignment);
    }

    public boolean isIcon() {
        return isIcon;
    }
}
