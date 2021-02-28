package be.sysa.log.sanitize.sanitizers;

import be.sysa.log.sanitize.Bounds;
import be.sysa.log.sanitize.Buffer;
import be.sysa.log.sanitize.MessageSanitizer;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Collections.singletonList;

/**
 * UUIDs are often  used as keys or as "private: references to data. The UUID sanitizer can mask them
 * while keeping the first part so they can still be searched if necessary.
 */
@AllArgsConstructor
@NoArgsConstructor
public class UuidSanitizer extends MessageSanitizer.StringSanitizer {
    private static final List<Pattern> patterns = MessageSanitizer.compilePatterns(singletonList(
            "\\p{XDigit}{8}-\\p{XDigit}{4}-\\p{XDigit}{4}-\\p{XDigit}{4}-\\p{XDigit}{12}"
    ));

    private boolean matchOnlyOnce;

    public static UuidSanitizer protect() {
        return new UuidSanitizer(true);
    }

    @Override
    public void sanitize(Buffer buffer) {
        patterns.forEach(pattern -> maskUuid(buffer, pattern));
    }

    private void maskUuid(Buffer buffer, Pattern pattern) {
        Matcher matcher = pattern.matcher(buffer.toString());
        while (matcher.find()) {
            if (matchOnlyOnce) {
                buffer.protect(new Bounds(matcher));
            } else {
                maskMatched(buffer, matcher);
            }
        }
    }

    private void maskMatched(Buffer buffer, Matcher matcher) {
        int start = matcher.start();
        buffer.mask(start + 9, 4);
        buffer.mask(start + 14, 4);
        buffer.mask(start + 19, 4);
    }

    @Override
    public String id() {
        return "UUID";
    }
}
