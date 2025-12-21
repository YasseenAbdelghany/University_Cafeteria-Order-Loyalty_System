package terminal;

import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.logging.ConsoleHandler;
import java.util.logging.SimpleFormatter;

/**
 * Centralized logger factory following Single Responsibility Principle
 * This class handles all logging configuration and creation
 */
public class LoggerFactory {
    private static final LoggerFactory instance = new LoggerFactory();

    private LoggerFactory() {
        // Configure root logger
        Logger rootLogger = Logger.getLogger("");
        ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(new SimpleFormatter());
        rootLogger.addHandler(handler);
        rootLogger.setLevel(Level.SEVERE);

    }

    public static Logger getLogger(Class<?> clazz) {
        return Logger.getLogger(clazz.getName());
    }

    public static LoggerFactory getInstance() {
        return instance;
    }
}
