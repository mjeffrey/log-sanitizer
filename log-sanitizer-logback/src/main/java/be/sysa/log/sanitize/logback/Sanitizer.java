package be.sysa.log.sanitize.logback;

import be.sysa.log.sanitize.MessageSanitizer;
import be.sysa.log.sanitize.sanitizers.*;

public class Sanitizer {
    static MessageSanitizer messageSanitizer = MessageSanitizer.of(
            new JsonSanitizer(),
            new IbanSanitizer(),
            new PanSanitizer(),
            new UuidSanitizer(),
            new ToStringSanitizer()
    );


}
