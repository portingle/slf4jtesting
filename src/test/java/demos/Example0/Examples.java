package demos.Example0;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.slf4j.Logger;
import slf4jtest.*;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class Examples {

    /**
     * trivial demo of calling the logging functions on a logger and verifying what was logged.
     */
    @Test
    public void demoConsoleLoggingUsingConstructor() throws Exception {

        TestLoggerFactory loggerFactory = Settings.instance()
                .enableAll() // necessary as by default only ErrorLevel is enabled
                .buildLogging();

        TestLogger logger = loggerFactory.getLogger("MyLogger");

        // expect to see some console logging
        logger.info("Hello World!");

        // verification - the TestLogger instance will have collected all the enabled logging was sent to the loggers
        TestLogger testLogger = loggerFactory.getLogger("MyLogger");
        Assert.assertTrue(testLogger.matches("Hello World!"));
    }


    /**
     * trivial demo of calling the logging functions on a logger and verifying what was logged.
     */
    @Test
    public void demoConsoleLoggingUsingBuilder() throws Exception {

        TestLoggerFactory loggerFactory = Settings.instance()
                .enableAll() // necessary as by default only ErrorLevel is enabled
                .buildLogging();

        TestLogger logger = loggerFactory.getLogger("MyLogger");

        // expect to see some console logging
        logger.info("Hello World!");

        // verification - the TestLogger instance will have collected all the enabled logging was sent to the loggers
        TestLogger testLogger = loggerFactory.getLogger("MyLogger");
        Assert.assertTrue(testLogger.matches("Hello World!"));
    }

    /**
     * This example demonstrates how to restrict which error messages get written on the console.
     * This is useful where we have tests that are expected to produce warnings such as deliberate failures.
     * In these cases we want to unclutter the console of things that we are expecting and avoid confusing the
     * dev's about which errors are expected and whioh are genuine faults.
     */
    @Test
    public void demoSuppressionOfConsoleLogging() throws Exception {
        /* Replace the error logger so we can check what actually went to system io.
        * This is not a good approach by the way as this has global impact on the JVM.
        * Using TestLogger or using a mock (subsequent example) is preferable but we'll do in this instance
        * as we're trying to prove a point about what would have been printed to the console during a test.
        */
        StringPrintStream ps = StringPrintStream.newStream();

        PrintStream orig = System.err;
        System.setErr(ps);

        ArrayList<TestLoggerFactory> testCases = new ArrayList<TestLoggerFactory>() {{
            // configure the logging impl to suppress some patterns of logging output
            add(Settings.instance()
                    .suppressPrinting("(?s).*Pattern to suppress.*")
                    .buildLogging());
            add(Settings.instance()
                    .suppressPrinting(Pattern.compile(".*Pattern to suppress.*", Pattern.DOTALL))
                    .buildLogging());

            Predicate<LogMessage> pred = new Predicate<LogMessage>() {
                Pattern pat = Pattern.compile("(?s).*Pattern to suppress.*");
                public boolean matches(LogMessage row) {
                    return pat.matcher(row.text).matches();
                }
            };

            add(Settings.instance()
                    .suppressPrinting(pred)
                    .buildLogging());
        }};

        try {
            for (int a = 0; a < testCases.size(); a++) {
                ps.clear();
                try {
                    TestLoggerFactory loggerFactory = testCases.get(a);
                    TestLogger logger = loggerFactory.getLogger(this.getClass());

                    // do some logging and verify that some of it was suppressed
                    logger.error("Should be printed");

                    // use multiline to demonstrate exclusion of multiline logging messages
                    logger.error("Pattern to suppress " + System.lineSeparator() +" << should not be printed");

                    // also make assertions about what would have been logged
                    assertContains(ps, "Should be printed");
                    assertNotContains(ps, "Pattern to suppress");
                } catch (Throwable ex) {
                    throw new AssertionError("failed test " + a, ex);
                }
            }
        } finally {
            System.setErr(orig);
        }
    }

    /**
     * In the previous examples we've seen logging captured using either the TestLogger or by replacing the system wide
     * logging using System.errOut/setErr. Sometimes however neither of these approaches is what we want and injecting
     * a mock logger is more useful.
     * <p>
     * This example uses mock to perform an "ordered" verification.
     */
    @Test
    public void demoDelegatingToMockito() throws Exception {
        // we'll hook the mock up to the logging framework
        Logger mockLogger = Mockito.mock(Logger.class);

        // setup the logging impl so that logging to the logger "MyLogger" is directed at the mock
        TestLoggerFactory loggerFactory = Settings.instance()
                .delegate("MyLogger", mockLogger).buildLogging();

        // do some work
        TestLogger logger = loggerFactory.getLogger("MyLogger");
        logger.info("Hello Johnny");
        logger.info("Hello Gracie");

        // check that the mock was called in the right order
        InOrder inOrder = Mockito.inOrder(mockLogger);
        inOrder.verify(mockLogger).info("Hello Johnny");
        inOrder.verify(mockLogger).info("Hello Gracie");
    }

    /**
     * If you want to have the "console" printing go to a specific PrintStream then you can choose what gets written
     * to which print stream.
     * Typically we might want to redirect all logging to out but the following example shows Error logging going to
     * stdout and Info logging going to stderr.
     */
    @Test
    public void demoAssociatingLogLevelsWithPrintStreams() throws Exception {
        // temporarily replace stderr/stdout so we can capture what would have been sent to the screen
        StringPrintStream outLog = StringPrintStream.newStream();
        PrintStream origOut = System.out;
        System.setOut(outLog);

        StringPrintStream errLog = StringPrintStream.newStream();
        PrintStream origErr = System.err;
        System.setErr(errLog);

        try {
            // capture info logging into a string stream so we can later verify what the print streams got sent
            TestLoggerFactory loggerFactory = Settings.instance()
                    .redirectPrintStream(LogLevel.ErrorLevel, System.out)
                    .redirectPrintStream(LogLevel.InfoLevel, System.err)
                    .enable(LogLevel.InfoLevel)
                    .buildLogging();

            TestLogger logger = loggerFactory.getLogger(this.getClass());
            logger.info("Info should be printed to Err stream");
            logger.error("Error should be printed to Out stream");

            // make assertions about what got logged
            assertContains(outLog, "Error should be printed to Out stream");
            assertContains(errLog, "Info should be printed to Err stream");

        } finally {
            System.setOut(origOut);
            System.setErr(origErr);
        }
    }

    // render nicer errors
    private void assertContains(StringPrintStream str, String expected) throws Error {
        if (!str.contains(expected)) {
            throw new AssertionError("expected '" + expected + "' but got '" + str.toString() + "'");
        }
    }

    // render nicer errors
    private void assertNotContains(StringPrintStream str, String expected) throws Error {
        if (str.contains(expected)) {
            throw new AssertionError("expected absence of '" + expected + "' but got '" + str.toString() + "'");
        }
    }
}