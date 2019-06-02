package be.sysa.log.sanitize;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.spi.LoggingEvent;

import java.lang.reflect.Field;

public class SanitizerLayout extends PatternLayout {

    public SanitizerLayout() {
        super();
    }

    public SanitizerLayout(String pattern) {
        super(pattern);
    }

    @Override
    public String format(LoggingEvent event) {

        // only process String type messages
        if (event.getMessage() != null && event.getMessage() instanceof String) {

            String message = event.getMessage().toString();
            message = StringUtils.trim("Some custom text --->>"+message);

            // earlier versions of log4j don't provide any way to update messages,
            // so use reflections to do this
            try {
                Field field = LoggingEvent.class.getDeclaredField("message");
                field.setAccessible(true);
                field.set(event, message);
            } catch (Exception e) {
                // Dont log it as it will lead to infinite loop. Simply print the trace
                e.printStackTrace();
            }

        }

        return super.format(event);
    }

}