package be.sysa.log.sanitize.log4j;

import lombok.extern.log4j.Log4j;

@Log4j
public class Logit {
    public static void main(String[] args) {
       new Logit().doit();
    }

    private void doit() {
        log.error("password=sfjsdhfwfw;1");

    }
}
