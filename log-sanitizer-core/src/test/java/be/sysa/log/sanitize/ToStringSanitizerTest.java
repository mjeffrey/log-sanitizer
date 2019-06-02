package be.sysa.log.sanitize;

import lombok.ToString;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

public class ToStringSanitizerTest {

    private MessageSanitizer.ToStringSanitizer sanitizer = new MessageSanitizer.ToStringSanitizer();
    private MyClass myClass;

    @ToString
    private static class MyClass{
        private String username = "myuser";
        private String password = "mypassword";

        public String intellijToString() {
            return "MyClass{" +
                    "username='" + username + '\'' +
                    ", password='" + password + '\'' +
                    '}';
        }

        public String toStringBuilderString() {
            return new ToStringBuilder(this)
                    .append("username", username)
                    .append("password", password)
                    .toString();
        }
    }

    @Before
    public void setUp() throws Exception {
        myClass = new MyClass();
    }

    @Test
    public void sanitizeLombokToString() {
        Buffer buffer = bufferOf(myClass.toString());
        sanitizer.sanitize(buffer);
        assertThat(buffer.toString()).isEqualTo("ToStringSanitizerTest.MyClass(username=myuser, password=**********)");
    }
    @Test
    public void sanitizeToStringBuilderString() {
        Buffer buffer = bufferOf(myClass.toStringBuilderString());
        sanitizer.sanitize(buffer);
        assertThat(buffer.toString()).endsWith("[username=myuser,password=**********]");
    }
    @Test
    public void sanitizeIntellijString() {
        Buffer buffer = bufferOf(myClass.intellijToString());
        sanitizer.sanitize(buffer);
        assertThat(buffer.toString()).isEqualTo("MyClass{username='myuser', password=************}");
    }


    private Buffer bufferOf(Object map) {
        return new Buffer(Objects.toString(map.toString()));
    }
}
