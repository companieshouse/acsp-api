package uk.gov.companieshouse.acsp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PaymentsConfig extends ApiConfig {
    @Value("${payments.url}")
    private String paymentsHost;

    public String getPaymentsHost() {
        return paymentsHost;
    }

    public void setPaymentsHost(String paymentsHost) {
        this.paymentsHost = paymentsHost;
    }
}
