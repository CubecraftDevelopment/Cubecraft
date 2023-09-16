package ink.flybird.cubecraft.net;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.whirvis.jraknet.identifier.Identifier;
import ink.flybird.fcommon.math.NumberCodec;

import java.awt.image.BufferedImage;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * a MOTD from server,powered by JRakNet.
 *
 * @author GrassBlock2022
 */
public class CubecraftProtocolIdentifier extends Identifier {
    public static final String IDENTIFIER_SEGMENT = "CUBECRAFT_JAVA";

    private final String version;
    private final String desc;
    private final BufferedImage img;

    public CubecraftProtocolIdentifier(String version, String desc, BufferedImage img) {
        this.version = version;
        this.desc = desc;
        this.img = img;
    }

    /**
     * verify if it is a cubecraft server identifier.
     *
     * @param identifier identifier
     */
    public static boolean isCubecraftIdentifier(Identifier identifier) {
        return identifier.build().startsWith(IDENTIFIER_SEGMENT);
    }

    /**
     * build a MOTD from string.
     *
     * @param s string
     * @return motd
     */
    public static CubecraftProtocolIdentifier from(String s) {
        if (!isCubecraftIdentifier(new Identifier(s))) {
            throw new RuntimeException("this is NOT a cubecraft server identifier.");
        }

        JsonObject root = JsonParser.parseString(s).getAsJsonObject();
        JsonObject img = root.get("img").getAsJsonObject();

        //motd image
        int w = img.get("width").getAsInt();
        int h = img.get("height").getAsInt();
        byte[] data = Base64.getDecoder().decode(img.get("data").getAsString());
        int[] arr = new int[w * h];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = NumberCodec.asInt(new byte[]{data[i * 4], data[i * 4 + 1], data[i * 4 + 2], data[i * 4 + 3]});
        }
        BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        image.setRGB(0, 0, w, h, arr, 0, w);


        String version = root.get("version").getAsString();
        String desc = root.get("desc").getAsString();

        return new CubecraftProtocolIdentifier(version, desc, image);
    }

    /**
     * {@inheritDoc}
     *
     * @return built MOTD.
     */
    @Override
    public String build() {
        int w = img.getWidth();
        int h = img.getHeight();

        int[] rawPixels = new int[w * h];
        img.getRGB(0, 0, w, h, rawPixels, 0, w);

        byte[] newPixels1 = new byte[rawPixels.length * 4];
        for (int i = 0; i < rawPixels.length; ++i) {
            int a = rawPixels[i] >> 24 & 0xFF;
            int r = rawPixels[i] >> 16 & 0xFF;
            int g = rawPixels[i] >> 8 & 0xFF;
            int b = rawPixels[i] & 0xFF;
            newPixels1[i * 4] = (byte) r;
            newPixels1[i * 4 + 1] = (byte) g;
            newPixels1[i * 4 + 2] = (byte) b;
            newPixels1[i * 4 + 3] = (byte) a;
        }

        JsonObject root = new JsonObject();
        root.add("version", new JsonPrimitive(this.version));
        root.add("desc", new JsonPrimitive(this.desc));
        JsonObject img = new JsonObject();
        img.add("width", new JsonPrimitive(w));
        img.add("height", new JsonPrimitive(h));
        img.add("data", new JsonPrimitive(new String(Base64.getEncoder().encode(newPixels1), StandardCharsets.UTF_8)));
        root.add("image", img);

        return IDENTIFIER_SEGMENT + "_" + new Gson().toJson(root);
    }

    public String getVersion() {
        return version;
    }

    public String getDesc() {
        return desc;
    }

    public BufferedImage getImg() {
        return img;
    }
}
