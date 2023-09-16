package ink.flybird.cubecraft.util.setting;

import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlWriter;
import ink.flybird.fcommon.event.EventBus;
import ink.flybird.cubecraft.event.SettingReloadEvent;
import ink.flybird.cubecraft.register.EnvironmentPath;
import ink.flybird.cubecraft.util.setting.item.SettingItem;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

//todo:迁移配置文件
public final class GameSetting {
    private final HashMap<String, SettingItem<?>> data = new HashMap<>();
    private final HashMap<String, Object> modify = new HashMap<>();
    private final HashMap<String, Map<String, Object>> map = new HashMap<>();
    private final String file;
    private EventBus eventBus;

    public GameSetting(String file) {
        this.file = file;
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    public void setEventBus(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public HashMap<String, Object> getModify() {
        return modify;
    }

    public HashMap<String, SettingItem<?>> getData() {
        return data;
    }

    public String getFile() {
        return file;
    }

    public void load() {
        Toml toml = new Toml();
        File f=new File(EnvironmentPath.CONFIG_FOLDER + this.file);
        if(!f.exists()){
            return;
        }
        toml.read(f);
        for (String s : toml.toMap().keySet()) {
            Toml table = toml.getTable(s);
            if (toml.getTable(s) == null) {
                continue;
            }
            Map<String,Object> map=table.toMap();
            for (String pth:map.keySet()){
                if(!this.data.containsKey(s+":"+pth)){
                    continue;
                }
                this.data.get(s+":"+pth).setValue(map.get(pth));
            }
            this.map.put(s, table.toMap());
        }
        if(this.eventBus==null){
            return;
        }
        this.eventBus.callEvent(new SettingReloadEvent(this));
    }

    private void save() {
        for (String s : this.modify.keySet()) {
            this.data.get(s).setValue(this.modify.get(s));
            this.map.get(s.split(":")[0]).put(s.split(":")[1], this.modify.get(s));
        }
        this.modify.clear();
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(EnvironmentPath.CONFIG_FOLDER + this.file);
            fileOutputStream.write(new TomlWriter().write(this.map).getBytes(StandardCharsets.UTF_8));
            fileOutputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void modify(String ns, String path, Object value) {
        this.modify(ns + ":" + path, value);
    }

    public void modify(String path, Object value) {
        this.modify.put(path, value);
    }

    public void register(Class<?> clazz) {
        for (Field f : clazz.getDeclaredFields()) {
            if (f.getAnnotation(SettingItemRegistry.class) == null) {
                continue;
            }
            if (!Modifier.isStatic(f.getModifiers())) {
                continue;
            }
            SettingItem<?> item;
            f.setAccessible(true);
            try {
                item = (SettingItem<?>) f.get(null);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            this.data.put(item.getNamespace() + ":" + item.getPath(), item);
            Map<String, Object> section = this.map.computeIfAbsent(item.getNamespace(), k -> new HashMap<>());
            if (section.containsKey(item.getPath())) {
                item.setValue(section.get(item.getPath()));
            } else {
                section.put(item.getPath(), item.getDefine());
            }
        }
        this.save();
    }
}