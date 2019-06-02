package be.sysa.log.sanitize;

import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

public class SensitiveLogTest extends AbstractFileTest {

    public String maskSensitiveData(String string) {
        Buffer buffer = new Buffer(string);

        new MessageSanitizer.JsonSanitizer().sanitize(buffer);
        new MessageSanitizer.IbanSanitizer().sanitize(buffer);
        new MessageSanitizer.PanSanitizer().sanitize(buffer);
        new MessageSanitizer.UuidSanitizer().sanitize(buffer);
        new MessageSanitizer.Base64Sanitizer().sanitize(buffer);

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
        new MessageSanitizer.JsonSanitizer().sanitize(buffer);
        JSONAssert.assertEquals(expected, extractJsonContent(buffer.toString()), JSONCompareMode.LENIENT);

    }

    private String extractJsonContent(CharSequence request) {
        String s = StringUtils.substringAfter(request.toString(), "{");
        s = StringUtils.substringBeforeLast(s, "}");
        return "{" + s + "}";
    }
}