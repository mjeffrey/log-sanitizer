package be.sysa.log.sanitize.log4j2;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.junit.Test;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Log4jTest {

    @Test
    public void name() {

    }

    @Test
    public void test() {
        TestAppender testAppender = new TestAppender("test", null, null);
        Logger LOGGER = LogManager.getLogger();
        LoggerContext loggerContext = (LoggerContext) LogManager.getContext();
        Configuration configuration = loggerContext.getConfiguration();

        LOGGER.info("Filepath: {}", configuration.getConfigurationSource().getLocation());
        // Log4j root logger has no name attribute -> name == ""
        LoggerConfig rootLoggerConfig = configuration.getLoggerConfig("");

        rootLoggerConfig.getAppenders().forEach((name, appender) -> {
            LOGGER.info("Appender {}: {}", name, appender.getLayout());
            // rootLoggerConfig.removeAppender(a.getName());
        });

        rootLoggerConfig.getAppenderRefs().forEach(ar -> {
            System.out.println("AppenderReference: " + ar.getRef());
        });

        // adding appenders
        configuration.addAppender(testAppender);
        LOGGER.info("Some message");
        System.out.println(testAppender.messages);
//        LoggingEvent loggingEvent = testAppender.events.get(0);
        //asset equals 1 because log level is info, change it to debug and
    }

    public class TestAppender extends AbstractAppender {
        public List<String> messages = new ArrayList<String>();

        protected TestAppender(String name, Filter filter, Layout<? extends Serializable> layout) {
            super(name, filter, layout);
        }


        @Override
        public void append(LogEvent logEvent) {
            messages.add(logEvent.getMessage().toString());

        }
    }
}
