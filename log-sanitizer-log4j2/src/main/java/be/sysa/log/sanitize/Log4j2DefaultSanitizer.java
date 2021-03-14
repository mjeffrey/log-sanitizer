package be.sysa.log.sanitize;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.rewrite.RewritePolicy;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.impl.Log4jLogEvent;
import org.apache.logging.log4j.core.impl.MutableLogEvent;
import org.apache.logging.log4j.message.SimpleMessage;

@Plugin(name = "DefaultSanitizer", category = "Core", elementType = "rewritePolicy", printObject = true)
public final class Log4j2DefaultSanitizer implements RewritePolicy {

    private static MessageSanitizer messageSanitizer = MessageSanitizer.recommended();

    @Override
    public LogEvent rewrite(final LogEvent event) {
        Buffer buffer = new Buffer(event.getMessage().getFormattedMessage());
        SimpleMessage simpleMessage = new SimpleMessage(messageSanitizer.clean(buffer));

        if (event instanceof MutableLogEvent) {
            MutableLogEvent mutableEvent = (MutableLogEvent) event;
            mutableEvent.setMessage(simpleMessage);
            return mutableEvent;
        } else {
            Log4jLogEvent.Builder builder = new Log4jLogEvent.Builder();
            builder.setLoggerName(event.getLoggerName());
            builder.setMarker(event.getMarker());
            builder.setLoggerFqcn(event.getLoggerFqcn());
            builder.setLevel(event.getLevel());
            builder.setMessage(simpleMessage);
            builder.setThrown(event.getThrown());
            builder.setContextStack(event.getContextStack());
            builder.setThreadName(event.getThreadName());
            builder.setSource(event.getSource());
            builder.setTimeMillis(event.getTimeMillis());
            return builder.build();
        }
    }

    @PluginFactory
    public static Log4j2DefaultSanitizer createPolicy() {
        return new Log4j2DefaultSanitizer();
    }
}
