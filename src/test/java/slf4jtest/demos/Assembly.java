package slf4jtest.demos;

import org.slf4j.ILoggerFactory;
import org.slf4j.LoggerFactory;

/** demonstrate pattern of constructor injection via multiple construction layers */
public class Assembly {

    public static void main(String[] args) {

        // this won't log unless there is a slf4j impl on the path and a config.
        // by contrast look at AssemblyTestExample
        final ILoggerFactory loggerFactory = LoggerFactory.getILoggerFactory();

        ChildClass child = new ChildClass(loggerFactory);
        child.doLogging();
    }
}

