package be.sysa.log.sanitize.sanitizers;

import be.sysa.log.sanitize.Buffer;
import be.sysa.log.sanitize.MessageSanitizer;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Arrays.asList;

public class UuidSanitizer extends MessageSanitizer.StringSanitizer {
    private static final List<Pattern> patterns = MessageSanitizer.compilePatterns(asList(
            "\\p{XDigit}{8}-\\p{XDigit}{4}-\\p{XDigit}{4}-\\p{XDigit}{4}-\\p{XDigit}{12}"
    ));

    @Override
    public void sanitize(Buffer buffer) {
        patterns.forEach(pattern -> maskUuid(buffer, pattern));
    }

    private void maskUuid(Buffer buffer, Pattern pattern) {
        Matcher matcher = pattern.matcher(buffer.toString());
        while (matcher.find()) {
            int start = matcher.start();
            buffer.mask(start + 9, 4);
            buffer.mask(start + 14, 4);
            buffer.mask(start + 19, 4);
        }
    }
    @Override
    public String id() {
        return "UUID";
    }
}
