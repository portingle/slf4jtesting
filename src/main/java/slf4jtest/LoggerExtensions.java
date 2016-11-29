package slf4jtest;

import java.util.Collection;

interface LoggerExtensions {

    /** access to the underlying detailed LogMessage objects */
    Collection<LogMessage> lines();

    /** verify that a regex matches the logging of some log level level*/
    boolean contains(String regex);

    /** verify that a regex matches the logging for a specific log level */
    boolean contains(LogLevel level, String regex);

    /** erase the captured logging */
    void clear();
}
