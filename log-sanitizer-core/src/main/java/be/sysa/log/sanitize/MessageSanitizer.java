package be.sysa.log.sanitize;

import be.sysa.log.sanitize.sanitizers.*;
import lombok.Builder;
import lombok.Value;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.toList;

/**
 * All KnownSanitizers are defined in this class. It also defines the keywords used by various sanitizers.
 */
@Value
public class MessageSanitizer {

    public static final String SANITIZER_PROPERTY_NAME = Configuration.class.getName();
    public static final char MASK_CHARACTER = '*';

    SanitizerList masked;
    SanitizerList retained;

    @Builder
    public MessageSanitizer(SanitizerList masked,
                            SanitizerList retained) {
        this.masked = defaultIfNull(masked);
        this.retained = defaultIfNull(retained);
    }

    private SanitizerList defaultIfNull(SanitizerList sanitizerList) {
        return sanitizerList == null ? SanitizerList.builder().build() : sanitizerList;
    }

    public static final SanitizerList knownSanitizers =
            SanitizerList.builder()
                    .add(new JwtSanitizer())
                    .add(new UuidSanitizer())
                    .add(new UrlSanitzer())
                    .add(new PanSanitizer())
                    .add(new IbanSanitizer())
                    .add(new JsonSanitizer())
                    .add(new Base64Sanitizer())
                    .add(new ToStringSanitizer())
                    .build();

    public static Map<String, Sanitizer> knownSanitizerMap() {
        return knownSanitizers.knownSanitizerMap();
    }

    public static MessageSanitizer recommended() {
        SanitizerList retain = SanitizerList.builder()
                .add(new UuidSanitizer())
                .add(new UrlSanitzer())
                .build();
        SanitizerList mask = SanitizerList.builder()
                .add(new JwtSanitizer())
                .add(new PanSanitizer())
                .add(new IbanSanitizer())
                .add(new JsonSanitizer())
                .add(new Base64Sanitizer())
                .add(new ToStringSanitizer())
                .build();

        return MessageSanitizer
                .builder()
                .retained(retain)
                .masked(mask)
                .build();
    }

    public static List<Pattern> compilePatterns(List<String> ibanPatterns) {
        return ibanPatterns.stream()
                .map(Pattern::compile).collect(toList());
    }

    public static MessageSanitizer all() {
        return MessageSanitizer.builder()
                .masked(knownSanitizers)
                .build();
    }

    public Buffer clean(Buffer buffer) {
        retained.process(buffer, false);
        masked.process(buffer, true);
        return buffer;
    }

}
