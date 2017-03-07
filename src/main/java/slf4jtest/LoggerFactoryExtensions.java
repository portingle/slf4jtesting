package slf4jtest;

import org.slf4j.ILoggerFactory;

public interface LoggerFactoryExtensions extends ILoggerFactory {

    /** get or create the given logger.
     * @param  name the SLF4J logger name.
     * @return the TestLogger
     */
    public TestLogger getLogger(Class<?> name);

    /** check but doesn't create the logger
     * @param  name the SLF4J logger name.
     * @return the TestLogger
    */
    public boolean loggerExists(String name);

    /** check but doesn't create the logger
     * @param  name the SLF4J logger name.
     * @return the TestLogger.
     */
    public boolean loggerExists(Class<?> name);

}

