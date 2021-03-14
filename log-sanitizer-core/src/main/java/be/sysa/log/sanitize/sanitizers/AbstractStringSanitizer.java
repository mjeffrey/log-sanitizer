package be.sysa.log.sanitize.sanitizers;

import be.sysa.log.sanitize.Sanitizer;

import java.util.List;

import static java.util.Arrays.asList;
import static org.apache.commons.lang3.StringUtils.lowerCase;

public abstract class AbstractStringSanitizer implements Sanitizer {
    public static final int MIN_STRING_TO_MASK_LENGTH = 6;
    private static final List<String> KEYWORDS = asList(
            "key",
            "password",
            "passwd",
            "access_token",
            "pan"
    );


    protected boolean matchesKeyWord(String fieldName) {
        return KEYWORDS.stream()
                .anyMatch(lowerCase(fieldName)::contains);
    }
}
