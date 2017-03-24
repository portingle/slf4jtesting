package slf4jtest;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.regex.Pattern;

/** useful for capturing PrintStream output from the logging */
public class StringPrintStream extends PrintStream {

    private final ByteArrayOutputStream baos;

    /* prefer this approach */
    public static StringPrintStream newStream() {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        return new StringPrintStream(baos);
    }

    /* prefer use of newStream() */
    public StringPrintStream(ByteArrayOutputStream baos) {
        super(baos);
        this.baos = baos;
    }

    public String toString() {
        return baos.toString();
    }

    public boolean contains(String substring) {
        return toString().contains(substring);
    }

    /* does a Pattern.DOTALL match */
    public boolean matches(String regex) {
        Pattern pat = Pattern.compile(regex, Pattern.DOTALL);
        return matches(pat);
    }

    public boolean matches(Pattern sub) {
        return sub.matcher(toString()).matches();
    }

    public void clear() { baos.reset();}
}
