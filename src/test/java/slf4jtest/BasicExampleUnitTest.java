package slf4jtest;

import org.junit.Test;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

import static org.junit.Assert.assertTrue;

class BasicExample {

    private final Logger logger;

    public BasicExample(ILoggerFactory lf) {
        this.logger = lf.getLogger(BasicExample.class.getName());
    }

    public void doLogging() {
        logger.info("Hello World!");
    }
}

public class BasicExampleUnitTest {

    @Test
    public void testBasicDemo() throws Exception {
        TestLoggerFactory loggerFactory = new TestLoggerFactory();

        BasicExample sut = new BasicExample(loggerFactory);
        sut.doLogging();

        TestLogger logger = loggerFactory.getLogger(BasicExample.class);
        assertTrue(logger.contains(".*Hello.*"));
    }
}