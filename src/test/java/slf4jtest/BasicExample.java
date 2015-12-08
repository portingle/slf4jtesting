package slf4jtest;

import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

public class BasicExample {

    private final Logger logger;

    public BasicExample(ILoggerFactory lf) {
        this.logger = lf.getLogger(BasicExample.class.getName());
    }

    public void aMethodThatLogs() {
        logger.info("Hello World!");
    }
}
