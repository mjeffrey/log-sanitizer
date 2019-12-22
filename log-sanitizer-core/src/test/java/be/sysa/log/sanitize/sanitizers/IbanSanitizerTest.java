package be.sysa.log.sanitize.sanitizers;

import be.sysa.log.sanitize.AbstractFileTest;
import be.sysa.log.sanitize.Buffer;
import be.sysa.log.sanitize.sanitizers.IbanSanitizer;
import org.junit.Ignore;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class IbanSanitizerTest extends AbstractFileTest {

    IbanSanitizer sanitizer = new IbanSanitizer();

    @Test
    @Ignore // TODO support whitespace for IBANs in display format
    public void testIbansWhitespace() throws Exception {
        assertMasked( "(FR76 3000 6000 0112 3456 7890 189)" );
        assertMasked( "iban=DE91 1000 0000 0123 4567 89,  " );
    }

    @Test
    public void testIbansFromFile() throws Exception {
        linesFromFile("ibans.txt").forEachOrdered( this::assertMasked );
    }

    @Test
    public void testIbansCompact() {
        assertMasked( "(FR7630006000011234567890189)" );
        assertMasked( "iban=DE91100000000123456789,  " );
    }

    protected void assertMasked(String original) {
        Buffer buffer = new Buffer(original);
        sanitizer.sanitize(buffer);
        assertThat(buffer.toString()).contains("***");
    }
}