package be.sysa.log.sanitize.logback;

import be.sysa.log.sanitize.Buffer;
import ch.qos.logback.classic.pattern.ThrowableProxyConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

import static be.sysa.log.sanitize.logback.Sanitizer.messageSanitizer;

public class ExceptionSanitizer extends ThrowableProxyConverter {

    @Override
    public String convert(ILoggingEvent event) {
        return messageSanitizer.clean(new Buffer(super.convert(event))).toString();
    }

}
