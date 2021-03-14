package be.sysa.log.sanitize.sanitizers;

import be.sysa.log.sanitize.Buffer;
import be.sysa.log.sanitize.Sanitizer;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class IbanSanitizerTest extends AbstractSanitizerTest {

    IbanSanitizer sanitizer = new IbanSanitizer();

    @Test
    public void testIbansFromFile() throws Exception {
        linesFromFile("ibans.txt").forEachOrdered( this::assertMasked );
    }

    @Test
    public void testIbansCompact() {
        assertMasked( "(FR7630006000011234567890189)" );
        assertMasked( "iban=DE91100000000123456789,  " );
    }

    @Test
    public void testProtected() {
        Sanitizer sanitizer1 = new IbanSanitizer();
        Buffer buffer = new Buffer("(FR7630006000011234567890189)");
        sanitizer1.process(buffer, false);
        assertThat(buffer.toString()).doesNotContain("***");
        Sanitizer sanitizer2 = new Base64Sanitizer();
        sanitizer2.process(buffer, true);
        assertThat(buffer.toString()).doesNotContain("***");

        Buffer buffer2 = new Buffer("(FR7630006000011234567890189)");
        sanitizer2.process(buffer2, true);
        assertThat(buffer2.toString()).contains("***");

    }

    protected void assertMasked(String original) {
        Buffer buffer = new Buffer(original);
        sanitizer.process(buffer, true);
        assertThat(buffer.toString()).contains("***");
    }
}
