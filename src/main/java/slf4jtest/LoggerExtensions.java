package slf4jtest;

import java.util.Collection;

interface LoggerExtensions {

    Collection<LogMessage> lines();

    boolean contains(String f);

    boolean contains(LogLevel level, String f);

    void clear();
}
