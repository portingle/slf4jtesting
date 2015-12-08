package slf4jtest;

import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;

import static org.junit.Assert.assertTrue;

public class BasicExampleUnitTest {

    @Test
    public void testAMethodThatLogs() throws Exception {
        TestLoggerFactory loggerFactory = new TestLoggerFactory();

        BasicExample sut = new BasicExample(loggerFactory);
        sut.aMethodThatLogs();

        TestLogger logger = loggerFactory.getLogger(BasicExample.class.getName());
        assertTrue(logger.contains(".*Hello.*"));
    }

    @Test
    public void testAMethodThatLogsWithAMock() throws Exception {
        Logger mockLogger = Mockito.mock(Logger.class);

        Settings cfg = new Settings().delegate(BasicExample.class.getName(), mockLogger);
        TestLoggerFactory loggerFactory = new TestLoggerFactory(cfg);

        BasicExample sut = new BasicExample(loggerFactory);
        sut.aMethodThatLogs();

        Mockito.verify(mockLogger).info("Hello World!");
    }

    @Test
    public void testAMethodThatSuppresses() throws Exception {

        Settings cfg = new Settings().suppressPrinting(".*Pattern to suppress.*");
        TestLoggerFactory loggerFactory = new TestLoggerFactory(cfg);


    }
}