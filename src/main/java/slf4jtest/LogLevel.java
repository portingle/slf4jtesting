package slf4jtest;

import java.util.ArrayList;
import java.util.List;

public abstract class LogLevel {

    private LogLevel() {
    }

    public static final LogLevel ErrorLevel = new LogLevel() {
        @Override
        public String toString() {
            return "ERROR";
        }
    };
    public static final LogLevel WarnLevel = new LogLevel() {
        @Override
        public String toString() {
            return "WARN";
        }
    };
    public static final LogLevel InfoLevel = new LogLevel() {
        @Override
        public String toString() {
            return "INFO";
        }
    };
    public static final LogLevel DebugLevel = new LogLevel() {
        @Override
        public String toString() {
            return "DEBUG";
        }
    };
    public static final LogLevel TraceLevel = new LogLevel() {
        @Override
        public String toString() {
            return "TRACE";
        }
    };

    public static final LogLevel from(String name) {
        if (name.equals("error")) return ErrorLevel;
        if (name.equals("warn")) return WarnLevel;
        if (name.equals("info")) return InfoLevel;
        if (name.equals("debug")) return DebugLevel;
        if (name.equals("trace")) return TraceLevel;
        throw new IllegalArgumentException("invalid slf4jtest.LogLevel " + name);
    }

    public static final List<LogLevel> All = new ArrayList<LogLevel>() {{
        add(ErrorLevel);
        add(WarnLevel);
        add(InfoLevel);
        add(DebugLevel);
        add(TraceLevel);
    }};
}


