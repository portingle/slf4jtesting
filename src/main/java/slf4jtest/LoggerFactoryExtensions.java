package slf4jtest;

import org.slf4j.ILoggerFactory;

public interface LoggerFactoryExtensions extends ILoggerFactory {

    public TestLogger getLogger(Class<?> name);

    public boolean loggerExists(String name);

    public boolean loggerExists(Class<?> name);

}

