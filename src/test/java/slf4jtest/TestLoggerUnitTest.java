package slf4jtest;

import junit.framework.TestCase;
import org.mockito.Mockito;
import org.slf4j.Logger;

import java.io.PrintStream;

public class TestLoggerUnitTest extends TestCase {

    public void testErrorsGotoStderr() {
        Settings settings = Settings.instance();
        assertTrue(settings.printStreams.get(LogLevel.ErrorLevel) == System.err);
        assertTrue(settings.printStreams.get(LogLevel.WarnLevel) == System.out);
        assertTrue(settings.printStreams.get(LogLevel.InfoLevel) == System.out);
        assertTrue(settings.printStreams.get(LogLevel.DebugLevel) == System.out);
        assertTrue(settings.printStreams.get(LogLevel.TraceLevel) == System.out);
    }

    public void testByDefaultOnlyErrorIsEnabled() {
        Settings settings = Settings.instance();
        assertTrue(settings.isEnabled(LogLevel.ErrorLevel));
        assertTrue(!settings.isEnabled(LogLevel.WarnLevel));
        assertTrue(!settings.isEnabled(LogLevel.InfoLevel));
        assertTrue(!settings.isEnabled(LogLevel.DebugLevel));
        assertTrue(!settings.isEnabled(LogLevel.TraceLevel));
    }

    public void testCanEnableAll() {
        Settings settings = Settings.instance().enableAll();
        assertTrue(settings.isEnabled(LogLevel.ErrorLevel));
        assertTrue(settings.isEnabled(LogLevel.WarnLevel));
        assertTrue(settings.isEnabled(LogLevel.InfoLevel));
        assertTrue(settings.isEnabled(LogLevel.DebugLevel));
        assertTrue(settings.isEnabled(LogLevel.TraceLevel));
    }

    public void testCanDisableAll() {
        Settings settings = Settings.instance().enableAll().disableAll();
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

            assert (console.matches(".*anError.*"));
            assert (!console.matches(".*someInfo.*"));

            assert (log.matches(".*anError.*"));
            assert (!log.matches(".*someInfo.*"));
        } finally {
            System.setErr(old);
        }
    }

    public void testPrintStreamCanBeOverridden() {
        StringPrintStream ps = StringPrintStream.newStream();

        TestLoggerFactory f = Settings.instance()
                .redirectPrintStream(LogLevel.ErrorLevel, ps).buildLogging();

        TestLogger log = f.getLogger("john");
        log.error("anError");

        assert (ps.contains("anError"));
        assert (log.contains("anError"));
    }

    public void testAssociatedPrintingCanBeDisabledButLinesAreStillRecorded() {
        StringPrintStream ps = StringPrintStream.newStream();

        TestLoggerFactory loggerFactory = Settings.instance().
                printingEnabled(false).redirectPrintStream(LogLevel.ErrorLevel, ps).
                buildLogging();

        TestLogger log = loggerFactory.getLogger("john");

        log.error("anError");

        assert (!ps.contains("anError"));
        assert (log.contains("anError"));
    }

    public void testConsolePrintingCanBeDisabledButLinesAreStillRecorded() {
        StringPrintStream console = StringPrintStream.newStream();
        PrintStream old = System.err;
        System.setErr(console); // << have to interfere with the system for this test

        try {
            TestLoggerFactory f  = Settings.instance().printingEnabled(false).buildLogging();
            TestLogger log = f.getLogger("john");

            log.error("anError");

            assert (!console.contains("anError"));
            assert (log.contains("anError"));
        } finally {
            System.setErr(old);
        }
    }

    public void testErrorIsEnabledByDefault() {
        Settings settings = Settings.instance();
        TestLoggerFactory f = new TestLoggerFactory(settings);
        TestLogger log = f.getLogger("john");
        assert (log.isErrorEnabled());
    }

    public void testErrorCanBeDisabled() {
        TestLoggerFactory f = Settings.instance().disable(LogLevel.ErrorLevel).buildLogging();
        TestLogger log = f.getLogger("john");
        assert (!log.isErrorEnabled());
    }

    public void testWarnIsDisabledByDefault() {
        TestLoggerFactory f = Settings.instance().buildLogging();
        TestLogger log = f.getLogger("john");
        assert (!log.isWarnEnabled());
    }

    public void testWarnCanBeEnabled() {
        TestLoggerFactory f = Settings.instance().enable(LogLevel.WarnLevel).buildLogging();
        TestLogger log = f.getLogger("john");
        assert (log.isErrorEnabled());
    }

    public void testPrintSuppressionsAffectsPrintStreamAndNotLogging() {
        StringPrintStream ps = StringPrintStream.newStream();

        TestLoggerFactory f = Settings.instance()
                .redirectPrintStream(LogLevel.ErrorLevel, ps)
                .suppressPrinting(".*suppressPrinting-me.*")
                .buildLogging();

        TestLogger log = f.getLogger("john");

        String ShouldBeLogged = "printme";
        String ShouldBePrintSuppressed = "suppressPrinting-me <<" + System.lineSeparator() + " dont print";

        log.error(ShouldBeLogged);
        log.error(ShouldBePrintSuppressed);

        assert (ps.toString().contains(ShouldBeLogged));
        assert (log.contains(ShouldBeLogged));

        assert (!ps.toString().contains(ShouldBePrintSuppressed));
        assert (log.contains(ShouldBePrintSuppressed));
    }

    public void testDelegateToAMockingLibrary() {
        Logger mockLogger = Mockito.mock(Logger.class);
        TestLoggerFactory f = Settings.instance()
                .printingEnabled(false)
                .delegate("john", mockLogger)
                .buildLogging();

        TestLogger log = f.getLogger("john");

        log.error("anError");

        Mockito.verify(mockLogger).error("anError");
    }
}
