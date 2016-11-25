package slf4jtest;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/** useful for capturing PrintStream output from the logging */
public class StringPrintStream extends PrintStream {

    private final ByteArrayOutputStream baos;

    public StringPrintStream(ByteArrayOutputStream baos) {
        super(baos);
        this.baos = baos;
    }

    public static StringPrintStream newStream() {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        return new StringPrintStream(baos);
    }

    public String toString() {
        return baos.toString();
    }

    public boolean contains(String sub) {
        return toString().contains(sub);
    }
}
