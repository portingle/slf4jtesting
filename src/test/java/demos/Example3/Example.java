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
        TestLoggerFactory loggerFactory = Settings.instance().enable(LogLevel.InfoLevel).buildLogging();
        TestLogger logger = loggerFactory.getLogger(this.getClass());

        // do some multiline logging
        logger.info("Line1" + System.lineSeparator() + "Line2");

        // this one does match multiline logging
        assert (loggerFactory.matches("Line1.*"));

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
        TestLoggerFactory loggerFactory = Settings.instance().enable(LogLevel.InfoLevel)
                .buildLogging();

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

    @Test
    public void testLogMessageMatchingUsingPredicate() {

        // enable info logging because only error is enabled by default
        TestLoggerFactory loggerFactory = Settings.instance().enable(LogLevel.InfoLevel).buildLogging();
        TestLogger logger = loggerFactory.getLogger(this.getClass());

        logger.info("Line1" + System.lineSeparator() + "Line2");

        final Pattern pattern = Pattern.compile("Line1.*", Pattern.DOTALL);

        logger.assertMatches(new Predicate<LogMessage>() {
            public boolean matches(LogMessage row) {
                return pattern.matcher(row.text).matches() && row.level == LogLevel.InfoLevel;
            }
        });

        try {
            final Pattern nonMatch = Pattern.compile("NOTGOOD");

            logger.assertMatches(new Predicate<LogMessage>() {
                public boolean matches(LogMessage row) {
                    return nonMatch.matcher(row.text).matches();
                }

                @Override
                public String toString() {
                    return nonMatch.toString();
                }
            });

            throw new RuntimeException("ought to have failed");
        } catch (AssertionError ex) {
            assert(ex.getMessage().equals("did not match NOTGOOD"));
        }
    }
}