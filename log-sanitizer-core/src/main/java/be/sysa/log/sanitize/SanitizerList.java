package be.sysa.log.sanitize;

import lombok.Builder;
import lombok.Singular;
import lombok.Value;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

@Value
public class SanitizerList {

    List<Sanitizer> sanitizers;

    @Builder
    public SanitizerList(@Singular("add") List<Sanitizer> sanitizers) {
        this.sanitizers = Collections.unmodifiableList(sanitizers);
    }

    public Map<String, Sanitizer> knownSanitizerMap() {
        return Collections.unmodifiableMap(sanitizers.stream()
                .collect(toMap(Sanitizer::id, identity())));
    }

    public void process(Buffer buffer, boolean mask){
        sanitizers.forEach(processor -> {
            try {
                processor.process(buffer, mask);
            } catch (Exception e) {
                // ignore it or prepend something on the buffer?
            }
        });
    }
}
