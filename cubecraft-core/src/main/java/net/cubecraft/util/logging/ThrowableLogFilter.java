package net.cubecraft.util.logging;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.filter.AbstractFilter;

@Plugin(name = "ThrowableLogFilter", category = "Core", elementType = Filter.ELEMENT_TYPE, printObject = true)
public class ThrowableLogFilter extends AbstractFilter {

    @PluginFactory
    public static ThrowableLogFilter createFilter() {
        return new ThrowableLogFilter();
    }

    @Override
    public Result filter(LogEvent event) {
        if (event.getThrown() == null) {
            return Result.NEUTRAL;
        }

        var logger = LogManager.getLogger(event.getLoggerName());
        var throwable = event.getThrown();

        logger.error("{}: {}", throwable.getClass().getName(), throwable.getLocalizedMessage());

        for (StackTraceElement element : throwable.getStackTrace()) {
            logger.error(element.toString());
        }

        if (throwable.getCause() != null) {
            logger.throwing(throwable.getCause());
        }

        return Result.DENY;
    }
}