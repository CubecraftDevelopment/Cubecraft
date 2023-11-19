package net.cubecraft;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ink.flybird.fcommon.I18nHelper;
import ink.flybird.fcommon.context.DynamicURLClassLoader;
import ink.flybird.fcommon.context.ThreadInitializer;
import ink.flybird.fcommon.file.XmlReader;
import ink.flybird.fcommon.logging.DefaultLoggerContext;
import ink.flybird.fcommon.logging.LoggerContext;
import ink.flybird.fcommon.registry.ConstructingMap;
import ink.flybird.fcommon.registry.RegisterMap;
import net.cubecraft.auth.SessionService;

import net.cubecraft.extension.ModManager;
import net.cubecraft.net.packet.Packet;
import ink.flybird.jflogger.LogManager;
import ink.flybird.jflogger.config.ConfigBuilder;
import org.iq80.leveldb.DBFactory;
import org.iq80.leveldb.impl.Iq80DBFactory;

import java.util.HashMap;

/**
 * simple register entry set...
 */
public interface SharedContext {


    GsonBuilder GSON_BUILDER=new GsonBuilder();
    XmlReader FAML_READER = new XmlReader();

    //common
    RegisterMap<SessionService> SESSION_SERVICE = new RegisterMap<>(null);
    ConstructingMap<Packet> PACKET = new ConstructingMap<>(Packet.class);
    I18nHelper I18N =new I18nHelper(I18nHelper.CHINESE_SIMPLIFIED,I18nHelper.ENGLISH);

    ModManager MOD=new ModManager();

    DynamicURLClassLoader CLASS_LOADER=new DynamicURLClassLoader();
    ThreadInitializer THREAD_INITIALIZER=new ThreadInitializer(CLASS_LOADER);
    LoggerContext LOG_CONTEXT = new DefaultLoggerContext(null);


    DBFactory LEVELDB_FACTORY=new Iq80DBFactory();
    HashMap<Thread,StringBuilder> THREAD_LOCAL_ENCODER=new HashMap<>();

    static Gson createJsonReader(){
        return GSON_BUILDER.create();
    }



    static void initializeLogContext(){
        LogManager.loadUp(new ConfigBuilder());
    }
}