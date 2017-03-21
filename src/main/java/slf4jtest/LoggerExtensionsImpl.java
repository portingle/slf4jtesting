package slf4jtest;

import java.io.PrintStream;
import java.util.Collection;
import java.util.Collections;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.regex.Pattern;

class LoggerExtensionsImpl implements LoggerExtensions {
    private final Settings settings;
    private final long startTime;
    private final Queue<LogMessage> rows = new ConcurrentLinkedQueue<>();

    LoggerExtensionsImpl(Settings settings, long startTime) {
        this.settings = settings;
        this.startTime = startTime;
    }

    void record(LogMessage message) {
        doLogging(message);
        doConsole(message);
    }

    private void doLogging(LogMessage message) {
        if (settings.isEnabled(message.level))
            rows.add(message);
    }

    private void doConsole(LogMessage message) {
        if (settings.isEnabled(message.level) && settings.printingEnabled && !isPrintSuppressed(message.text)) {
            PrintStream out = settings.printStreams.get(message.level);
            out.println(layout(message));
            out.flush();
        }
    }

    private String layout(LogMessage message) {
        long delta = message.timeStamp - startTime;
        return delta +
                " " + message.level +
                " [" + message.threadName + "] " +
                message.logName +
                " - " + message.text;
    }

    private boolean isPrintSuppressed(String msg) {
        for (String regex : settings.printSuppressions) {
            if (msg.matches(regex))
                return true;
        }
        return false;
    }

    public Collection<LogMessage> lines() {
        return Collections.unmodifiableCollection(rows);
    }

    /*
    * @deprecated "use matches(..)
    */
    public boolean contains(String regex) {
        return matches(regex);
    }

    public boolean matches(String regex) {
        for (LogMessage row : rows) {
            if (row.text.matches(regex))
                return true;
        }
        return false;
    }

    public boolean matches(Pattern regex) {
        for (LogMessage row : rows) {
            if (regex.matcher(row.text).matches())
                return true;
        }
        return false;
    }

    /*
    * @deprecated "use matches(..)
    */
    public boolean contains(LogLevel level, String regex) {
        return matches(level, regex);
    }

    public boolean matches(LogLevel level, String regex) {
        for (LogMessage row : rows) {
            if (row.level == level)
                if (row.text.matches(regex))
                    return true;
        }
        return false;
    }

    @Override
    public boolean matches(LogLevel level, Pattern regex) {
        for (LogMessage row : rows) {
            if (row.level == level)
                if (regex.matcher(row.text).matches())
                    return true;
        }
        return false;
    }

    public void clear() {
        rows.clear();
    }
}
