package be.sysa.log.sanitize;

public interface Sanitizer {
    void process(Buffer buffer, boolean mask);
    String id();

    default void process(Buffer buffer){
        process(buffer, true);
    }

    default void retain(Buffer buffer){
        process(buffer, false);
    }
}
