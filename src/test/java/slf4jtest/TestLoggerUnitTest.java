package slf4jtest;

import junit.framework.TestCase;
import org.mockito.Mockito;
import org.slf4j.Logger;

import java.io.PrintStream;

public class TestLoggerUnitTest extends TestCase {

    public void testErrorsGotoStderr() {
        Settings settings = new Settings();
        assertTrue(settings.printStreams.get(LogLevel.ErrorLevel) == System.err);
        assertTrue(settings.printStreams.get(LogLevel.WarnLevel) == System.out);
        assertTrue(settings.printStreams.get(LogLevel.InfoLevel) == System.out);
        assertTrue(settings.printStreams.get(LogLevel.DebugLevel) == System.out);
        assertTrue(settings.printStreams.get(LogLevel.TraceLevel) == System.out);
    }

    public void testByDefaultOnlyErrorIsEnabled() {
        Settings settings = new Settings();
        assertTrue(settings.isEnabled(LogLevel.ErrorLevel));
        assertTrue(!settings.isEnabled(LogLevel.WarnLevel));
        assertTrue(!settings.isEnabled(LogLevel.InfoLevel));
        assertTrue(!settings.isEnabled(LogLevel.DebugLevel));
        assertTrue(!settings.isEnabled(LogLevel.TraceLevel));
    }

    public void testCanEnableAll() {
        Settings settings = new Settings().enableAll();
        assertTrue(settings.isEnabled(LogLevel.ErrorLevel));
        assertTrue(settings.isEnabled(LogLevel.WarnLevel));
        assertTrue(settings.isEnabled(LogLevel.InfoLevel));
        assertTrue(settings.isEnabled(LogLevel.DebugLevel));
        assertTrue(settings.isEnabled(LogLevel.TraceLevel));
    }

    public void testCanDisableAll() {
        Settings settings = new Settings().enableAll().disableAll();
        assertTrue(!settings.isEnabled(LogLevel.ErrorLevel));
        assertTrue(!settings.isEnabled(LogLevel.WarnLevel));
        assertTrue(!settings.isEnabled(LogLevel.InfoLevel));
        assertTrue(!settings.isEnabled(LogLevel.DebugLevel));
        assertTrue(!settings.isEnabled(LogLevel.TraceLevel));
    }

    public void testLoggingDefaults() {
        StringPrintStream console = StringPrintStream.newStream();
        PrintStream old = System.err;
        System.setErr(console);

        try {
            TestLoggerFactory f = new TestLoggerFactory();
            TestLogger log = f.getLogger("john");

            log.error("anError");
            log.info("someInfo");

            assert (console.contains("anError"));
            assert (!console.contains("someInfo"));

            assert (log.contains("anError"));
            assert (!log.contains("someInfo"));
        } finally {
            System.setErr(old);
        }
    }

    public void testPrintStreamCanBeOverridden() {
        StringPrintStream ps = StringPrintStream.newStream();

        Settings settings = new Settings().redirectPrintStream(LogLevel.ErrorLevel, ps);

        TestLoggerFactory f = new TestLoggerFactory(settings);
        TestLogger log = f.getLogger("john");
        log.error("anError");

        assert (ps.contains("anError"));
        assert (log.contains("anError"));
    }

    public void testAssociatedPrintingCanBeDisabledButLinesAreStillRecorded() {
        StringPrintStream ps = StringPrintStream.newStream();

        Settings settings = new Settings().printingEnabled(false).redirectPrintStream(LogLevel.ErrorLevel, ps);

        TestLoggerFactory f = new TestLoggerFactory(settings);
        TestLogger log = f.getLogger("john");

        log.error("anError");

        assert (!ps.contains("anError"));
        assert (log.contains("anError"));
    }

    public void testConsolePrintingCanBeDisabledButLinesAreStillRecorded() {
        StringPrintStream console = StringPrintStream.newStream();
        PrintStream old = System.err;
        System.setErr(console); // << have to interfere with the system for this test

        try {
            Settings s = new Settings().printingEnabled(false);
            TestLoggerFactory f = new TestLoggerFactory(s);
            TestLogger log = f.getLogger("john");

            log.error("anError");

            assert (!console.contains("anError"));
            assert (log.contains("anError"));
        } finally {
            System.setErr(old);
        }
    }

    public void testErrorIsEnabledByDefault() {
        Settings settings = new Settings();
        TestLoggerFactory f = new TestLoggerFactory(settings);
        TestLogger log = f.getLogger("john");
        assert (log.isErrorEnabled());
    }

    public void testErrorCanBeDisabled() {
        Settings settings = new Settings().disable(LogLevel.ErrorLevel);
        TestLoggerFactory f = new TestLoggerFactory(settings);
        TestLogger log = f.getLogger("john");
        assert (!log.isErrorEnabled());
    }

    public void testWarnIsDisabledByDefault() {
        Settings settings = new Settings();
        TestLoggerFactory f = new TestLoggerFactory(settings);
        TestLogger log = f.getLogger("john");
        assert (!log.isWarnEnabled());
    }

    public void testWarnCanBeEnabled() {
        Settings settings = new Settings().enable(LogLevel.WarnLevel);
        TestLoggerFactory f = new TestLoggerFactory(settings);
        TestLogger log = f.getLogger("john");
        assert (log.isErrorEnabled());
    }

    public void testPrintSuppressionsAffectsPrintStreamAndNotLogging() {
        StringPrintStream ps = StringPrintStream.newStream();

        Settings settings = new Settings()
                .redirectPrintStream(LogLevel.ErrorLevel, ps)
                .suppressPrinting(".*suppressPrinting-me*");

        TestLoggerFactory f = new TestLoggerFactory(settings);
        TestLogger log = f.getLogger("john");

        String ShouldBeLogged = "printme";
        String ShouldBePrintSuppressed = "suppressPrinting-me";

        log.error(ShouldBeLogged);
        log.error(ShouldBePrintSuppressed);

        assert (ps.toString().contains(ShouldBeLogged));
        assert (log.contains(ShouldBeLogged));

        assert (!ps.toString().contains(ShouldBePrintSuppressed));
        assert (log.contains(ShouldBePrintSuppressed));
    }

    public void testDelegateToAMockingLibrary() {
        Logger mockLogger = Mockito.mock(Logger.class);
        Settings settings = new Settings()
                .printingEnabled(false)
                .delegate("john", mockLogger);

        TestLoggerFactory f = new TestLoggerFactory(settings);
        TestLogger log = f.getLogger("john");

        log.error("anError");

        Mockito.verify(mockLogger).error("anError");
    }
}
