package be.sysa.log.sanitize.sanitizers;

import be.sysa.log.sanitize.Buffer;
import be.sysa.log.sanitize.Sanitizer;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class AbstractSanitizerTest {

    protected void assertMasked(Sanitizer sanitizer, String original) {
        Buffer buffer = new Buffer(original);
        sanitizer.process(buffer, true);
        assertThat(buffer.toString()).contains("***");
    }

    protected Stream<String> linesFromFile(String filename) throws Exception {
        return Files.lines(Paths.get(ClassLoader.getSystemResource(filename).toURI()));
    }

}
