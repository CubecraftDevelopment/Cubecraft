package net.cubecraft.client.gui.base;

import net.cubecraft.client.ClientContext;
import net.cubecraft.client.gui.font.FontAlignment;
import net.cubecraft.SharedContext;
import me.gb2022.commons.file.FAMLDeserializer;
import me.gb2022.commons.file.XmlReader;
import me.gb2022.commons.math.MathHelper;
import com.google.gson.*;
import net.cubecraft.text.TextComponent;
import org.w3c.dom.Element;

import java.util.Random;

public class Text {
    private TextComponent text;
    private FontAlignment alignment;
    private final boolean isIcon;

    public Text(String text, int color, FontAlignment alignment, boolean isIcon) {
        this.text = TextComponent.text(text).color(color);
        this.alignment = alignment;
        this.isIcon = isIcon;
    }

    public Text(String text, int color, FontAlignment alignment){
        this(text,color,alignment,false);
    }

    public FontAlignment getAlignment() {
        return alignment;
    }

    public TextComponent getText() {
        return text;
    }

    public void setText(TextComponent text) {
        this.text = text;
    }

    public void setAlignment(FontAlignment alignment) {
        this.alignment = alignment;
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
                splash = new Gson().fromJson(ClientContext.RESOURCE_MANAGER.getResource(src).getAsText(), String[].class);
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
