package me.gb2022.quantum3d;

import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

public interface Q3DLoggerContext {
    ILoggerFactory[] LOGGER_FACTORY_CONTAINER = new ILoggerFactory[1];

    static void setContext(ILoggerFactory logger) {
        LOGGER_FACTORY_CONTAINER[0] = logger;
    }

    static Logger getLogger(String name) {
        return LOGGER_FACTORY_CONTAINER[0].getLogger(name);
    }
}
