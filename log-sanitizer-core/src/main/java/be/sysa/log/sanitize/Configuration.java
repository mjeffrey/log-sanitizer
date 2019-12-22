package be.sysa.log.sanitize;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.stream.Collectors;

import static be.sysa.log.sanitize.MessageSanitizer.SANITIZER_PROPERTY_NAME;
import static java.util.Arrays.stream;

/**
 * Configuration can be done via a system property for example: -Dbe.sysa.log.sanitize.Configuration="JSON:UUID:PAN"
 * The value of the property should contain a String with sanitizer Ids separated by colons.
 * It is not necessary to
 */
public class Configuration {

    private static Map<String, Sanitizer> knownSanitizers = MessageSanitizer.knownSanitizers();

    public static MessageSanitizer messageSanitizer() {
        String property = System.getProperty(SANITIZER_PROPERTY_NAME);
        return MessageSanitizer.of(stream(StringUtils.split(property,":"))
                .map(Configuration::loadSanitizer)
                .collect(Collectors.toList()));
    }

    private static Sanitizer loadSanitizer(String id) {
        return knownSanitizers.containsKey(id) ? knownSanitizers.get(id) : newSanitizer(id);
    }

    @SneakyThrows
    private static Sanitizer newSanitizer(String id) {
        Class<?> aClass = Class.forName(id);
        return (Sanitizer) aClass.newInstance();
    }
}
