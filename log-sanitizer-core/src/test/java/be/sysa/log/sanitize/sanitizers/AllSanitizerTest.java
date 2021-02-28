package be.sysa.log.sanitize.sanitizers;

import be.sysa.log.sanitize.Buffer;
import be.sysa.log.sanitize.Sanitizer;
import be.sysa.log.sanitize.SanitizerExample;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import static org.apache.commons.lang3.StringUtils.*;
import static org.assertj.core.api.Assertions.assertThat;

public class AllSanitizerTest {

    private SanitizerExample converter = new SanitizerExample();
    private String jsonRequest = "Body: {\"scope\":\n\"https://api.paypal.com/v1/payments\",\"nonce\":\"2018-11-28T12:30:02ZT6yacUJ33wpfNm5I92Hhf8QJiFrpUSXlih-kCj4oWg8\",\"access_token\":\"A21AAHFyA9umVi9GeohYPnL9OK3jkD63TrAWW1\",\"token_type\":\"Bearer\",\"app_id\":\"APP-4VX56799PM6XXXX\",\"expires_in\":\"32400\"} after body";
    private String expandingJson = "Body: {\"num\": 0.32400,\"key\":\"B660B303-14A0-4100-8E38-6C837EE289CC\"} after body";

    @Test
    public void convert() {
        assertThat(converter.maskSensitiveData("json={\"key\":\"someKey\"}")).isEqualTo("json={\"key\":\"so**Key\"}");
        assertThat(converter.maskSensitiveData("json=")).isEqualTo("json=");
    }

  @Test
    public void maskIban() {
        assertReplaced("some data 'BE15123456789012' other data ", "BE15123456789012", "BE1512******9012");
    }

    @Test
    public void pan() {
        assertPanReplaced("some data '15123456789012' other data ", "15123456789012", "1512******9012");
    }

    @Test
    public void uuid() {
        assertUuidReplaced("some data 67D262BE-BF0D-4334-A591-7EEA71AF2C81 other data 533ae169-2843-4d21-9a23-92816b4bfe2f", "some data 67D262BE-****-****-****-7EEA71AF2C81 other data 533ae169-****-****-****-92816b4bfe2f");
    }

    @Test
    public void base64() {
        String base64 = "TWFuIGlzIGRpc3Rpbmd1aSB0aGlz\n" +
                "IHNpbmd1bGFyIHBhc3Npb3Qgb2Yg\n" +
                "dGhlIG1pbmQsIHRoYXQgY29udGlu\n" +
                "dWVkIGFuZCBpbmRlZmF0aWRzIHRo\n";

        assertBase64Replaced(base64, "TWFuIGlzIGRpc3Rpbmd1aSB0aGlz\n" +
                "IHNpbmd1bG******************\n" +
                "********************Y29udGlu\n" +
                "dWVkIGFuZCBpbmRlZmF0aWRzIHRo\n");
    }

    @Test
    public void base64Bepaf() {
        String base64 = "bepaf='[jC8R7PONb1e7ATFcEWAqXcjIXLw=]'";
        assertBase64Replaced(base64, "bepaf='[jC8R7PONb1**********XcjIXLw=]'");
    }

    @Test
    public void textNotTreatedAsBase64() {
        String base64 = "this='[shouldnotbeconveted]'";
        assertBase64Replaced(base64, base64);
    }

    @Test
    public void textTreatedAsBase64() {
        String base64 = "this='[shouldbeconveted6]'";
        assertBase64Replaced(base64, "this='[should******eted6]'");
    }

    @Test
    public void textShouldBeMaskedBase64TooLong() {
        assertBase64Replaced("transactionIdentifier='[OTBhNjgxYThiNDlhNDJlOGJhNDk=]'", "transactionIdentifier='[OTBhNjgxYT**********OGJhNDk=]'");
    }

    @Test
    public void textShouldBeMaskedBase64MixedCase() {
        assertBase64Replaced("transactionIdentifier='[OTBhOGJhNDk=]'", "transactionIdentifier='[OTBh****NDk=]'");
    }

    @Test
    public void panWithSpaces() {
        assertPanReplaced("some data 1512 3456 7890 1234  other data ", "1512 3456 7890 1234", "1512 **** **** 1234");
    }

    @Test
    public void maskJsonSmaller() {
        //TODO test cases where JSON is longer or equal.
        assertReplacedInJson(jsonRequest, "A21AAHFyA9umVi9GeohYPnL9OK3jkD63TrAWW1", "A21AAHFyA9um************OK3jkD63TrAWW1");
    }

    @Test
    public void maskJsonExpanded() {
        //TODO test cases where JSON is longer or equal.
        assertReplacedInJson(expandingJson, "B660B303-14A0-4100-8E38-6C837EE289CC", "B660B303-14A************6C837EE289CC");
    }

    private void assertBase64Replaced(String request, String expected) {
        assertSanitizedIsEqualTo(new Base64Sanitizer(), request, expected);
    }

    private void assertUuidReplaced(String request, String expected) {
        assertSanitizedIsEqualTo(new UuidSanitizer(), request, expected);
    }

    private void assertPanReplaced(String request, String original, String replacement) {
        String expected = request.replace(original, replacement);
        assertThat(expected).isNotEqualTo(original);
        assertSanitizedIsEqualTo(new PanSanitizer(), request, expected);
    }

    private void assertReplaced(String request, String original, String replacement) {
        String expected = request.replace(original, replacement);
        assertThat(expected).isNotEqualTo(original);
        assertThat(converter.maskSensitiveData(request)).isEqualTo(expected);
    }

    @SneakyThrows
    private void assertReplacedInJson(String request, String original, String replacement) {
        EmbeddedJson jsonRequest = new EmbeddedJson(request);

        String expectedJson = jsonRequest.json.replace(original, replacement);
        assertThat(expectedJson).isNotEqualTo(original);


        JsonSanitizer jsonSanitizer = new JsonSanitizer();
        Buffer buffer = new Buffer(request);
        jsonSanitizer.sanitize(buffer);

        String sanitizedBuffer = buffer.toString();
        EmbeddedJson sanitizedJsonRequest = new EmbeddedJson(sanitizedBuffer);
        JSONAssert.assertEquals(expectedJson, sanitizedJsonRequest.json, JSONCompareMode.LENIENT);

        assertThat(sanitizedBuffer).startsWith(jsonRequest.prefix);
        assertThat(sanitizedBuffer).endsWith(jsonRequest.suffix);
    }

    private Buffer assertSanitizedIsEqualTo(Sanitizer sanitizer, String request, String expected) {
        Buffer buffer = new Buffer(request);
        sanitizer.sanitize(buffer);
        assertThat(buffer.toString()).isEqualTo(expected);
        return buffer;
    }

    private class EmbeddedJson {
        String prefix;
        String json;
        String suffix;

        EmbeddedJson(String request) {
            prefix = substringBefore(request, "{");
            suffix = substringAfterLast(request, "}");
            json = removeEnd(removeStart(request, prefix), suffix);
        }
    }

}
