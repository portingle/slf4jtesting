package slf4jtest;

import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Pattern;

public class TestLoggerFactory implements LoggerFactoryExtensions, LoggerExtensions {
    private final long startTime = System.currentTimeMillis();

    private final Settings settings;

    private final ConcurrentMap<String, TestLogger> loggers = new ConcurrentHashMap<>();

    public TestLoggerFactory(final Settings settings) {
        this.settings = settings;
    }

    public TestLoggerFactory() {
        this.settings = new Settings();
    }

    /**
     * check if a regex exists in a particular log level output.
     */
    public boolean contains(LogLevel level, String substring) {
        for (TestLogger l : loggers.values()) {
            if (l.contains(level, substring))
                return true;
        }
        return false;
    }

    /**
     * check if a substring exists within any of the loggers output
     *
     * @deprecated "use matches(..."
     */
    public boolean contains(String substring) {
        for (TestLogger l : loggers.values()) {
            if (l.matches(substring))
                return true;
        }
        return false;
    }

    /**
     * check if a regex exists in a particular log level output
     */
    public boolean matches(LogLevel level, String regex) {
        for (TestLogger l : loggers.values()) {
            if (l.matches(level, regex))
                return true;
        }
        return false;
    }

    @Override
    public Collection<LogMessage> lines() {
        ArrayList<LogMessage> lm = new ArrayList<>();

        for (TestLogger l : loggers.values()) {
            lm.addAll(l.lines());
        }
        return Collections.unmodifiableCollection(lm);
    }

    /**
     * check if a regex exists in any of the loggers output.
     * matches using Pattern.DOTALL
     */
    public boolean matches(String regex) {
        for (TestLogger l : loggers.values()) {
            if (l.matches(regex))
                return true;
        }
        return false;
    }

    /**
     * check if a regex exists in a particular log level output
     */
    public boolean matches(LogLevel level, Pattern regex) {
        for (TestLogger l : loggers.values()) {
            if (l.matches(level, regex))
                return true;
        }
        return false;
    }

    @Override
    public boolean assertMatches(Predicate<LogMessage> predicate) throws Error {
        boolean matched = matches(predicate);
        if (!matched) {
            throw new AssertionError("did not match " + predicate.toString());
        }
        return true;
    }

    /**
     * check if a regex exists in any of the loggers output
     */
    public boolean matches(Pattern regex) {
        for (TestLogger l : loggers.values()) {
            if (l.matches(regex))
                return true;
        }
        return false;
    }

    @Override
    public boolean matches(Predicate<LogMessage> predicate) {
        for (TestLogger l : loggers.values()) {
            if (l.matches(predicate))
                return true;
        }
        return false;
    }

    /**
     * clear all registered loggers
     */
    public void clear() {
        for (TestLogger l : loggers.values()) {
            l.clear();
        }
    }

    /**
     * get or create the logger
     */
    @Override
    public TestLogger getLogger(String name) {

        TestLogger cached = loggers.get(name);
        if (cached != null)
            return cached;

        TestLogger newLogger = createMock(settings, name);

        TestLogger oldLogger = loggers.putIfAbsent(name, newLogger);
        if (oldLogger != null) return oldLogger;
        return newLogger;
    }

    /**
     * get or create the logger
     */
    public TestLogger getLogger(Class<?> name) {
        return getLogger(name.getName());
    }

    /**
     * verify presence of logger
     */
    public boolean loggerExists(String name) {
        return loggers.containsKey(name);
    }

    /**
     * verify presence of logger
     */
    public boolean loggerExists(Class<?> name) {
        return loggers.containsKey(name.getName());
    }

    private final static Map<String, LogLevel> logFnNameToLogLevel = new HashMap<String, LogLevel>() {{
        this.put("error", LogLevel.ErrorLevel);
        this.put("warn", LogLevel.WarnLevel);
        this.put("info", LogLevel.InfoLevel);
        this.put("debug", LogLevel.DebugLevel);
        this.put("trace", LogLevel.TraceLevel);
    }};

    private final static Map<String, LogLevel> isEnabledFnNameToLogLevel = new HashMap<String, LogLevel>() {{
        this.put("isErrorEnabled", LogLevel.ErrorLevel);
        this.put("isWarnEnabled", LogLevel.WarnLevel);
        this.put("isInfoEnabled", LogLevel.InfoLevel);
        this.put("isDebugEnabled", LogLevel.DebugLevel);
        this.put("isTraceEnabled", LogLevel.TraceLevel);
    }};

    private TestLogger createMock(final Settings settings, final String logName) {

        InvocationHandler handler = new InvocationHandler() {
            LoggerExtensionsImpl extension = new LoggerExtensionsImpl(settings, startTime);

            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                String name = method.getName();

                Class<?> declaringClass = method.getDeclaringClass();
                if (declaringClass == LoggerExtensions.class) {
                    try {
                        return method.invoke(extension, args);
                    } catch (InvocationTargetException ite) {
                        throw ite.getCause();
                    }
                }

                try {
                    if ("getName".equals(name)) {
                        return logName;
                    }

                    if (isEnabledFnNameToLogLevel.containsKey(name)) {
                        LogLevel l = isEnabledFnNameToLogLevel.get(name);
                        return settings.enabledLevels.contains(l);
                    }

                    if (logFnNameToLogLevel.containsKey(name)) {
                        LogLevel l = logFnNameToLogLevel.get(name);
                        String s = formatLogMessage(method, args);
                        LogMessage message = new LogMessage(logName, l, s);
                        extension.record(message);
                    }

                    return null;
                } finally {
                    callDelegate(method, args);
                }
            }

            private void callDelegate(Method method, Object[] args) throws IllegalAccessException, InvocationTargetException {
                /* call the same method on the delegate.
                 typical usage would be to allow the user to supply a Mockito/EasyMock
                 as a delegate so that they can do some assertions or whatever.
                 thus ignore return value as this expected to be a verification mock.
                 no need to specify any expectation (when's) on that mock either.
                 */
                Logger delegate = settings.delegates.get(logName);
                if (delegate != null) method.invoke(delegate, args); // ignore return value ignore return value
            }
        };

        return (TestLogger) Proxy.newProxyInstance(
                Logger.class.getClassLoader(),
                new Class<?>[]{TestLogger.class},
                handler);
    }

    private String formatLogMessage(Method method, Object[] args) {
        Class<?>[] paramTypes = method.getParameterTypes();
        StringBuilder s = new StringBuilder();

        int p = 0;
        if (paramTypes[0] == Marker.class) {
            p++;
        }

        if (paramTypes.length - p == 1) {
            s.append(args[p].toString());
        } else {
            String format = args[p].toString();

            FormattingTuple ft;
            if (paramTypes[p + 1] == Object[].class) {
                ft = MessageFormatter.arrayFormat(format, (Object[]) args[p + 1]);
            } else {
                Object[] arr = new Object[args.length - 1 - p];
                for (int i = 0; i < args.length - p - 1; i++) {
                    arr[i] = args[i + 1 + p];
                }

                ft = MessageFormatter.arrayFormat(format, arr);
            }
            s.append(ft.getMessage());

            if (null != ft.getThrowable()) {
                StringWriter sw = new StringWriter();
                ft.getThrowable().printStackTrace(new PrintWriter(sw));
                s.append("\n");
                s.append(sw.toString());
            }

        }
        return s.toString();
    }
}