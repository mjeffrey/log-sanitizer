package be.sysa.log.sanitize.sanitizers;

import be.sysa.log.sanitize.Buffer;
import be.sysa.log.sanitize.MessageSanitizer;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Arrays.asList;

/**
 * Looks for strings that could be a PAN (= Primary Account Number) (= Credit/Debit Card number) based on a regex.
 *
 */
public class PanSanitizer extends MessageSanitizer.StringSanitizer {
    private static final List<Pattern> patterns = MessageSanitizer.compilePatterns(asList(
            "[0-9][0-9 ]{11,17}[0-9]"
    ));

    @Override
    public void sanitize(Buffer buffer) {
        patterns.forEach(pattern -> maskPan(buffer, pattern));
    }

    void maskPan(Buffer buffer, Pattern pattern) {
        Matcher matcher = pattern.matcher(buffer.toString());
        while (matcher.find()) {
            buffer.maskCharactersBetween(matcher, 4, 4);
        }
    }
    @Override
    public String id() {
        return "PAN";
    }
}
