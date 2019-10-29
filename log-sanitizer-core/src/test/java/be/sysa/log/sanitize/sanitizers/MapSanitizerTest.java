package be.sysa.log.sanitize.sanitizers;

import be.sysa.log.sanitize.Buffer;
import be.sysa.log.sanitize.sanitizers.ToStringSanitizer;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

public class MapSanitizerTest {

    private ToStringSanitizer sanitizer = new ToStringSanitizer();
    private HashMap<String, String> map;

    @Before
    public void setUp() throws Exception {
        map = new LinkedHashMap<>();
        map.put("username", "myuser");
        map.put("password", "myp3104e");
    }

    @Test
    public void sanitizeMap() {
        Buffer buffer = bufferOf(map);
        sanitizer.sanitize(buffer);
        assertThat(buffer.toString()).isEqualTo("{username=myuser, password=********}");
    }

    private Buffer bufferOf(Object map) {
        return new Buffer(Objects.toString(map.toString()));
    }
}
