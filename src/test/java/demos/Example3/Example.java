package demos.Example3;

import org.junit.Test;
import slf4jtest.*;

import java.util.regex.Pattern;

/**
 * This example demonstrates constructing a nested assembly, ie the class Example, and testing it's logging
 */
public class Example {

    @Test
    public void testMultiLineMatching() {

        // enable info logging because only error is enabled by default
        Settings settings = new Settings().enable(LogLevel.InfoLevel);
        TestLoggerFactory loggerFactory = new TestLoggerFactory(settings);
        TestLogger logger = loggerFactory.getLogger(this.getClass());

        // do some multiline logging
        logger.info("Line1" + System.lineSeparator() + "Line2");

        // this one doesn't match multiline logging
        assert (!loggerFactory.matches("Line1.*"));

        // using DOTALL we can match multiline
        Pattern regex = Pattern.compile("Line1.*", Pattern.DOTALL);
        Pattern regexNoMatch = Pattern.compile("NOMATCH");

        assert (loggerFactory.matches(regex));
        assert (!loggerFactory.matches(regexNoMatch));

        assert (loggerFactory.matches(LogLevel.InfoLevel, regex));
        assert (!loggerFactory.matches(LogLevel.ErrorLevel, regex));

        assert (logger.matches(regex));
        assert (!logger.matches(regexNoMatch));

        assert (logger.matches(LogLevel.InfoLevel, regex));
        assert (!logger.matches(LogLevel.ErrorLevel, regex));
    }

    @Test
    public void testLogMessageMatching() {

        // enable info logging because only error is enabled by default
        Settings settings = new Settings().enable(LogLevel.InfoLevel);
        TestLoggerFactory loggerFactory = new TestLoggerFactory(settings);
        TestLogger logger = loggerFactory.getLogger(this.getClass());

        logger.info("Line1" + System.lineSeparator() + "Line2");

        Pattern pattern = Pattern.compile("Line1.*", Pattern.DOTALL);

        boolean found = false;
        for (LogMessage l : logger.lines()) {
            if (pattern.matcher(l.text).matches())
                found = true;
        }
        assert (found);
    }
}

