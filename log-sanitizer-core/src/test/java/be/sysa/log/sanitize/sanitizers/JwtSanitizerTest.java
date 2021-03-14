package be.sysa.log.sanitize.sanitizers;

import be.sysa.log.sanitize.Buffer;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JwtSanitizerTest {
    JwtSanitizer jwtSanitizer = new JwtSanitizer();
    private JWSSigner jwsSigner;

    @SneakyThrows
    @BeforeEach
    void setUp() {
        jwsSigner = new MACSigner("secretkey-1234567890123456789012");
    }

    @SneakyThrows
    @Test
    void sanitize() {

        final Buffer buffer = new Buffer("MyToken=" + getSerializedJwt("Hello, world!"));
        jwtSanitizer.process(buffer, true);

        final String sanitized = buffer.toString();
        final String original = buffer.getOriginal();
        assertThat(original).isEqualTo( "MyToken=eyJhbGciOiJIUzI1NiJ9.SGVsbG8sIHdvcmxkIQ.k2VkIicmdrRx9AImAfZMazdTAlfaJdJMIguF0gUCXss");
        assertThat(sanitized).isEqualTo("MyToken=eyJhbGciOiJIUzI1NiJ9.SGVsbG8sIHdvcmxkIQ.signature                                  ");
    }

    private String getSerializedJwt(String payload) throws JOSEException {
        JWSObject jwsObject = new JWSObject(new JWSHeader(JWSAlgorithm.HS256), new Payload(payload));
        jwsObject.sign(jwsSigner);
        return jwsObject.serialize();
    }

    @Test
    void id() {
        assertThat(jwtSanitizer.id() ).isEqualTo("JWT");
    }
}
