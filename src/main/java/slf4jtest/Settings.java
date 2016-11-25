package slf4jtest;

import org.slf4j.Logger;

import java.io.PrintStream;
import java.util.*;

public class Settings {
    // controls whether printing to console occurs
    final boolean print;
    // override console print streams per log level
    final Map<LogLevel, PrintStream> printStreams;
    // suppressPrinting certain regexes from printing
    final List<String> printSuppressions;

    // determine value of isXXXXEnabled
    final List<LogLevel> enabledLevels;
    // push the call down onto a provided instance
    final Map<String, Logger> delegates;

    static PrintStream NoopPrintStream = new PrintStream(new NoopOutputStream());

    private Settings(boolean print,
                     Map<LogLevel, PrintStream> printStreams,
                     List<String> suppressionPatterns,
                     List<LogLevel> enabledLevels,
                     Map<String, Logger> delegates) {
        this.print = print;
        this.printStreams = readonlyMap(printStreams);
        this.printSuppressions = readonlyList(suppressionPatterns);
        this.enabledLevels = readonlyList(enabledLevels);
        this.delegates = readonlyMap(delegates);
    }

    public Settings() {
        print = true;
        printStreams = readonlyMap(new HashMap<LogLevel, PrintStream>() {
            {
                for (LogLevel l : LogLevel.All) {
                    put(l, NoopPrintStream);
                }
                put(LogLevel.ErrorLevel, System.err);
            }
        });

        printSuppressions = readonlyList();
        enabledLevels = readonlyList(new ArrayList<LogLevel>() {{
            add(LogLevel.ErrorLevel);
        }});

        delegates = readonlyMap();
    }

    public Settings printingEnabled(boolean print) {
        return new Settings(print, printStreams, printSuppressions, enabledLevels, delegates);
    }

    public Settings suppressPrinting(String regex) {
        List<String> newSuppressions = new ArrayList<>(printSuppressions);
        newSuppressions.add(regex);

        return new Settings(print, printStreams, newSuppressions, enabledLevels, delegates);
    }

    public Settings enable(LogLevel level) {
        List<LogLevel> newLevels = new ArrayList<>(enabledLevels);
        newLevels.add(level);

        return new Settings(print, printStreams, printSuppressions, newLevels, delegates);
    }

    public Settings disable(LogLevel level) {
        List<LogLevel> newLevels = new ArrayList<>(enabledLevels);
        newLevels.remove(level);

        return new Settings(print, printStreams, printSuppressions, newLevels, delegates);
    }

    /**
     * <pre>
     * // setup a buffer to capture output
     * ByteArrayOutputStream baos = new ByteArrayOutputStream();
     * PrintStream ps = new PrintStream(baos);
     *
     * Settings settings = new Settings().associatePrintStream(LogLevel.ErrorLevel, ps);
     * TestLoggerFactory f = new TestLoggerFactory(settings);
     *
     * // run some code using f
     *
     * // assert logging was captured
     * assert(baos.toString().contains("bang"));
     *</pre>
     * */
    public Settings associatePrintStream(LogLevel level, PrintStream ps) {
        Map<LogLevel, PrintStream> newPrintStreams = new HashMap<>(printStreams);
        newPrintStreams.put(level, ps);

        return new Settings(print, newPrintStreams, printSuppressions, enabledLevels, delegates);
    }

    public Settings delegates(Map<String, Logger> delegates) {
        return new Settings(print, printStreams, printSuppressions, enabledLevels, delegates);
    }

    public Settings delegate(Class<?> loggerName, Logger logger) {
        return delegate(loggerName.getName(), logger);
    }

    public Settings delegate(String loggerName, Logger logger) {
        Map<String, Logger> newDelegates = new HashMap<>(delegates);
        newDelegates.put(loggerName, logger);

        return new Settings(print, printStreams, printSuppressions, enabledLevels, newDelegates);
    }

    private static <a,b> Map<a,b> readonlyMap(){
        return Collections.unmodifiableMap(new HashMap<a,b>());
    }
    private static <a,b> Map<a,b> readonlyMap(Map<a,b> map){
        return Collections.unmodifiableMap(map);
    }
    private static <a> List<a> readonlyList(){
        return Collections.unmodifiableList(new ArrayList<a>());
    }
    private static <a> List<a> readonlyList(List<a> list){
        return Collections.unmodifiableList(list);
    }
}
