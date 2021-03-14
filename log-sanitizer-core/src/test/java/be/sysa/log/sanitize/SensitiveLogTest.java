package be.sysa.log.sanitize;

import be.sysa.log.sanitize.sanitizers.*;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import static org.assertj.core.api.Assertions.assertThat;

public class SensitiveLogTest extends AbstractSanitizerTest {

    public String maskSensitiveData(String string) {
        Buffer buffer = new Buffer(string);

        new JsonSanitizer().process(buffer, true);
        new IbanSanitizer().process(buffer, true);
        new PanSanitizer().process(buffer, true);
        new UuidSanitizer().process(buffer, true);
        new Base64Sanitizer().process(buffer, true);

        return buffer.toString();
    }

    @Test
    public void readFile() throws Exception {
        linesFromFile("log-input.txt").forEachOrdered(l -> {
                    String maskSensitiveData = maskSensitiveData(l);
                    System.out.println(maskSensitiveData);
                }
        );
    }

    @SneakyThrows
    private void assertReplacedInJson(String request, String original, String replacement) {
        String jsonRequest = extractJsonContent(request);

        String expected = jsonRequest.replace(original, replacement);
        assertThat(expected).isNotEqualTo(original);
// TODO check that before JSON and after JSON are there.
        Buffer buffer = new Buffer(request);
        new JsonSanitizer().process(buffer, true);
        JSONAssert.assertEquals(expected, extractJsonContent(buffer.toString()), JSONCompareMode.LENIENT);

    }

    private String extractJsonContent(CharSequence request) {
        String s = StringUtils.substringAfter(request.toString(), "{");
        s = StringUtils.substringBeforeLast(s, "}");
        return "{" + s + "}";
    }
}
