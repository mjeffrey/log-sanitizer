package be.sysa.log.sanitize.log4j2;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class Logit {
    public static void main(String[] args) {
       new Logit().doit();
    }

    private void doit() {
        log.error("xxx");
    }
}
