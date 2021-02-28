package be.sysa.log.sanitize.sanitizers;

import be.sysa.log.sanitize.Bounds;
import be.sysa.log.sanitize.Buffer;
import be.sysa.log.sanitize.MessageSanitizer;
import lombok.NoArgsConstructor;
import lombok.extern.java.Log;
import org.iban4j.IbanUtil;

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
public class IbanSanitizer extends PanSanitizer {
    private boolean matchOnlyOnce;
    private volatile Boolean ibanValidator;
    private static final List<Pattern> patterns = MessageSanitizer.compilePatterns(singletonList(
            "[A-Z]{2,2}[0-9]{2,2}[a-zA-Z0-9]{1,30}"
    ));

    public IbanSanitizer(boolean matchOnlyOnce) {
        this.matchOnlyOnce = matchOnlyOnce;
    }

    public static IbanSanitizer protect() {
        return new IbanSanitizer(true);
    }

    @Override
    public void sanitize(Buffer buffer) {
        patterns.forEach(pattern -> maskIban(buffer, pattern));
    }

    void maskIban(Buffer buffer, Pattern pattern) {
        Matcher matcher = pattern.matcher(buffer.toString());
        while (matcher.find()) {
            if (isMatched(matcher.group())) {
                if (matchOnlyOnce) {
                    buffer.protect(new Bounds(matcher));
                } else {
                    buffer.maskCharactersBetween(new Bounds(matcher), 4, 4);
                }
            }
        }
    }

    private boolean isMatched(String iban) {
        if (ibanValidator == null || ibanValidator == Boolean.TRUE) {
            try {
                IbanUtil.validate(iban);
            } catch (NoClassDefFoundError cnf) {
                ibanValidator = Boolean.FALSE;
                log.warning("Could not load iban4j class, so we may match things that are not valid IBANs");
                return true;
            } catch (RuntimeException e) {
                return false;
            }
        }
        return true;
    }


    @Override
    public String id() {
        return "IBAN";
    }

}
