package be.sysa.log.sanitize;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ConfigurationTest {

    @Test
    public void messageSanitizer_KnownSanitizers() {
        System.setProperty(MessageSanitizer.SANITIZER_PROPERTY_NAME, "JSON:IBAN:PAN");
        MessageSanitizer messageSanitizer = Configuration.messageSanitizer();
        List<? extends Sanitizer> sanitizers = messageSanitizer.getMasked().getSanitizers();
        assertThat(sanitizers).extracting(Sanitizer::id).containsExactly("JSON", "IBAN", "PAN");
    }

    @Test
    public void messageSanitizer_CustomSanitizers() {
        System.setProperty(MessageSanitizer.SANITIZER_PROPERTY_NAME, "JSON:" + CustomSanitizer.class.getName() + ":PAN");
        MessageSanitizer messageSanitizer = Configuration.messageSanitizer();
        List<? extends Sanitizer> sanitizers = messageSanitizer.getMasked().getSanitizers();
        assertThat(sanitizers).extracting(Sanitizer::id).containsExactly("JSON", "CUSTOM", "PAN");
    }

    public static class CustomSanitizer implements Sanitizer{

        @Override
        public void process(Buffer buffer, boolean mask) {

        }

        @Override
        public String id() {
            return "CUSTOM";
        }
    }
}
