package demos.Example2;

import org.junit.Test;
import slf4jtest.LogLevel;
import slf4jtest.Settings;
import slf4jtest.TestLoggerFactory;

/**
 * This example demonstrates constructing a nested assembly, ie the class Example, and testing it's logging
 */
public class ExampleUnitTest {
    @Test
    public void testDemoAssemblyInjection() {

        // enable info logging because only error is enabled by default
        TestLoggerFactory loggingImpl = Settings.instance().
                enable(LogLevel.InfoLevel).buildLogging();

        // create the Assembly component along with its internal internalDetail instance
        Example assembly = new Example(loggingImpl);
        assembly.doSomeInfoLogging();

        // check the Info logging
        assert (loggingImpl.matches("Hello from Assembly"));
        assert (loggingImpl.matches("Hello from InternalSubcomponent"));
    }
}

