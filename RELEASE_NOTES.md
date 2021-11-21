# RELEASE NOTES

- 1.2.0

  - LogMessage is now JavaBeen compliant to ease use with Hamcrest. 
  ```java
   Matcher<Class> matcherName = allOf(
   hasProperty("level", equalTo(LogLevel.ERROR)),
   hasProperty("text", equalTo("Some message")));
   ```


- 1.1.3

  - added StringPrintStream.clear()
  - for convenience "matches(String)" now uses Pattern.DOTALL style matching as this is typically what folk want
  - use "match(Pattern)" if you want to fine tune
  - whereas "contains(String)" methods use "String.contains" behaviour

- 1.1.2

  - Added matching against Predicate

- 1.1.1

  - LogMessage is now public
  - matches() now supports Pattern allowing more powerful pattern matches
  - TestLoggerFactory now extends TestLoggerExtensions as a convenience for matching across all loggers. 

- 1.1.0

  - Upgrade dependency 'org.slf4j:slf4j-api:1.7.5' to "1.7.24"

- 1.0.2

  - deprecated TestLogger.contains in favour of TestLogger.matches (Thanks rolandio)
  - added TestLoggerFactory.clear() to clear all registered loggers (Thanks rolandio)

- 1.0.1

  - added build dependency on classpathHell - and corrected the test classpath issues this highlighted
  - moved to java 8

- 1.0.0

  - initial release

