package be.sysa.log.sanitize.sanitizers;

import be.sysa.log.sanitize.Buffer;
import be.sysa.log.sanitize.MessageSanitizer;

import java.util.List;
import java.util.regex.Pattern;

import static java.util.Arrays.asList;

public class IbanSanitizer extends PanSanitizer {
    private static final List<Pattern> patterns = MessageSanitizer.compilePatterns(asList(
            "[A-Z]{2,2}[0-9]{2,2}[a-zA-Z0-9]{1,30}"
    ));

    @Override
    public void sanitize(Buffer buffer) {
        patterns.forEach(pattern -> maskPan(buffer, pattern));
    }

    @Override
    public String id() {
        return "IBAN";
    }

}
