package io.flybird.cubecraft.register;

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
import io.flybird.cubecraft.auth.SessionService;

import io.flybird.cubecraft.extansion.ExtensionManager;
import io.flybird.cubecraft.net.packet.Packet;
import org.iq80.leveldb.DBFactory;
import org.iq80.leveldb.impl.Iq80DBFactory;

/**
 * simple register entry set...
 */
public interface SharedContext {
    GsonBuilder GSON_BUILDER=new GsonBuilder();
    XmlReader FAML_READER = new XmlReader();

    //common
    RegisterMap<SessionService, ?> SESSION_SERVICE = new RegisterMap<>(null);
    ConstructingMap<Packet> PACKET = new ConstructingMap<>(Packet.class);
    I18nHelper I18N =new I18nHelper(I18nHelper.CHINESE_SIMPLIFIED,I18nHelper.ENGLISH);

    ExtensionManager MOD=new ExtensionManager();

    DynamicURLClassLoader CLASS_LOADER=new DynamicURLClassLoader();
    ThreadInitializer THREAD_INITIALIZER=new ThreadInitializer(CLASS_LOADER);
    LoggerContext LOG_CONTEXT = new DefaultLoggerContext(null);


    DBFactory LEVELDB_FACTORY=new Iq80DBFactory();

    static Gson createJsonReader(){
        return GSON_BUILDER.create();
    }


}