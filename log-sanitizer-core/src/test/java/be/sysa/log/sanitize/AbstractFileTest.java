package be.sysa.log.sanitize;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

public abstract class AbstractFileTest {

    protected Stream<String> linesFromFile(String filename) throws Exception {
        return Files.lines(Paths.get(ClassLoader.getSystemResource(filename).toURI()));
    }

}
