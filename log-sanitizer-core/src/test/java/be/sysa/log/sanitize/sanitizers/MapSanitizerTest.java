package be.sysa.log.sanitize.sanitizers;

import be.sysa.log.sanitize.Buffer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

public class MapSanitizerTest {

    private ToStringSanitizer sanitizer = new ToStringSanitizer();
    private HashMap<String, String> map;

    @BeforeEach
    public void setUp() throws Exception {
        map = new LinkedHashMap<>();
        map.put("username", "myuser");
        map.put("password", "myp3104e");
    }

    @Test
    public void sanitizeMap() {
        Buffer buffer = bufferOf(map);
        sanitizer.process(buffer, true);
        assertThat(buffer.toString()).isEqualTo("{username=myuser, password=********}");
    }

    private Buffer bufferOf(Object map) {
        return new Buffer(Objects.toString(map.toString()));
    }
}
