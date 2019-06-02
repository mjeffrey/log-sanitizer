package be.sysa.log.sanitize;


import org.apache.log4j.Logger;
import org.apache.log4j.pattern.LogEvent;
import org.apache.log4j.rewrite.RewritePolicy;
import org.apache.log4j.spi.LoggingEvent;

public final class Log4jDefaultSanitizer implements RewritePolicy {

    private static MessageSanitizer messageSanitizer = MessageSanitizer.all();

    @Override
    public LoggingEvent rewrite(LoggingEvent source) {
        Buffer buffer = new Buffer(source.getRenderedMessage());

        LoggingEvent loggingEvent = new LoggingEvent(
                source.getFQNOfLoggerClass(),
                source.getLogger() != null ? source.getLogger(): Logger.getLogger(source.getLoggerName()),
                source.getTimeStamp(),
                source.getLevel(),
                messageSanitizer.clean(buffer),
                source.getThreadName(),
                source.getThrowableInformation(),
                source.getNDC(),
                source.getLocationInformation(),
                source.getProperties()
        );
        return loggingEvent;
    }


}