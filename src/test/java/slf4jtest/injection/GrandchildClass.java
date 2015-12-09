package slf4jtest.injection;

import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

class GrandchildClass {
    private final Logger logger;

    GrandchildClass(ILoggerFactory lf) {
        this.logger = lf.getLogger(this.getClass().getName());
    }

    public void doLogging() {
        logger.info("Hello from Grandchild");
    }
}
