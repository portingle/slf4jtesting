package slf4jtest;

import java.util.Collection;
import java.util.regex.Pattern;

interface LoggerExtensions {

    /** access to the underlying detailed LogMessage objects */
    Collection<LogMessage> lines();

    /** verify that a regex matches the logging of some log level level*/
    boolean matches(String regex);

    /** verify that a regex matches the logging of some log level level*/
    boolean matches(Pattern regex);

    /** verify that a regex matches the logging for a specific log level */
    boolean matches(LogLevel level, String regex);

    /** verify that a regex matches the logging for a specific log level */
    boolean matches(LogLevel level, Pattern regex);

    /** verify that a regex matches the logging of some log level level.
     * @deprecated use matches
     */
    boolean contains(String regex);

    /** verify that a regex matches the logging for a specific log level.
     *  @deprecated use matches
     */
    boolean contains(LogLevel level, String regex);

    /** erase the captured logging */
    void clear();
}
