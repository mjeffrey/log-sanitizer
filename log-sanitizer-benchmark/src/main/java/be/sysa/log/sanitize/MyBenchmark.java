package be.sysa.log.sanitize;

import be.sysa.log.sanitize.MessageSanitizer.*;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Warmup;

public class MyBenchmark {

    private static final SanitizerExample converter = new SanitizerExample();
    private static final String request = "Body: {\"scope\":\"https://api.paypal.com/v1/payments/.* https://uri.paypal.com/services/payments/refund https://uri.paypal.com/services/applications/webhooks https://uri.paypal.com/services/payments/payment/authcapture https://uri.paypal.com/payments/payouts https://api.paypal.com/v1/vault/credit-card/.* https://uri.paypal.com/services/disputes/read-seller https://uri.paypal.com/services/subscriptions https://uri.paypal.com/services/disputes/read-buyer https://api.paypal.com/v1/vault/credit-card openid https://uri.paypal.com/services/disputes/update-seller https://uri.paypal.com/services/payments/realtimepayment\",\"nonce\":\"2018-11-28T12:30:02ZT6yacUJ33wpfNm5I92Hhf8QJiFrpUSXlih-kCj4oWg8\",\"access_token\":\"A21AAHFyA9umVi9GeohYPnL9OK3jkD63TrAWW1\",\"token_type\":\"Bearer\",\"app_id\":\"APP-4VX56799PM6XXXX\",\"expires_in\":\"32400\"}";

    @Benchmark
    @Warmup(time = 3, iterations = 1)
    @Measurement(time = 3, iterations = 2)
    @Fork(1)
    public void testJson() {
        Buffer buffer = new Buffer(request);
        new JsonSanitizer().sanitize(buffer);
    }

    @Benchmark
    @Warmup(time = 3, iterations = 1)
    @Measurement(time = 3, iterations = 2)
    @Fork(1)
    public void testIban() {
        Buffer buffer = new Buffer(request);
        new IbanSanitizer().sanitize(buffer);
    }

    @Benchmark
    @Warmup(time = 3, iterations = 1)
    @Measurement(time = 3, iterations = 2)
    @Fork(1)
    public void testPan() {
        Buffer buffer = new Buffer(request);
        new PanSanitizer().sanitize(buffer);
    }

    @Benchmark
    @Warmup(time = 3, iterations = 1)
    @Measurement(time = 3, iterations = 2)
    @Fork(1)
    public void testUuid() {
        Buffer buffer = new Buffer(request);
        new UuidSanitizer().sanitize(buffer);
    }

    @Benchmark
    @Warmup(time = 3, iterations = 1)
    @Measurement(time = 3, iterations = 2)
    @Fork(1)
    public void testBase64() {
        Buffer buffer = new Buffer(request);
        new Base64Sanitizer().sanitize(buffer);
    }

    @Benchmark
    @Warmup(time = 3, iterations = 1)
    @Measurement(time = 3, iterations = 2)
    @Fork(1)
    public void testToString() {
        Buffer buffer = new Buffer(request);
        new ToStringSanitizer().sanitize(buffer);
    }

}
