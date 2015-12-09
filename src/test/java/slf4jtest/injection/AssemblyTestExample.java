package slf4jtest.injection;

import org.junit.Test;
import slf4jtest.LogLevel;
import slf4jtest.Settings;
import slf4jtest.TestLoggerFactory;

public class AssemblyTestExample {
    @Test
    public void testDemoAssemblyInjection() {

        // turn on console logging of info level
        Settings settings = new Settings();
        TestLoggerFactory loggerFactory = new TestLoggerFactory(settings);

        // create the child component and its internal grandchild instance
        ChildClass child = new ChildClass(loggerFactory);
        child.doLogging();

        // check the logging
        assert(loggerFactory.contains("Hello from Child"));
        assert(loggerFactory.contains("Hello from Grandchild"));
    }
}
