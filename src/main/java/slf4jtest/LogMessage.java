package slf4jtest;

import lombok.Data;

@Data
public class LogMessage {
    public final String logName;
    public final LogLevel level;
    public final String text;
    public final long timeStamp = System.currentTimeMillis();
    public final String threadName = Thread.currentThread().getName();

    public LogMessage(String logName, LogLevel level, String formattedMessage) {
        this.logName = logName;
        this.level = level;
        this.text = formattedMessage;
    }

    @Override
    public String toString() {
        return "LogMessage(" + logName + "," + level + "," + text + ")";
    }
}
