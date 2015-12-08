package slf4jtest;

import java.io.PrintStream;
import java.util.Collection;
import java.util.Collections;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

class LoggerExtensionsImpl implements LoggerExtensions {
    private final Settings settings;
    private final Queue<LogMessage> rows = new ConcurrentLinkedQueue<>();

    public LoggerExtensionsImpl(Settings settings) {
        this.settings = settings;
    }

    public void record(LogLevel level, String msg) {
        rows.add(new LogMessage(level, msg));
        if (settings.print && !isSuppressed(msg)) {
            PrintStream out = settings.printStreams.get(level);
            out.println(level + ": " + msg);
            out.flush();
        }
    }

    private boolean isSuppressed(String msg) {
        for (String regex : settings.printSuppressions) {
            if (msg.matches(regex)) return true;
        }
        return false;
    }

    public Collection<LogMessage> lines() {
        return Collections.unmodifiableCollection(rows);
    }

    public boolean contains(String regex) {
        for (LogMessage row : rows) {
            if (row.message.matches(regex)) return true;
        }
        return false;
    }

    public boolean contains(LogLevel level, String regex) {
        for (LogMessage row : rows) {
            if (row.level == level && row.message.matches(regex)) return true;
        }
        return false;
    }

    public void clear() {
        rows.clear();
    }
}
