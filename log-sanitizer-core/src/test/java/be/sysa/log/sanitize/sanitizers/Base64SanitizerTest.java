package be.sysa.log.sanitize.sanitizers;

import be.sysa.log.sanitize.Buffer;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

public class Base64SanitizerTest {

    private Base64Sanitizer base64Sanitizer;

    @BeforeEach
    void setUp() {
        base64Sanitizer = new Base64Sanitizer(10);
    }

    @ParameterizedTest
    @ValueSource(strings = {"c29tZSBkYXRh==", "MTIyMzIzMzB0LXU0IHJnb2lqd2UwZjlpY2VyaGY5ZXVyaHZvZGZwa3ZiW3RodC1rb3dzZDtjbHdwZXdlZXZiZg=="})
    public void sanitize(String base64) {
        Buffer buffer = new Buffer("AAA-" + base64 + "-ZZZ");
        System.out.println(buffer);
        base64Sanitizer.sanitize(buffer);
        System.out.println(buffer);
        assertThat(buffer.toString()).contains("*****")
                .startsWith("AAA-" + StringUtils.left(base64, 2))
                .endsWith(StringUtils.right(base64, 2) + "-ZZZ")
        ;
    }

    @Test
    void id() {
        assertThat(base64Sanitizer.id()).isEqualTo("BASE64");
    }
}
