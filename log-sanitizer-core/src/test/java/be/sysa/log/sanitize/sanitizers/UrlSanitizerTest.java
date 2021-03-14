package be.sysa.log.sanitize.sanitizers;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class UrlSanitizerTest extends AbstractSanitizerTest {

    private UrlSanitzer urlSanitizer = new UrlSanitzer();

    @ParameterizedTest
    @ValueSource(strings = {
            "other http://localhost.com:8080/fred?red=2 fred",
            "https://www.google.com:443/fred",
            "http://localhost:8080/fred?red=2"
    })
    public void sanitized(String text) {
        assertMasked(urlSanitizer, text);
    }

}
