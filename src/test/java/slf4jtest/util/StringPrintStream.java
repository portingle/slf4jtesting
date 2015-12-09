package slf4jtest.util;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

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
}
