package slf4jtest;

import junit.framework.TestCase;
import org.mockito.Mockito;
import org.slf4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class TestLoggerUnitTest extends TestCase {

    public void testByDefaultOnlyErrorIsPrinted() {

        Settings settings = new Settings();
        assertTrue(settings.printStreams.get(LogLevel.ErrorLevel) != Settings.NoopPrintStream);
        assertTrue(settings.printStreams.get(LogLevel.WarnLevel) == Settings.NoopPrintStream);
        assertTrue(settings.printStreams.get(LogLevel.InfoLevel) == Settings.NoopPrintStream);
        assertTrue(settings.printStreams.get(LogLevel.DebugLevel) == Settings.NoopPrintStream);
        assertTrue(settings.printStreams.get(LogLevel.TraceLevel) == Settings.NoopPrintStream);
    }

    public void testByPrintStreamCanBeOverridden() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);

        Settings settings = new Settings().associatePrintStream(LogLevel.ErrorLevel, ps);

        TestLoggerFactory f = new TestLoggerFactory(settings);
        TestLogger l = f.getLogger("john");
        l.error("bang");

        assert(baos.toString().contains("bang"));
        assert (l.contains("bang"));
    }

    public void testPrintingCanBeDisabledButLinesAreStillRecorded() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);

        Settings settings = new Settings().associatePrintStream(LogLevel.ErrorLevel, ps).printingEnabled(false);

        TestLoggerFactory f = new TestLoggerFactory(settings);
        TestLogger l = f.getLogger("john");
        l.error("bang");

        assert(!baos.toString().contains("bang"));
        assert (l.contains("bang"));
    }

    public void testErrorIsEnabledByDefault() {
        Settings settings = new Settings();
        TestLoggerFactory f = new TestLoggerFactory(settings);
        TestLogger l = f.getLogger("john");
        assert (l.isErrorEnabled());
    }
    public void testErrorCanBeDisabled() {
        Settings settings = new Settings().disable(LogLevel.ErrorLevel);
        TestLoggerFactory f = new TestLoggerFactory(settings);
        TestLogger l = f.getLogger("john");
        assert (!l.isErrorEnabled());
    }
    public void testWarnIsDisabledByDefault() {
        Settings settings = new Settings();
        TestLoggerFactory f = new TestLoggerFactory(settings);
        TestLogger l = f.getLogger("john");
        assert (!l.isWarnEnabled());
    }
    public void testWarnCanBeEnabled() {
        Settings settings = new Settings().enable(LogLevel.WarnLevel);
        TestLoggerFactory f = new TestLoggerFactory(settings);
        TestLogger l = f.getLogger("john");
        assert (l.isErrorEnabled());
    }

    public void testCanSuppressPrintingSelectivelyButStillGetLogged() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);

        Settings settings = new Settings()
                .associatePrintStream(LogLevel.ErrorLevel, ps)
                .suppressPrinting(".*suppressPrinting-me*");

        TestLoggerFactory f = new TestLoggerFactory(settings);
        TestLogger l = f.getLogger("john");

        String ShouldBeLogged = "printme";
        String ShouldBePrintSuppressed = "suppressPrinting-me";

        l.error(ShouldBeLogged);
        l.error(ShouldBePrintSuppressed);

        assert(baos.toString().contains(ShouldBeLogged));
        assert (l.contains(ShouldBeLogged));

        assert(!baos.toString().contains(ShouldBePrintSuppressed));
        assert (l.contains(ShouldBePrintSuppressed));
    }

    public void testDelegateToAMockingLibrary() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);

        Logger mockLogger = Mockito.mock(Logger.class);
        Settings settings = new Settings()
                .printingEnabled(false)
                .delegate("john", mockLogger);

        TestLoggerFactory f = new TestLoggerFactory(settings);
        TestLogger l = f.getLogger("john");

        l.error("bang");

        Mockito.verify(mockLogger).error("bang");
    }
}
