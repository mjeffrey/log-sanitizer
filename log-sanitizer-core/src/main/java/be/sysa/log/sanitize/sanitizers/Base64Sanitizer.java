package be.sysa.log.sanitize.sanitizers;

import be.sysa.log.sanitize.Bounds;
import be.sysa.log.sanitize.Buffer;
import be.sysa.log.sanitize.MessageSanitizer;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Collections.singletonList;

/**
 * Matches things that look like base64 since it may be encoded secrets (or access tokens).
 * Tends to match a lot since base64 characters are common.
 */
public class Base64Sanitizer extends MessageSanitizer.StringSanitizer {

    private static final List<Pattern> patterns = MessageSanitizer.compilePatterns(singletonList(
            "[\\p{Alnum}+/\\n\\r]{9,}"
    ));
    public static final int BASE64_STRING_MIN_LENGTH = 8;
    final int minLength;

    public Base64Sanitizer() {
        this(BASE64_STRING_MIN_LENGTH);
    }

    /**
     * Minimum length of string to match to consider as base64. Make higher to reduce false positives.
     * @param minLength minimum length before we match base64
     */
    public Base64Sanitizer(int minLength) {
        this.minLength = minLength;
    }

    @Override
    public void sanitize(Buffer buffer) {
        patterns.forEach(pattern -> maskMiddle(buffer, pattern));
    }

    private void maskMiddle(Buffer buffer, Pattern pattern) {
        Matcher matcher = pattern.matcher(buffer.toString());

        while (matcher.find()) {
            final Bounds bounds = new Bounds(matcher);
            int start = bounds.start();
            int length = bounds.length();

            if (length >= minLength && !buffer.isAllChars(bounds)) {
                int middleLength = (length / 3) + 1;
                buffer.mask(start + middleLength, middleLength);
            }
        }
    }
    @Override
    public String id() {
        return "BASE64";
    }
}
