package slf4jtest;

import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

public class AssemblyExample {

    private final ChildObject child;

    public AssemblyExample(ILoggerFactory lf) {
        child = new ChildObject(lf);
    }
}

class ChildObject {
    private final Logger logger;
    private final GrandchildObject grandchild;

    ChildObject(ILoggerFactory lf) {
        this.logger = lf.getLogger(AssemblyExample.class.getName());
        grandchild = new GrandchildObject(lf);
    }

    public void aMethodThatLogs() {
        logger.info("Hello World!");
    }
}

class GrandchildObject {
    private final Logger logger;

    GrandchildObject (ILoggerFactory lf) {
        this.logger = lf.getLogger(AssemblyExample.class.getName());
    }

    public void aMethodThatLogs() {
        logger.info("Bye World!");
    }
}
