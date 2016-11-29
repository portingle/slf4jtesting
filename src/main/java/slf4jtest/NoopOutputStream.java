package slf4jtest;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

/** Potentially useful for log suppression; eg by creating something like "new PrintStream(NoopOutputStream.Instance)"
 * and passing that to a redirect.
 * */
public class NoopOutputStream extends OutputStream {
    static OutputStream Instance = new NoopOutputStream();

    /** use NoopOutputStream.Instance instead */
    private NoopOutputStream() {
    }

    @Override
    public void write(int b) throws IOException {
        // noop
    }
}
