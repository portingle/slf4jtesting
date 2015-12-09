package slf4jtest.demos;

import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import slf4jtest.LogLevel;
import slf4jtest.Settings;
import slf4jtest.TestLogger;
import slf4jtest.TestLoggerFactory;
import slf4jtest.StringPrintStream;

public class OtherDemos {

    @Test
    public void testDemoLoggingToTheConsole() throws Exception {

        Settings cfg = new Settings().associatePrintStream(LogLevel.InfoLevel, System.out);
        TestLoggerFactory loggerFactory = new TestLoggerFactory(cfg);

        TestLogger logger = loggerFactory.getLogger(this.getClass());

        // no verification - expect to see some console logging
        logger.info("Hello World!");
    }

    @Test
    public void testDelegatingToMockito() throws Exception {
        // we'll hook the mock up to the logging framework
        Logger mockLogger = Mockito.mock(Logger.class);

        Settings cfg = new Settings().delegate(BasicExample.class, mockLogger);
        TestLoggerFactory loggerFactory = new TestLoggerFactory(cfg);

        BasicExample sut = new BasicExample(loggerFactory);
        sut.doLogging();

        Mockito.verify(mockLogger).info("Hello World!");
    }

    @Test
    public void testSuppressionOfLogPrinting() throws Exception {
        StringPrintStream ps = StringPrintStream.newStream();

        // capture info logging into a string stream so we can verify what the print streams get sent
        Settings cfg = new Settings()
                .associatePrintStream(LogLevel.InfoLevel, ps)
                .suppressPrinting(".*Pattern to suppress.*");

        TestLoggerFactory loggerFactory = new TestLoggerFactory(cfg);

        TestLogger logger = loggerFactory.getLogger(this.getClass());
        logger.info("Should be printed");
        logger.info("Pattern to suppress");

        assert(ps.toString().contains("Should be printed"));
        assert(!ps.toString().contains("Pattern to suppress"));
    }
}