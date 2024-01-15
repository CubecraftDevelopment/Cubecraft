package net.cubecraft.mod.object;

import com.moandjiezana.toml.Toml;
import net.cubecraft.SharedContext;
import net.cubecraft.Side;
import net.cubecraft.exception.ModConstructException;
import net.cubecraft.mod.CubecraftMod;

import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarFile;

public abstract class Mod {
    public static final Mod CLIENT = integrated("/client_internal_mod.toml");
    public static final Mod SERVER = integrated("/server_internal_mod.toml");
    public static final Mod SHARED = integrated("/core_internal_mod.toml");

    private final Toml document = new Toml();
    private Class<?> modClass;
    private Object modObject;


    public static Mod integrated(String location) {
        return new IntegratedMod(location);
    }

    public static Mod standalone(JarFile file) {
        return new StandAloneMod(file);
    }

    public static Side getExtensionSide(Class<?> clazz) {
        CubecraftMod extension = clazz.getAnnotation(CubecraftMod.class);
        if (extension == null) {
            return null;
        }
        return extension.side();
    }

    public static int getExtensionAPIVersion(Class<?> clazz) {
        CubecraftMod extension = clazz.getAnnotation(CubecraftMod.class);
        if (extension == null) {
            return 0;
        }
        return extension.apiVersion();
    }

    public abstract void loadDescriptionInfo();

    public final Toml getDocument() {
        return document;
    }

    public final Set<String> getDependencies(Side side) {
        String id = switch (side) {
            case CLIENT -> "dependencies-client";
            case SERVER -> "dependencies-server";
            case SHARED -> "dependencies-shared";
        };

        if (this.document.getTable(id) == null) {
            return new HashSet<>();
        }
        return this.document.getTable(id).toMap().keySet();
    }

    public final Set<String> getDependencies() {
        Set<String> list = new HashSet<>();
        for (Side s : Side.values()) {
            list.addAll(this.getDependencies(s));
        }
        return list;
    }

    public final Class<?> getModClass() {
        return modClass;
    }

    public final Object getModObject() {
        return modObject;
    }

    public final String getId() {
        return this.document.getTable("meta").getString("id");
    }

    public final String getModClassName() {
        return this.document.getTable("meta").getString("class");
    }

    public final boolean isStandalone() {
        return this instanceof StandAloneMod;
    }

    public final void construct() {
        try {
            this.modClass = SharedContext.CLASS_LOADER.loadClass(this.getModClassName());
        } catch (Exception e) {
            try {
                this.modClass = Class.forName(this.getModClassName());
            } catch (ClassNotFoundException ex) {
                throw new ModConstructException(e);
            }
        }

        try {
            this.modObject = this.modClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new ModConstructException(e);
        }
    }

    public Side getSide() {
        return Side.fromString(this.document.getTable("meta").getString("side"));
    }
}
