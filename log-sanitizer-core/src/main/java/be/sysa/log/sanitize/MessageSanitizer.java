package be.sysa.log.sanitize;

import be.sysa.log.sanitize.sanitizers.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.lang3.StringUtils.lowerCase;

/**
 * All KnownSanitizers are defined in this class. It also defines the keywords used by various sanitizers.
 */
public class MessageSanitizer {

    public static final String SANITIZER_PROPERTY_NAME = Configuration.class.getName();
    public static final char MASK_CHARACTER = '*';

    private static final List<String> KEYWORDS = asList(
            "key",
            "password",
            "passwd",
            "access_token",
            "pan"
    );

    private List<? extends Sanitizer> sanitizers;

    public static MessageSanitizer of(Sanitizer... sanitizers) {
        return new MessageSanitizer(asList(sanitizers));
    }

    public static MessageSanitizer all() {
        return MessageSanitizer.of(knownSanitizers().values());
    }

    public static MessageSanitizer of(Collection<Sanitizer> sanitizers) {
        return new MessageSanitizer(sanitizers);
    }

    private MessageSanitizer(Collection<? extends Sanitizer> sanitizers) {
        this.sanitizers = unmodifiableList(new ArrayList<>(sanitizers));
    }

    public static Map<String, Sanitizer> knownSanitizers() {
        return Stream.of(
                new JwtSanitizer(),
                new UuidSanitizer(),
                new IbanSanitizer(),
                new JsonSanitizer(),
                new PanSanitizer(),
                new Base64Sanitizer(),
                new ToStringSanitizer())
                .collect(toMap(StringSanitizer::id, identity()));
    }

    public static Map<String, Sanitizer> recommendedSanitizers() {
        return Stream.of(
                new JwtSanitizer(),
                new UuidSanitizer(true),
                new IbanSanitizer(true),
                new JsonSanitizer(),
                new PanSanitizer(),
                new Base64Sanitizer(12),
                new ToStringSanitizer())
                .collect(toMap(StringSanitizer::id, identity()));
    }

    public static List<Pattern> compilePatterns(List<String> ibanPatterns) {
        return ibanPatterns.stream()
                .map(Pattern::compile).collect(toList());
    }

    List<? extends Sanitizer> getSanitizers() {
        return sanitizers;
    }

    public Buffer clean(Buffer buffer) {
        sanitizers.forEach(processor -> {
            try {
                processor.sanitize(buffer);
            } catch (Exception e) {
                // ignore it or prepend something on the buffer?
            }
        });
        return buffer;
    }

    public abstract static class StringSanitizer implements Sanitizer {
        public static final int MIN_STRING_TO_MASK_LENGTH = 6;

        protected boolean matchesKeyWord(String fieldName) {
            return KEYWORDS.stream()
                    .anyMatch(lowerCase(fieldName)::contains);
        }
    }

}
