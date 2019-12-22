package be.sysa.log.sanitize;

import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ConfigurationTest {

    @Test
    public void messageSanitizer_KnownSanitizers() {
        System.setProperty(MessageSanitizer.SANITIZER_PROPERTY_NAME, "JSON:IBAN:PAN");
        MessageSanitizer messageSanitizer = Configuration.messageSanitizer();
        List<? extends Sanitizer> sanitizers = messageSanitizer.getSanitizers();
        assertThat(sanitizers).extracting(Sanitizer::id).containsExactly("JSON", "IBAN", "PAN");
    }

    @Test
    public void messageSanitizer_CustomSanitizers() {
        System.setProperty(MessageSanitizer.SANITIZER_PROPERTY_NAME, "JSON:" + CustomSanitizer.class.getName() + ":PAN");
        MessageSanitizer messageSanitizer = Configuration.messageSanitizer();
        List<? extends Sanitizer> sanitizers = messageSanitizer.getSanitizers();
        assertThat(sanitizers).extracting(Sanitizer::id).containsExactly("JSON", "CUSTOM", "PAN");
    }

    public static class CustomSanitizer implements Sanitizer{

        @Override
        public void sanitize(Buffer buffer) {

        }

        @Override
        public String id() {
            return "CUSTOM";
        }
    }
}