package be.sysa.log.sanitize.logback;

import be.sysa.log.sanitize.MessageSanitizer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Sanitizer {
    static MessageSanitizer messageSanitizer = MessageSanitizer.of(
            MessageSanitizer.recommendedSanitizers().values()
    );

}
