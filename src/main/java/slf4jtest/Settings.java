package slf4jtest;

import org.slf4j.Logger;

import java.io.PrintStream;
import java.util.*;

public class Settings {
    // controls whether printing to console occurs
    final boolean printingEnabled;
    // override console print streams per log level
    final Map<LogLevel, PrintStream> printStreams;
    // suppressPrinting certain regexes from printing
    final List<String> printSuppressions;

    // determine value of isXXXXEnabled
    final Set<LogLevel> enabledLevels;
    // push the call down onto a provided instance
    final Map<String, Logger> delegates;

    private Settings(boolean print,
                     Map<LogLevel, PrintStream> printStreams,
                     List<String> suppressionPatterns,
                     Set<LogLevel> enabledLevels,
                     Map<String, Logger> delegates) {
        this.printingEnabled = print;
        this.printStreams = readonlyMap(printStreams);
        this.printSuppressions = readonlyList(suppressionPatterns);
        this.enabledLevels = readonlySet(enabledLevels);
        this.delegates = readonlyMap(delegates);
    }

    public Settings() {
        printingEnabled = true;

        printStreams = readonlyMap(new HashMap<LogLevel, PrintStream>() {
            {
                for (LogLevel l : LogLevel.All) {
                    put(l, System.out);
                }
                put(LogLevel.ErrorLevel, System.err);
            }
        });

        printSuppressions = readonlyList();
        enabledLevels = readonlySet(new HashSet<LogLevel>() {{
            add(LogLevel.ErrorLevel);
        }});

        delegates = readonlyMap();
    }

    public static Settings instance() {
        return new Settings();
    }

    public TestLoggerFactory buildLogging() {
        return new TestLoggerFactory(this);
    }

    public Settings printingEnabled(boolean print) {
        return new Settings(print, printStreams, printSuppressions, enabledLevels, delegates);
    }

    public Settings suppressPrinting(String regex) {
        List<String> newSuppressions = new ArrayList<>(printSuppressions);
        newSuppressions.add(regex);

        return new Settings(printingEnabled, printStreams, newSuppressions, enabledLevels, delegates);
    }

    public Settings enableAll() {
        return enable(LogLevel.All.toArray(new LogLevel[0]));
    }

    public Settings disableAll() {
        return disable(LogLevel.All.toArray(new LogLevel[0]));
    }

    public Settings enable(LogLevel... levels) {
        Set<LogLevel> newLevels = new HashSet<>(enabledLevels);
        newLevels.addAll(Arrays.asList(levels));

        return new Settings(printingEnabled, printStreams, printSuppressions, newLevels, delegates);
    }

    public Settings disable(LogLevel... levels) {
        Set<LogLevel> newLevels = new HashSet<>(enabledLevels);
        newLevels.removeAll(Arrays.asList(levels));

        return new Settings(printingEnabled, printStreams, printSuppressions, newLevels, delegates);
    }

    /*
     * <pre>
     * // setup a buffer to capture output
     * StringPrintStream console = StringPrintStream.newStream();
     *
     * Settings settings = new Settings().redirectPrintStream(LogLevel.ErrorLevel, console);
     * TestLoggerFactory f = new TestLoggerFactory(settings);
     *
     * // run some code using 'f' that logs 'someString'
     *
     * // assert logging was emitted
     * assert(console.contains("someString"));
     *</pre>
     * */
    public Settings redirectPrintStream(LogLevel level, PrintStream ps) {
        Map<LogLevel, PrintStream> newPrintStreams = new HashMap<>(printStreams);
        newPrintStreams.put(level, ps);

        return new Settings(printingEnabled, newPrintStreams, printSuppressions, enabledLevels, delegates);
    }

    /*
     * Provide a delegate to where all logging will be sent.
     * None of the supression or log level facilites of Settings apply to this delegate; it gets everything.
     *
     * This is typically used for injecting a mock object into the logging chain.
     * One can them make assertions on the mock.
     * */
    public Settings delegate(Class<?> loggerName, Logger logger) {
        return delegate(loggerName.getName(), logger);
    }

    public Settings delegate(String loggerName, Logger logger) {
        Map<String, Logger> newDelegates = new HashMap<>(delegates);
        newDelegates.put(loggerName, logger);

        return new Settings(printingEnabled, printStreams, printSuppressions, enabledLevels, newDelegates);
    }

    /* true if the given level is enabled in these settings */
    public boolean isEnabled(LogLevel level) {
        return enabledLevels.contains(level);
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
    private static <a> Set<a> readonlySet(Set<a> set){
        return Collections.unmodifiableSet(set);
    }
}
