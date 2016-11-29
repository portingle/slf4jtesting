package demos.Example1;

import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

/**
 * demonstrates ctor injection of the logging implementation
 */
class Example {

    private final Logger logger;

    public Example(ILoggerFactory lf) {
        this.logger = lf.getLogger(Example.class.getName());
    }

    public void doLogging() {
        logger.error("Hello World!");
    }
}
