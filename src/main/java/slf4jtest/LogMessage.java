package slf4jtest;

class LogMessage {
    final String logName;
    final LogLevel level;
    final String text;
    final long timeStamp = System.currentTimeMillis();
    final String threadName = Thread.currentThread().getName();

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
