package be.sysa.log.sanitize;

import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

import static be.sysa.log.sanitize.MessageSanitizer.SANITIZER_PROPERTY_NAME;
import static java.util.Arrays.stream;

/**
 * Configuration can be done via a system property for example: -Dbe.sysa.log.sanitize.Configuration="JSON:UUID:PAN"
 * The value of the property should contain a String with sanitizer Ids separated by colons.
 */
public class Configuration {

    private static Map<String, Sanitizer> knownSanitizerMap = MessageSanitizer.knownSanitizerMap();

    public static MessageSanitizer messageSanitizer() {
        String property = System.getProperty(SANITIZER_PROPERTY_NAME);
        SanitizerList.SanitizerListBuilder builder = SanitizerList.builder();
        stream(StringUtils.split(property, ":"))
                .map(Configuration::loadSanitizer)
                .forEach(builder::add);
        return MessageSanitizer.builder().masked(builder.build()).build();
    }

    private static Sanitizer loadSanitizer(String id) {
        return knownSanitizerMap.containsKey(id) ? knownSanitizerMap.get(id) : newSanitizer(id);
    }

    @SneakyThrows
    private static Sanitizer newSanitizer(String id) {
        Class<?> aClass = Class.forName(id);
        return (Sanitizer) aClass.newInstance();
    }
}
