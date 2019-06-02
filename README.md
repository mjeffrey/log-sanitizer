# Java Log Sanitizer

This library can be used to intercept "suspicious" data before it is logged and then mask the data to make it less useful to an attacker.
For example it can identify and mask Credit Card number or IBANS.
 
The masking is meant to keep the logging useful but not useful enough for an attacker or a casual observer.
This project was inspired by the data breaches at Github and Twitter that appear to be the result of logging. 
Traditionally code reviews are the mechanism meant to catch errors but these are not foolproof and security should be multi-layered. 
This library can never be perfect and is meant to be an addition to, **NOT** a replacement for, secure development and operational practices. 
It supports logback. It will be expanded to log4j2 and log4j in future but these are not yet tested. 

## Status
Currently alpha, more automated testing needed. Not recommended for production yet.

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. See deployment for notes on how to deploy the project on a live system.

### Prerequisites

You must be using logback (or in future log4j2 or log4j). A wrapper like commons-logging of Slf4j is OK as long as the underlying logger is supported. 
No other logging frameworks are currently supported.

### Dependencies
There are few dependencies and they are only needed at runtime. The library plugs into the logging frameworks and so there is no direct dependency.

* log-sanitizer-core depends on Jackson 2.9 for JSON parsing. If you use the JSON sanitizer you need to make sure that Jackson 2.9.X is available in the classpath.
* commons-lang3 is needed for most most sanitizers.
* [log4j]:  runtime dependency with log-sanitizer-log4j 
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


##### Maven:
```xml
<dependency>
    <groupId>be.sysa</groupId>
    <artifactId>log-sanitizer-core</artifactId>
    <version>1.0.0</version>
</dependency>
<dependency>
    <groupId>be.sysa</groupId>
    <artifactId>log-sanitizer-logback</artifactId>
    <version>1.0.0</version>
</dependency>
```
##### Gradle:
```gradle
compile group: 'be.sysa', name: 'log-sanitizer-core', version: '1.0.0'
compile group: 'be.sysa', name: 'log-sanitizer-logback', version: '1.0.0'
```

## Log4J2 (works but not fully tested)

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
    <groupId>be.sysa</groupId>
    <artifactId>log-sanitizer-core</artifactId>
    <version>1.0.0</version>
</dependency>
<dependency>
    <groupId>be.sysa</groupId>
    <artifactId>log-sanitizer-log4j2</artifactId>
    <version>1.0.0</version>
</dependency>
```
##### Gradle:
```gradle
compile group: 'be.sysa', name: 'log-sanitizer-core', version: '1.0.0'
compile group: 'be.sysa', name: 'log-sanitizer-log4j2', version: '1.0.0'
```

# Development

## Running the tests

./mvnw clean verify

## Running the JMH benchmarks

./mvnw clean verify

## Caveats and Limitations
Testing is quite limited so far. It will be tested on real systems soon and integration tests with the logging systems themselves will be introduced. 

## Contributing

Please read [CONTRIBUTING.md](https://gist.github.com/PurpleBooth/b24679402957c63ec426) for details on our code of conduct, and the process for submitting pull requests to us.

## Versioning

We use [SemVer](http://semver.org/) for versioning. For the versions available, see the [tags on this repository](https://github.com/your/project/tags). 

## Authors

* **Mark Jeffrey** 


## License

This project is licensed under the Apache 2.0 License - see the [LICENSE.txt](LICENSE.txt) file for details

## Acknowledgments
