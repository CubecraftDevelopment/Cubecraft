package net.cubecraft;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.gb2022.commons.I18nHelper;
import me.gb2022.commons.context.DynamicURLClassLoader;
import me.gb2022.commons.context.ThreadInitializer;
import me.gb2022.commons.file.XmlReader;
import me.gb2022.commons.registry.ConstructingMap;
import me.gb2022.commons.registry.RegisterMap;
import org.apache.logging.log4j.LogManager;
import ink.flybird.jflogger.config.ConfigBuilder;
import net.cubecraft.auth.SessionService;
import net.cubecraft.mod.ModManager;
import net.cubecraft.net.packet.Packet;
import org.iq80.leveldb.DBFactory;
import org.iq80.leveldb.impl.Iq80DBFactory;

import java.util.HashMap;

/**
 * simple register entry set...
 */
public interface SharedContext {
    GsonBuilder GSON_BUILDER = new GsonBuilder();
    XmlReader FAML_READER = new XmlReader();

    //common
    RegisterMap<SessionService> SESSION_SERVICE = new RegisterMap<>(null);

    ConstructingMap<Packet> PACKET = new ConstructingMap<>(Packet.class);

    I18nHelper I18N = new I18nHelper(I18nHelper.CHINESE_SIMPLIFIED, I18nHelper.ENGLISH);

    ModManager MOD = new ModManager();

    DynamicURLClassLoader CLASS_LOADER = new DynamicURLClassLoader();
    ThreadInitializer THREAD_INITIALIZER = new ThreadInitializer(CLASS_LOADER);

    DBFactory LEVELDB_FACTORY = new Iq80DBFactory();
    HashMap<Thread, StringBuilder> THREAD_LOCAL_ENCODER = new HashMap<>();

    static Gson createJsonReader() {
        return GSON_BUILDER.create();
    }


    static void initializeLogContext() {
        //LogManager.loadUp(new ConfigBuilder());
    }

}