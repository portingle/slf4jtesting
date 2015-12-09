package slf4jtest.demos;

import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import slf4jtest.demos.GrandchildClass;

class ChildClass {
    private final Logger logger;
    private final GrandchildClass grandchild;

    ChildClass(ILoggerFactory lf) {
        this.logger = lf.getLogger(this.getClass().getName());
        grandchild = new GrandchildClass(lf);
    }

    public void doLogging() {
        logger.info("Hello from Child");
        grandchild.doLogging();
    }
}
