package be.sysa.log.sanitize.logback;

import be.sysa.log.sanitize.SanitizerExample;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;

public class LogbackPropertySanitizerTest {

    private SanitizerExample converter = new SanitizerExample();
    private HashMap<String, String> map;

    @BeforeEach
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
