package be.sysa.log.sanitize;

import be.sysa.log.sanitize.MessageSanitizer.*;

public class SanitizerExample {
    private static final MessageSanitizer messageSanitizer = MessageSanitizer.all();

    public String maskSensitiveData(String string) {
        return messageSanitizer.clean(new Buffer(string)).toString();
    }
}
