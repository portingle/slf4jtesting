package slf4jtest;

import org.slf4j.ILoggerFactory;

public interface LoggerFactoryExtensions extends ILoggerFactory {

    /** get or create the given logger  */
    public TestLogger getLogger(Class<?> name);

    /** check but doesn't create the logger */
    public boolean loggerExists(String name);

    /** check but doesn't create the logger */
    public boolean loggerExists(Class<?> name);

}

