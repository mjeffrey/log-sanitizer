package be.sysa.log.sanitize.sanitizers;

import be.sysa.log.sanitize.Bounds;
import be.sysa.log.sanitize.Buffer;
import be.sysa.log.sanitize.MessageSanitizer;
import lombok.NoArgsConstructor;
import lombok.extern.java.Log;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Collections.singletonList;

/**
 * Looks for things that look like an IBAN based on a regex and masks the middle part.
 * Only supports standard "system" format (without spaces). Human readable format splits the
 * IBAN into groups of max 4 characters and is not (yet) identified.
 */
@NoArgsConstructor
@Log
public class UrlSanitzer extends AbstractStringSanitizer {
    private static final List<Pattern> patterns = MessageSanitizer.compilePatterns(singletonList(
            "(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]"
    ));

    @Override
    public void process(Buffer buffer, boolean mask) {
        patterns.forEach(pattern -> maskUrl(buffer, pattern, mask));
    }

    private void maskUrl(Buffer buffer, Pattern pattern, boolean mask) {
        Matcher matcher = pattern.matcher(buffer.toString());
        while (matcher.find()) {
            if (mask) {
                buffer.maskCharactersBetween(new Bounds(matcher), 4, 4);
            }
            buffer.protect(new Bounds(matcher));
        }
    }

    @Override
    public String id() {
        return "URL";
    }

}
