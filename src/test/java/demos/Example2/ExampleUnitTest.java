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
        Settings settings = new Settings().enable(LogLevel.InfoLevel);
        TestLoggerFactory loggingImpl = new TestLoggerFactory(settings);

        // create the Assembly component along with its internal internalDetail instance
        Example assembly = new Example(loggingImpl);
        assembly.doSomeInfoLogging();

        // check the Info logging
        assert (loggingImpl.contains("Hello from Assembly"));
        assert (loggingImpl.contains("Hello from InternalSubcomponent"));
    }
}

