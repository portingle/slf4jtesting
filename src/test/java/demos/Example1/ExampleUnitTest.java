package demos.Example1;

import org.junit.Test;
import slf4jtest.TestLogger;
import slf4jtest.TestLoggerFactory;

import static org.junit.Assert.assertTrue;

public class ExampleUnitTest {

    @Test
    public void testBasicDemo() throws Exception {
        TestLoggerFactory loggerFactory = new TestLoggerFactory();

        Example sut = new Example(loggerFactory);
        sut.doLogging();

        TestLogger logger = loggerFactory.getLogger(Example.class);
        assertTrue(logger.matches(".*Hello.*"));
    }
}