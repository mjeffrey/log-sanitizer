package be.sysa.log.sanitize.sanitizers;

import be.sysa.log.sanitize.Bounds;
import be.sysa.log.sanitize.Buffer;
import be.sysa.log.sanitize.MessageSanitizer;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Arrays.asList;
import static java.util.regex.Pattern.DOTALL;
import static org.apache.commons.lang3.StringUtils.*;

/**
 * A matcher for the result of a generated toString() method which generates name, value pairs (it also covers toString() of Maps which is similar)
 * It covers lombok @ToString, Intellij toString() and apache commons ToStringBuilder formats.
 * The keyword list is used to identify potential secrets.
 */
public class ToStringSanitizer extends AbstractStringSanitizer {
    private static final int MIN_MAP_STRING_LENGTH = 12;
    private static final List<Pattern> patterns = asList(
            Pattern.compile("(\\(.+\\))", DOTALL),
            Pattern.compile("(\\{.+})", DOTALL),
            Pattern.compile("(\\[.+])", DOTALL));

    @Override
    public void process(Buffer buffer, boolean mask) {
        for (Pattern pattern : patterns) {
            Matcher matcher = pattern.matcher(buffer);
            if (matcher.find()) {
                String group = matcher.group();
                if (group.length() > MIN_MAP_STRING_LENGTH) {
                    Bounds bounds = new Bounds(matcher);
                    if (mask) {
                        String newMapString = filterMapStringFields(substring(group, 1, group.length() - 1));
                        buffer.replaceAt(bounds, left(group, 1) + newMapString + right(group, 1));
                    }
                    buffer.protect(bounds);
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
                split[1] = matchesKeyWord(split[0]) ? StringUtils.rightPad("", split[1].length(), MessageSanitizer.MASK_CHARACTER) : split[1];
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
