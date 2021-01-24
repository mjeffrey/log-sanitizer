package be.sysa.log.sanitize.logback;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class LoggerTest {
    @Test
    public void readFile() throws Exception{
        log.info("UUID: _286410cd-1042-4926-9423-2df7f8eec276");
        log.info("{ id='_390265a0-f9c8-4e13-a11b-5e4396641284'");


        log.info("Dec 12 19:23:29 turtle-i-0b84aa8d5c9e624c7.internal.production.pay-nxt.com ocs-turtle 2018-12-12 19:23:22.700  INFO          " +
                "[https-jsse-nio-8080-exec-1] c.u.o.t.s.webapp.service.bc.BepService   : BC BEP Transaction: 088af590-3b0e-474f-8d31-4fdef0e666ab, " +
                "received EPARes: BEP{Message=[MessageElem{ id='_390265a0-f9c8-4e13-a11b-5e4396641284', transferTransId='[]', extPayerAuthRequest='null', " +
                "extPayerAuthResponse='[EPAResElem{ id='_df30b012-035c-45a0-bbe4-b2a1dcf4fc71', " +
                "payeeData='[PayeeDataElem{ acquirerId='[700007]', payeeName='[]', merchantId='[000001012]', merchantCountryCode='[]', merchantUrl='[]'}]', purchaseData='[PurchaseDataElem{ transactionIdentifier='[OTBhNjgxYThiNDlhNDJlOGJhNDk=]', purchaseDate='[20181212 19:21:45]', purchaseAmount='[16229]', purchaseCurrency='[978]', currencyExponent='[2]', orderDescription='null'}]', transactionData='[TransactionDataElem{ signatureDate='[20181212 19:23:22]', transactionStatus='[Y]', bepaf='[jC8R7PONb1e7ATFcEWAqXcjIXLw=]', issuerCustomerRef='[]'}]', payerData='[PayerDataElem{ accountIdentifier='[39955435]', issuerId='[300005]'}]', notificationUrl='[https://prod-txm-psp.cwi.aw.atos.net/kernel-bcmc/rest/psp/notifyResult]', transactionType='[00]', transactionRoutingMeans='[50]', invalidData='[]', messageExtension='[]'}]', transResultNotificationReq='null', transResultNotificationRes='[]', payerAuthResultAck='null', error='[]'}]}.");
    }

}
