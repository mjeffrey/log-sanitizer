package be.sysa.log.sanitize.logback;

import be.sysa.log.sanitize.Buffer;
import be.sysa.log.sanitize.MessageSanitizer;
import be.sysa.log.sanitize.sanitizers.*;
import ch.qos.logback.classic.pattern.MessageConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

public class LogbackDefaultSanitizer extends MessageConverter {

    private static MessageSanitizer messageSanitizer = MessageSanitizer.of(
                new JsonSanitizer(),
                new IbanSanitizer(),
                new PanSanitizer(),
                new UuidSanitizer(),
                new ToStringSanitizer()
    );

    public String convert(ILoggingEvent event) {
        return maskSensitiveData(super.convert(event));
    }


    private String maskSensitiveData(String string) {
        return messageSanitizer.clean(new Buffer(string)).toString();
    }

}
