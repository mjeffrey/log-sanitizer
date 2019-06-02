package be.sysa.log.sanitize;

public interface Sanitizer {
    void sanitize(Buffer buffer);
    String id();
}
