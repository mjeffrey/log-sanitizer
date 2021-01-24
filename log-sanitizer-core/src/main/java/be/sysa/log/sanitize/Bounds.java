package be.sysa.log.sanitize;

import lombok.Value;
import lombok.experimental.Accessors;

import java.util.regex.Matcher;

@Value
@Accessors(fluent = true)
public class Bounds {
    int start;
    int end;

    public Bounds(Matcher matcher){
        start = matcher.start();
        end = matcher.end();
    }

    public Bounds(int start, int end) {
        this.start = start;
        this.end = end;
    }

    public int length() {
        return end - start;
    }
}
