# Java Log Sanitizer

This library can be used to intercept "suspicious" data before it is logged and then mask the data to make it less useful to an attacker.
For example it can identify and mask Credit Card number or IBANS.
 
The masking is meant to keep the logging useful but not useful enough for an attacker or a casual observer.
This project was inspired by the data breaches at Github and Twitter that appear to be the result of logging. 
Traditionally code reviews are the mechanism meant to catch errors but these are not foolproof and security should be multi-layered. 
This library can never be perfect and is meant to be an addition to, **NOT** a replacement for, secure development and operational practices. 
It supports logback. It will be expanded to log4j2 and log4j in future but these are not yet tested. 

## Status
Currently running in production environments. 

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. See deployment for notes on how to deploy the project on a live system.

### Prerequisites

You must be using logback (or in future log4j2 or log4j). A wrapper like commons-logging of Slf4j is OK as long as the underlying logger is supported. 
No other logging frameworks are currently supported.

### Dependencies
There are few dependencies and they are only needed at runtime. The library plugs into the logging frameworks and so there is no direct dependency.

* log-sanitizer-core depends on Jackson 2.9 for JSON parsing. If you use the JSON sanitizer you need to make sure that Jackson 2.9.X is available in the classpath.
* commons-lang3 is needed for most most sanitizers.
* [log4j2]:  runtime dependency with log-sanitizer-log4j2 
* [logback]: runtime dependency with log-sanitizer-logback 


## Installing

The easiest way to use log-sanitizer is with Maven or Gradle.

## Logback
In Logback a new MessageConverter with a "conversionWord" and then used as part of a pattern.
For example: LogbackDefaultSanitizer defined as "sanitizedMessage"

```xml
<conversionRule conversionWord="sanitizedMessage" converterClass="be.sysa.log.sanitize.logback.LogbackDefaultSanitizer" />
...
    <pattern>%-4relative [%thread] %-5level %logger{35} - %sanitizedMessage %n</pattern>
```
## Performance
The will be a perfromance penalty using the Sanitizers. It is up to you to determine if you are willing to accept the penalty.
You do not need to use all sanitizers.

This is the output from `jmh` which is used to do the microbenchmarks.
It is from an old iMac I7 from 2009. Units are operations per second.
``` 
Benchmark                  Mode  Cnt       Score   Error  Units
MyBenchmark.testBase64    thrpt    2   21359.898          ops/s
MyBenchmark.testIban      thrpt    2   89887.605          ops/s
MyBenchmark.testJson      thrpt    2   74777.634          ops/s
MyBenchmark.testPan       thrpt    2  125183.180          ops/s
MyBenchmark.testToString  thrpt    2   28615.992          ops/s
MyBenchmark.testUuid      thrpt    2  153878.500          ops/s
``` 
### Using the libraries with logback 

##### Maven:
```xml
<dependency>
    <groupId>be.sysa.log-sanitizer</groupId>
    <artifactId>log-sanitizer-core</artifactId>
    <version>2.0.0</version>
</dependency>
<dependency>
    <groupId>be.sysa.log-sanitizer</groupId>
    <artifactId>log-sanitizer-logback</artifactId>
    <version>2.0.0</version>
</dependency>
```
##### Gradle:
```gradle
compile group: 'be.sysa', name: 'log-sanitizer-core', version: '1.0.5'
compile group: 'be.sysa', name: 'log-sanitizer-logback', version: '1.0.5'
```

## Log4J2 (works but not fully tested and not yet released to maven central)

Log4j2 implements a rewrite appender that is able to delegate to a real appender. In the example below we are delegating to console but typically it would be to a file appender.
Within the rewrite appender the <MySanitizer/> element corresponds to the name attribute of a @org.apache.logging.log4j.core.config.plugins.Plugin annotation.

The log-sanitizer-log4j2 dependency only supplies one class: Log4J2DefaultSanitizer that will apply all of the default sanitizers to a log message.
However you can just copy the class and use that as the basis for your custom Sanitizer. 
Copying the class also means that the log4j-core dependency (and specified version) is not included. 

```java
@Plugin(name = "MySanitizer", category = "Core", elementType = "rewritePolicy", printObject = true)
public final class MyLog4j2Sanitizer implements RewritePolicy {
```
#### Configuration
```xml
<?xml version="1.0" encoding="UTF-8"?>
<Configuration>
    <Appenders>
        <Console name="Console" target="SYSTEM_OUT">
            <PatternLayout pattern="%d{HH:mm:ss.SSS} [%t] %-5level %logger{36} - %msg%n"/>
        </Console>
        <Rewrite name="Rewrite">
            <MySanitizer/>
            <AppenderRef ref="Console" />
        </Rewrite>
    </Appenders>
    <Loggers>
        <Root level="info">
            <AppenderRef ref="Rewrite" />
        </Root>
    </Loggers>
</Configuration>
```

##### Maven:
```xml
<dependency>
    <groupId>be.sysa.log-sanitizer</groupId>
    <artifactId>log-sanitizer-core</artifactId>
    <version>2.0.0</version>
</dependency>
<dependency>
    <groupId>be.sysa.log-sanitizer</groupId>
    <artifactId>log-sanitizer-log4j2</artifactId>
    <version>2.0.0</version>
</dependency>
```
##### Gradle:
```gradle
compile group: 'be.sysa', name: 'log-sanitizer-core', version: '2.0.0'
compile group: 'be.sysa', name: 'log-sanitizer-log4j2', version: '2.0.0'
```

# Additional ways to avoid logging secrets
* toString methods (including lombok @Data @Value, @ToString) can leak data. Always create a toString method with @Override and label 
  the excluded data as <hidden> or another label. 
* Annotating secret data can highlight to reviewers/maintainers that there is secret data (and so should not be logged)
* Verifying secret data is not part of toString. Unit tests (perhaps scanning and constructing objects dynamically) are best for this.
* preventing ssl logging in production: Consider checking if "javax.net.debug" is set (in production) and if so fail startup.
* avoiding debug in production: Debug should be off in production. With debug many more toString methods are called (perhaps in thirdparty libs) 
  and sensitive data can be logged.

# Development

## Running the tests

./mvnw clean verify

## Running the JMH benchmarks

./mvnw clean verify

## Caveats and Limitations
The Logback sanitizer Has been used in production in a PCI-DSS compliant system for almost 1 year. 

## Contributing

Please read [CONTRIBUTING.md](https://gist.github.com/PurpleBooth/b24679402957c63ec426) for details on our code of conduct, and the process for submitting pull requests to us.

## Versioning

We use [SemVer](http://semver.org/) for versioning. For the versions available, see the [tags on this repository](https://github.com/your/project/tags). 

## Authors

* **Mark Jeffrey** 


## License

This project is licensed under the Apache 2.0 License - see the [LICENSE.txt](LICENSE.txt) file for details

## Acknowledgments
