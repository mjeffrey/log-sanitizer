package be.sysa.log.sanitize;

import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class AbstractFileTest {

    protected Stream<String> linesFromFile(String filename) throws Exception {
        return Files.lines(Paths.get(ClassLoader.getSystemResource(filename).toURI()));
    }

}
