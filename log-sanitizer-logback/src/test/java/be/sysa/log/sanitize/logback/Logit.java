package be.sysa.log.sanitize.logback;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;

@Slf4j
public class Logit {
    public static void main(String[] args) {
       new Logit().doit();
    }

    private void doit() {
        HashMap<String, String> map = new HashMap<>();
        map.put("username", "myuser");
        map.put("password", "myp3104e");
        log.error("A UUID {}, a Map {}", "23B8ECA5-F162-4E2A-AA02-259A41BB33C6", map, new RuntimeException("UUID should be  masked: 23B8ECA5-F162-4E2A-AA02-259A41BB33C6"));
    }
}
