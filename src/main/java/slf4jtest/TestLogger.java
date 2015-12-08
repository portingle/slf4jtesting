package slf4jtest;

import org.slf4j.Logger;

/* combined interface returned by the TestLoggerFactory but only instantiated virtually in the proxy invocation handler */
public interface TestLogger extends LoggerExtensions, Logger {
}
