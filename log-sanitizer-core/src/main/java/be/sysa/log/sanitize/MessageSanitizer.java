package be.sysa.log.sanitize;

import com.fasterxml.jackson.core.*;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.Collections.unmodifiableList;
import static java.util.regex.Pattern.DOTALL;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.lang3.StringUtils.*;

/**
 * All KnownSanitizers are defined in this
 */
public class MessageSanitizer {

    static final String SANITIZER_PROPERTY_NAME = Configuration.class.getName();
    static final char MASK_CHARACTER = '*';

    private static final List<String> KEYWORDS = asList(
            "key",
            "password",
            "passwd",
            "access_token"
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
                new JsonSanitizer(),
                new IbanSanitizer(),
                new PanSanitizer(),
                new UuidSanitizer(),
                new Base64Sanitizer(),
                new ToStringSanitizer())
                .collect(toMap(StringSanitizer::id, Function.identity()));
    }

    private static List<Pattern> compilePatterns(List<String> ibanPatterns) {
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

    public static class PanSanitizer extends StringSanitizer {
        private static final List<Pattern> patterns = compilePatterns(asList(
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

    public static class IbanSanitizer extends PanSanitizer {
        private static final List<Pattern> patterns = compilePatterns(asList(
                "BE\\d{2}\\s*\\d{4}\\s*\\d{4}\\s*\\d{4}",
                "FR\\d{2}\\s*\\d{4}\\s*\\d{4}\\s*\\d{4}\\s*\\d{4}\\s*\\d{4}\\s*\\d{3}",
                "RO\\d{2}\\s*\\p{Alpha}{4}\\s*\\d{4}\\s*\\d{4}\\s*\\d{4}\\s*\\d{4}",
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

    public static class Base64Sanitizer extends StringSanitizer {

        private static final List<Pattern> patterns = compilePatterns(asList(
                "[\\p{Alnum}+/\\n\\r]{9,}"
        ));
        public static final int BASE64_STRING_MIN_LENGTH = 6;

        @Override
        public void sanitize(Buffer buffer) {
            patterns.forEach(pattern -> maskMiddle(buffer, pattern));
        }

        private void maskMiddle(Buffer buffer, Pattern pattern) {
            Matcher matcher = pattern.matcher(buffer.toString());

            while (matcher.find()) {
                int start = matcher.start();
                int length = matcher.end() - start;

                if (length >= BASE64_STRING_MIN_LENGTH && !buffer.isAllChars(matcher)) {
                    int middleLength = (length / 3) + 1;
                    buffer.mask(start + middleLength, middleLength);
                }
            }
        }
        @Override
        public String id() {
            return "BASE64";
        }
    }

    public static class UuidSanitizer extends StringSanitizer {
        private static final List<Pattern> patterns = compilePatterns(asList(
                "\\p{XDigit}{8}-\\p{XDigit}{4}-\\p{XDigit}{4}-\\p{XDigit}{4}-\\p{XDigit}{12}"
        ));

        @Override
        public void sanitize(Buffer buffer) {
            patterns.forEach(pattern -> maskUuid(buffer, pattern));
        }

        private void maskUuid(Buffer buffer, Pattern pattern) {
            Matcher matcher = pattern.matcher(buffer.toString());
            while (matcher.find()) {
                int start = matcher.start();
                buffer.mask(start + 9, 4);
                buffer.mask(start + 14, 4);
                buffer.mask(start + 19, 4);
            }
        }
        @Override
        public String id() {
            return "UUID";
        }
    }

    public static class ToStringSanitizer extends StringSanitizer {
        private static final int MIN_MAP_STRING_LENGTH = 12;
        private static final List<Pattern> patterns = asList(Pattern.compile("(\\(.+\\))", DOTALL),  Pattern.compile("(\\{.+\\})", DOTALL), Pattern.compile("(\\[.+\\])", DOTALL));
        @Override
        public void sanitize(Buffer buffer) {
            for (Pattern pattern : patterns) {
                Matcher matcher = pattern.matcher(buffer);
                if (matcher.find()) {
                    String group = matcher.group();
                    if (group.length() > MIN_MAP_STRING_LENGTH) {
                        String newMapString = filterMapStringFields(substring(group, 1, group.length()-1));
                        buffer.replaceAt(matcher, left(group,1) + newMapString + right(group,1));
                    }
                }
            }
        }

        @SneakyThrows
        private String filterMapStringFields(String mapString) {
            String[] strings = split(mapString, ",");
            for (int i = 0; i < strings.length; i++) {
                String[] split = split(strings[i], "=");
                if (split.length > 1) {
                    split[1] = matchesKeyWord(split[0]) ? rightPad("", split[1].length(), MASK_CHARACTER) : split[1];
                }
                strings[i] = String.join("=", split);
            }
            return String.join(",", strings);
        }

        @Override
        public String id() {
            return "TO_STRING";
        }
    }


    public static class JsonSanitizer extends StringSanitizer {
        private static final int MIN_JSON_LENGTH = 15;
        private static final Pattern json = Pattern.compile("\\{.+\\}", DOTALL);

        @Override
        public void sanitize(Buffer buffer) {
            Matcher matcher = json.matcher(buffer);
            if (matcher.find()) {
                String group = matcher.group();
                if (group.length() > MIN_JSON_LENGTH) {
                    String newJson = filterJson(group);
                    buffer.replaceAt(matcher, newJson);
                }
            }
        }

        private String maskValue(String value) {
            int length = value.length();
            if (length < MIN_STRING_TO_MASK_LENGTH) {
                return value;
            }
            int thirdLength = length / 3;
            char[] c = value.toCharArray();
            for (int i = 0; i < thirdLength; i++) {
                c[thirdLength + i] = MASK_CHARACTER;
            }
            return new String(c);
        }

        @SneakyThrows
        private String filterJson(String json) {
            JsonFactory factory = new JsonFactory();
            ByteArrayOutputStream stream = new ByteArrayOutputStream(json.length() * 3);
            try (
                    JsonGenerator generator = factory.createGenerator(stream, JsonEncoding.UTF8);
                    JsonParser parser = factory.createParser(json)
            ) {
                while (!parser.isClosed()) {
                    JsonToken jsonToken = parser.nextToken();
                    if (JsonToken.FIELD_NAME.equals(jsonToken)) {
                        String fieldName = parser.getCurrentName();
                        jsonToken = parser.nextToken();
                        generator.writeFieldName(fieldName);
                        String value = parser.getValueAsString();

                        if (matchesKeyWord(fieldName)) {
                            String newValue = maskValue(value);
                            generator.writeString(newValue);
                        } else {
                            if (jsonToken.isNumeric()) {
                                generator.writeNumber(value);
                            } else {
                                generator.writeString(value);
                            }
                        }
                    } else if (JsonToken.START_ARRAY.equals(jsonToken)) {
                        generator.writeStartArray();
                    } else if (JsonToken.END_ARRAY.equals(jsonToken)) {
                        generator.writeEndArray();
                    } else if (JsonToken.START_OBJECT.equals(jsonToken)) {
                        generator.writeStartObject();
                    } else if (JsonToken.END_OBJECT.equals(jsonToken)) {
                        generator.writeEndObject();
                    }
                }
            } catch (Exception e) {
                return json;
            }
            return stream.toString();
        }

        @Override
        public String id() {
            return "JSON";
        }
    }
}
