package demos.Example2;

import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

/**
 * Assembly is assembly of classes using constructor injection of the logging implementation.
 */
class Example {
    private final Logger logger;
    private final InternalSubcomponent internalDetail;

    Example(ILoggerFactory lf) {
        this.logger = lf.getLogger(this.getClass().getName());
        internalDetail = new InternalSubcomponent(lf);
    }

    void doSomeInfoLogging() {
        logger.info("Hello from Assembly");
        internalDetail.doLogging();
    }
}

class InternalSubcomponent {
    private final Logger logger;

    InternalSubcomponent(ILoggerFactory lf) {
        this.logger = lf.getLogger(this.getClass().getName());
    }

    void doLogging() {
        logger.info("Hello from InternalSubcomponent");
    }
}
