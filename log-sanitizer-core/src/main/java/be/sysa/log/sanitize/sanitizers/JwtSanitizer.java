package be.sysa.log.sanitize.sanitizers;

import be.sysa.log.sanitize.Bounds;
import be.sysa.log.sanitize.Buffer;
import be.sysa.log.sanitize.MessageSanitizer;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Collections.singletonList;

/**
 * Matches things that look like a Signed JWT (e.g. JWT access tokens, IDTokens) and masks the signature.
 * Note that since we are sure this is a JWT, Sanitizers that follow this one will not perform sanitization.
 */
@NoArgsConstructor
public class JwtSanitizer extends AbstractStringSanitizer {

    private static final List<Pattern> patterns = MessageSanitizer.compilePatterns(singletonList(
            "[A-Za-z0-9-_]{15,}\\.[A-Za-z0-9-_]{10,}\\.[A-Za-z0-9-_]{10,}"
    ));

    @Override
    public void process(Buffer buffer, boolean mask) {
        patterns.forEach(pattern -> maskJwt(buffer, pattern, mask));
    }

    private void maskJwt(Buffer buffer, Pattern pattern, boolean mask) {
        Matcher matcher = pattern.matcher(buffer.toString());

        while (matcher.find()) {
            maskSignature(buffer, matcher, mask);
        }
    }

    private void maskSignature(Buffer buffer, Matcher matcher, boolean mask) {
        int headerStart = matcher.start();
        int signatureEnd = matcher.end() - 1;

        final int payloadStart = StringUtils.indexOf(buffer, ".");
        final int signatureStart = StringUtils.indexOf(buffer, ".", payloadStart + 1);
        final String jwt = buffer.subSequence(headerStart, payloadStart).toString();
        final String header = new String(Base64.getUrlDecoder().decode(jwt), StandardCharsets.UTF_8);

        if (isJoseHeader(header)) {
            if (mask) {
                buffer.maskString("signature", new Bounds(signatureStart + 1, signatureEnd));
            }
            buffer.protect(new Bounds(matcher));
        }

    }

    private boolean isJoseHeader(String header) {
        JsonFactory factory = new JsonFactory();
        try (JsonParser parser = factory.createParser(header)) {
            while (!parser.isClosed()) {
                JsonToken jsonToken = parser.nextToken();
                if (JsonToken.FIELD_NAME.equals(jsonToken)) {
                    String fieldName = parser.getCurrentName();
                    if ("alg".equals(fieldName)) { // headers must contain the "alg" element see https://tools.ietf.org/html/rfc7515#section-4.1
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            return false; // we could not parse this so not a JOSE heade
        }
        return false;
    }

    @Override
    public String id() {
        return "JWT";
    }
}
