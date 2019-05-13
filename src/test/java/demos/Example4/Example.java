package demos.Example4;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import org.junit.Test;
import org.slf4j.LoggerFactory;
import slf4jtest.LogLevel;
import slf4jtest.Settings;
import slf4jtest.TestLogger;
import slf4jtest.TestLoggerFactory;

/**
 * This example demonstrates replacing the default console printing with
 * a logger that's been configured with a pattern.
 * This logger doesn't impact the test api, only the rendition on the console.
 */
public class Example {

    private Settings installAlternativeScreenLogger(Settings s) {
        LoggerContext context = (LoggerContext)LoggerFactory.getILoggerFactory();

//Create a new console
        ConsoleAppender<ILoggingEvent> consoleAppender = new ConsoleAppender<>();
        consoleAppender.setContext(context);

//Message Encoder
        PatternLayoutEncoder ple = new PatternLayoutEncoder();
        ple.setContext(context);
        ple.setPattern("MYPATTERN %date %level [%thread] %logger{10} %msg%n");
        ple.start();
        consoleAppender.setEncoder(ple);

        consoleAppender.start();

//Get ROOT logger, and add appender to it
        ch.qos.logback.classic.Logger alternativeLogger = context.getLogger("CONSOLE"); //Logger.ROOT_LOGGER_NAME);
        alternativeLogger.setAdditive(false);
        alternativeLogger.setLevel(Level.INFO);
        alternativeLogger.addAppender(consoleAppender);

        return s.printingEnabled(false).delegate(this.getClass().getName(), alternativeLogger);
    }

    @Test
    public void testDelegation() {

        // enable info logging because only error is enabled by default
        Settings basicSettings = Settings.instance().enable(LogLevel.InfoLevel);

        TestLoggerFactory loggerFactory = installAlternativeScreenLogger(basicSettings).buildLogging();
        TestLogger logger = loggerFactory.getLogger(this.getClass());

        // do some multiline logging
        logger.info("Line1" + System.lineSeparator() + "Line2");

        // this one does match multiline logging
        assert (loggerFactory.matches("Line1.*"));
        assert (loggerFactory.matches("Line1.*Line2"));
        assert (!loggerFactory.matches("MYPATTERN.*"));

    }

}