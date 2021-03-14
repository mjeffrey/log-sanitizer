package be.sysa.log.sanitize.sanitizers;

import be.sysa.log.sanitize.Bounds;
import be.sysa.log.sanitize.Buffer;
import be.sysa.log.sanitize.MessageSanitizer;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Collections.singletonList;

/**
 * Looks for strings that could be a PAN (= Primary Account Number) (= Credit/Debit Card number) based on a regex.
 *
 */
public class PanSanitizer extends AbstractStringSanitizer {
    private static final List<Pattern> patterns = MessageSanitizer.compilePatterns(singletonList(
            "[0-9][0-9 ]{11,17}[0-9]"
    ));

    @Override
    public void process(Buffer buffer, boolean mask) {
        patterns.forEach(pattern -> maskPan(buffer, pattern, mask));
    }

    void maskPan(Buffer buffer, Pattern pattern, boolean mask) {
        Matcher matcher = pattern.matcher(buffer.toString());
        while (matcher.find()) {
            Bounds bounds = new Bounds(matcher);
            if ( mask ){
                buffer.maskCharactersBetween(bounds, 4, 4);
            }
            buffer.protect(bounds);
        }
    }
    @Override
    public String id() {
        return "PAN";
    }
}
