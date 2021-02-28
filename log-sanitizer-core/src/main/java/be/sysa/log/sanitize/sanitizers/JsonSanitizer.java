package be.sysa.log.sanitize.sanitizers;

import be.sysa.log.sanitize.Bounds;
import be.sysa.log.sanitize.Buffer;
import be.sysa.log.sanitize.MessageSanitizer;
import com.fasterxml.jackson.core.*;
import lombok.SneakyThrows;

import java.io.ByteArrayOutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.DOTALL;

/**
 * Looks for keywords in JSON keys {@link MessageSanitizer.StringSanitizer#matchesKeyWord(String)}
 *
 */
public class JsonSanitizer extends MessageSanitizer.StringSanitizer {
    private static final int MIN_JSON_LENGTH = 15;
    private static final Pattern json = Pattern.compile("\\{.+}", DOTALL);

    @Override
    public void sanitize(Buffer buffer) {
        Matcher matcher = json.matcher(buffer);
        if (matcher.find()) {
            String group = matcher.group();
            if (group.length() > MIN_JSON_LENGTH) {
                String newJson = filterJson(group);
                buffer.replaceAt(new Bounds(matcher), newJson);
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
            c[thirdLength + i] = MessageSanitizer.MASK_CHARACTER;
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
