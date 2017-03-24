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
        boolean levelIsEnabled = settings.isEnabled(message.level);
        boolean messageIsntSuppressed = !isPrintSuppressed(message);

        if (levelIsEnabled && settings.printingEnabled && messageIsntSuppressed) {
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

    private boolean isPrintSuppressed(LogMessage msg) {
        for (Predicate<LogMessage> predicate : settings.printSuppressions) {
            if (predicate.matches(msg))
                return true;
        }
        return false;
    }

    public Collection<LogMessage> lines() {
        return Collections.unmodifiableCollection(rows);
    }

    /*
    * does a String.contains(String) style comparison
    */
    public boolean contains(String substring) {
        return matches(new Predicate<LogMessage>() {
            public boolean matches(LogMessage lm) {
                return lm.text.contains(substring);
            }
        });
    }

    public boolean contains(LogLevel level, String substring) {
        return matches(new Predicate<LogMessage>() {
            public boolean matches(LogMessage lm) {
                return lm.text.contains(substring) && lm.level == level;
            }
        });
    }

    public boolean matches(final String regex) {
        Pattern pat = Pattern.compile(regex, Pattern.DOTALL);
        return matches(pat);
    }

    public boolean matches(final Pattern regex) {
        return matches(new Predicate<LogMessage>() {
            public boolean matches(LogMessage lm) {
                return regex.matcher(lm.text).matches();
            }
        });
    }

    public boolean matches(final LogLevel level, final String regex) {
        Pattern pat = Pattern.compile(regex, Pattern.DOTALL);
        return matches(level, pat);
    }

    @Override
    public boolean matches(final LogLevel level, final Pattern regex) {
        return matches(new Predicate<LogMessage>() {
            public boolean matches(LogMessage row) {
                return row.level == level &&regex.matcher(row.text).matches();
            }
        });
    }

    @Override
    public boolean matches(Predicate<LogMessage> predicate) {
        for (LogMessage row : rows) {
                if (predicate.matches(row))
                    return true;
        }
        return false;
    }

    public boolean assertMatches(Predicate<LogMessage> predicate) throws Error {
        boolean matched = matches(predicate);
        if (!matched) {
            throw new AssertionError("did not match " + predicate.toString());
        }
        return true;
    }

    public void clear() {
        rows.clear();
    }
}
