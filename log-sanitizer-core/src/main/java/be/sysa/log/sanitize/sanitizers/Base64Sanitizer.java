package be.sysa.log.sanitize.sanitizers;

import be.sysa.log.sanitize.Buffer;
import be.sysa.log.sanitize.MessageSanitizer;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Arrays.asList;

public class Base64Sanitizer extends MessageSanitizer.StringSanitizer {

    private static final List<Pattern> patterns = MessageSanitizer.compilePatterns(asList(
            "[\\p{Alnum}+/\\n\\r]{9,}"
    ));
    public static final int BASE64_STRING_MIN_LENGTH = 6;

    @Override
    public void sanitize(Buffer buffer) {
        patterns.forEach(pattern -> maskMiddle(buffer, pattern));
    }

    private void maskMiddle(Buffer buffer, Pattern pattern) {
        Matcher matcher = pattern.matcher(buffer.toString());

        while (matcher.find()) {
            int start = matcher.start();
            int length = matcher.end() - start;

            if (length >= BASE64_STRING_MIN_LENGTH && !buffer.isAllChars(matcher)) {
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
