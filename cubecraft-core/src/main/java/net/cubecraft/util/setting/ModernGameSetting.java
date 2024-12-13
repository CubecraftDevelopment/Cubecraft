package net.cubecraft.util.setting;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.cubecraft.util.setting.item.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public final class ModernGameSetting {
    public static final Logger LOGGER = LogManager.getLogger("GameSettings");
    public static final String FILE = System.getProperty("user.dir") + "/config/%s.cfg.json";
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final String name;
    private final Map<String, SettingItem<?>> items = new HashMap<>();
    private final Map<String, Object> loadedValues = new HashMap<>();

    public ModernGameSetting(String name) {
        this.name = name;
    }

    public void register(SettingItem<?> item) {
        this.items.put(item.id(), item);

        if (this.loadedValues.containsKey(item.id())) {
            if (item.useCustomSerialization()) {
                item.deserialize(((String) this.loadedValues.get(item.id())));
                return;
            }
            item.setValue(this.loadedValues.get(item.id()));
        }
    }

    public void register(SettingItem<?> item, String namespace) {
        SettingNSInjector.inject(item, namespace);

        register(item);
    }

    public String getName() {
        return name;
    }

    public void register(Class<?> c) {
        var namespace = "";

        if (c.isAnnotationPresent(Settings.class)) {
            namespace = c.getAnnotation(Settings.class).value();
        }

        for (var field : c.getDeclaredFields()) {
            if (!SettingItem.class.isAssignableFrom(field.getType())) {
                continue;
            }

            try {
                register((SettingItem<?>) field.get(null), namespace);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public <I extends SettingItem<?>> I get(String path, Class<I> type) {
        return type.cast(this.items.get(path));
    }

    public <I> I getValue(String path, Class<I> type) {
        return type.cast(this.items.get(path).getValue());
    }

    private File getFile() {
        var file = new File(FILE.formatted(getName()));

        if (!file.exists()) {
            if (file.getParentFile().mkdirs()) {
                LOGGER.info("created settings folder.");
            }
            try {
                if (file.createNewFile()) {
                    LOGGER.info("created settings file {}.", file.getAbsolutePath());
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return file;
    }

    public void load() {
        var f = getFile();

        try {
            var root = JsonParser.parseReader(new FileReader(f));

            if (root == null || root.isJsonNull()) {
                return;
            }

            _loadItem(null, root.getAsJsonObject());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void save() {
        var file = getFile();

        try (var o = new FileOutputStream(file)) {
            o.write(saveAsString().getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String saveAsString() {
        var root = new JsonObject();

        for (var id : this.items.keySet()) {
            _saveItem(get(id, SettingItem.class), root, id.split(":"), 0);
        }

        return GSON.toJson(root);
    }

    private void _loadItem(String path, JsonObject o) {
        for (var s : o.keySet()) {
            var e = o.get(s);
            var id = path == null ? s : path + ":" + s;

            if (e.isJsonObject()) {
                _loadItem(id, e.getAsJsonObject());
                continue;
            }

            var p = e.getAsJsonPrimitive();

            if (p.isNumber()) {
                this.loadedValues.put(id, p.getAsNumber());
                continue;
            }
            if (p.isString()) {
                this.loadedValues.put(id, p.getAsString());
                continue;
            }
            if (p.isBoolean()) {
                this.loadedValues.put(id, p.getAsBoolean());
            }
        }
    }

    private void _saveItem(SettingItem<?> item, JsonObject o, String[] dc, int pIndex) {
        var cursor = dc[pIndex];

        if (pIndex != dc.length - 1) {
            var sect = o.getAsJsonObject(cursor);

            if (sect == null) {
                sect = new JsonObject();
                o.add(cursor, sect);
            }

            _saveItem(item, sect, dc, pIndex + 1);
            return;
        }

        if (item instanceof BoolSetting b) {
            o.addProperty(cursor, b.getValue());
            return;
        }
        if (item instanceof IntSetting i) {
            o.addProperty(cursor, i.getValue());
            return;
        }
        if (item instanceof FloatSetting f) {
            o.addProperty(cursor, f.getValue());
            return;
        }
        if (item instanceof StringSetting i) {
            o.addProperty(cursor, i.getValue());
            return;
        }
        o.addProperty(cursor, item.serialize());
    }

    private static final class SettingNSInjector {
        public static final Field F_NAMESPACE;

        static {
            try {
                F_NAMESPACE = SettingItem.class.getDeclaredField("namespace");
                F_NAMESPACE.setAccessible(true);
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
        }

        static void inject(SettingItem<?> item, String namespace) {
            try {
                var origin = F_NAMESPACE.get(item);
                F_NAMESPACE.set(item, ((String) origin).replaceFirst("\\*", namespace));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
