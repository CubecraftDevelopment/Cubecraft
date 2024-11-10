package net.cubecraft.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;

public interface NumberResolver {
    static Number parse(JsonElement element) {
        if (!element.isJsonPrimitive()) {
            throw new JsonSyntaxException("Expected a primitive value");
        }

        var primitive = element.getAsJsonPrimitive();

        if (primitive.isNumber()) {
            return primitive.getAsNumber().intValue();
        }

        if (primitive.isString()) {
            return parse(element.getAsString());
        }

        throw new JsonSyntaxException("Expected a string | number value");
    }

    static Number parse(String string) {
        if (string.startsWith("0x")) {
            return Integer.parseInt(string.substring(2), 16);
        }
        if (string.startsWith("#")) {
            return Integer.parseInt(string.substring(1), 16);
        }
        return Double.parseDouble(string);
    }
}
