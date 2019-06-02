package be.sysa.log.sanitize.logback;

import be.sysa.log.sanitize.Buffer;
import be.sysa.log.sanitize.MessageSanitizer;
import be.sysa.log.sanitize.Sanitizer;
import be.sysa.log.sanitize.SanitizerExample;
import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.commons.lang3.StringUtils.*;
import static org.assertj.core.api.Assertions.assertThat;

public class LogbackPropertySanitizerTest {

    private SanitizerExample converter = new SanitizerExample();
    private HashMap<String, String> map;

    @Before
    public void setUp() throws Exception {
        map = new HashMap<>();
        map.put("username", "myuser");
        map.put("password", "myp3104e");

    }

    @Test
    public void maskPassword() {
        assertReplaced(map.toString(), "myp3104e", "********");
    }

    @Test
    public void maskPasswordInMap() {
        assertReplaced(map.toString(), "myp3104e", "********");
    }

    private void assertReplaced(String request, String original, String replacement) {

        String expected = request.replace(original, replacement);
        assertThat(expected).isNotEqualTo(original);
        assertThat(converter.maskSensitiveData(request)).isEqualTo(expected);
    }

}