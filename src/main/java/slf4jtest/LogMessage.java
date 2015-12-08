package slf4jtest;

class LogMessage {
    final LogLevel level;
    final String message;

    public LogMessage(LogLevel level, String message) {
        this.level = level;
        this.message = message;
    }

    @Override
    public String toString() {
        return "LogMessage(" + level + "," + message + ")";
    }
}
