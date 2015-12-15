# SLF4JTesting

SLF4JTesting provides facilities for log testing that work well in environents with concurrent test execution and where console logging is desired.

SLF4JTesting provides a "Testing Double" of SLF4J LoggerFactory to facilitate log testing but also as a utility
for use in unit and integration tests to conveniently configure logging on a test by test basis. SLF4JTesting 
does not rely on singletons or other constructs that might cause cross-test interference.

SLF4JTesting has been designed to:

- support unit and integration testing
- support concurrent test execution such as [TestNG parallel suites](http://testng.org/doc/documentation-main.html#parallel-suites) or [Scalatest parallel test execution](http://doc.scalatest.org/2.0/index.html#org.scalatest.ParallelTestExecution) 
- promote dependency injection of the logging implementation
- support testing of multi-threaded components
- support testing of assemblies
- provide a simple console logging facility
- allow selective suppression of individual console logging messages (eg to hide any expected errors)
- support integration with mocking libraries (eg Mockito)

SLF4JTesting differs from some other solutions because it:   

* does not pollute the classpath with any StaticLoggerBinder stuff
* does not compete with other SLF4J implementations resident on the classpath
* does not rely on cluttering the classpath with special logback.xml/logback-test.xml files
* does not use statics
* does not rely on singletons anywhere
* does not need thread-locals etc 

## Usage

### Setting up

Include the jar as a test dependency.

```
<dependency>
  <groupId>com.portingle</groupId>
  <artifactId>slf4jtesting</artifactId>
  <version>1.0.0</version>
  <scope>test</scope>
</dependency>
```

### Basic Example

Here we use constructor injection using the SLF4J logger factory interface.

```java
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

public class Example1 {

    private final Logger logger;

    public Example1(ILoggerFactory lf) {
        this.logger = lf.getLogger(Example1.class.getName());
    }

    public void aMethodThatLogs() {
        logger.info("Hello World!");
    }
}
```

The `.getName()` is a tiny bit of boilerplate that's only necessary because SLF4J's'
[ILoggerFactory](http://www.slf4j.org/api/org/slf4j/ILoggerFactory.html) interface does not provide a by-class convenience
method like [LoggerFactory](http://www.slf4j.org/api/org/slf4j/LoggerFactory.html) does.

Constructor injection is not that arduous, even less so if you use a dependecy injection framework, but it provides the seam that allows us to
inject out testing double.

```java
import org.junit.Test;
import static org.junit.Assert.assertTrue;

public class Example1UnitTest {

    @Test
    public void testAMethodThatLogs() throws Exception {
        TestLoggerFactory loggerFactory = new TestLoggerFactory();

        Example1 sut = new Example1(loggerFactory);
        sut.aMethodThatLogs();

        TestLogger logger = loggerFactory.getLogger(Example1.class.getName());
        assertTrue(logger.contains(".*Hello.*"));
    }
}
```


## Motivation

### Improved construction patterns and testability

I strongly believe in dependency injection (DI) so anything that looks like a static or singleton offends me,
statics and singletons are an impediment to testable code.

We can't conveniently substitute statics for during testing and in the case of SLF4J we end up resorting to
nasties like classpath manipulation to get over this.

Anything global or static leaks information between tests and typically requires before and after setup
and tear down which is more test boilerplate.
This pattern means that running tests classes or class methods concurrently leads to interference as all threads interact with the
shared mutable global.

These problems can be avoided if we do not permit this share state between tests and instead use POJO's and DI; this library attempts
to make it easy to use a POJO/DI pattern with SLF4J and write tests for that code.

### Console logging during tests

Typically we don't want logging going to the console in tests. A possible exception to this is that it's useful
if any unexpected errors are logged to the console. Sometimes however we are performing negative tests and we expect
certain error messages but we do not want to see these on the console.

SLF4JTesting supports optional console logging but also a regex based scheme to suppress certain expected log lines.
This helps us keep our CI console clean without losing the benefits of logging of unexpected conditions that would help
us diagnose failing tests more easily.

### Multithreaded code

Sometimes we have an integration test where the test subject performs logging from another thread than the foreground test thread.
SLF4JTesting collects all logging occurring in the test subject regardless of thread.

### Assembly integration testing

Sometimes we want to test an assembly where the assembly constructs objects internally.
If each parent object is injected with the logger factory and any objects the parent creates are also injected with the
 logger factory then this pattern allows the entire logging implementation to be substituted in tests.

 ```java
 import org.slf4j.ILoggerFactory;
 import org.slf4j.Logger;

 public class AssemblyExample {

     private final ChildObject child;

     public AssemblyExample(ILoggerFactory lf) {
         child = new ChildObject(lf);
     }
 }

 class ChildObject {
     private final Logger logger;
     private final GrandchildObject grandchild;

     ChildObject(ILoggerFactory lf) {
         logger = lf.getLogger(AssemblyExample.class.getName());
         grandchild = new GrandchildObject(lf);
     }

     public void aMethodThatLogs() {
         logger.info("Hello World!");
     }
 }

 class GrandchildObject {
     private final Logger logger;

     GrandchildObject (ILoggerFactory lf) {
         logger = lf.getLogger(AssemblyExample.class.getName());
     }

     public void aMethodThatLogs() {
         logger.info("Bye World!");
     }
 }
 ```

### Making assertions

It is possible to make some assertions about what was logged by using methods provided by   TestLogger`.`

```
TestLogger logger = loggerFactory.getLogger(Example1.class.getName());
assertTrue(logger.contains(".*Hello.*"));
```

### Mocking

You can make further assertions by using a mocking framework.

SLF4JLogging allows you to hook a mock (eg Mockito or other) up to the logging implementation so that
 you can use the power of your chosen mocking framework directly to test the logging.

SLF4J logging does not impose any particular mocking framework.

```java
public class MockingExampleUnitTest {
    @Test
    public void testAMethodThatLogsWithAMock() throws Exception {
        Logger mockLogger = Mockito.mock(Logger.class);

        Settings cfg = new Settings().delegate(BasicExample.class.getName(), mockLogger);
        TestLoggerFactory loggerFactory = new TestLoggerFactory(cfg);

        BasicExample sut = new BasicExample(loggerFactory);
        sut.aMethodThatLogs();

        Mockito.verify(mockLogger).info("Hello World!");
    }
}
```

### Locally configurabe console output & logging suppression

Logging in tests can be controlled on a test by test basis without classpath fun and games.

It is also possible to selectively suppress the console logging using regular expressions. This is particularly useful
on those occasions where you want console logging turned on in a test (eg for diagnostics) but you do not want error logging
caused by deliberate failures to clog the build console. In my experience this is most often around integration testing.

```
Settings cfg = new Settings().suppressPrinting(".*Pattern to suppress.*");
TestLoggerFactory loggerFactory = new TestLoggerFactory(cfg);

logger.error("Pattern to suppress - will not be printed");
logger.error("This will be printed");
```
