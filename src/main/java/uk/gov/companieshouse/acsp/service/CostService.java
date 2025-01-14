package uk.gov.companieshouse.acsp.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.model.payment.Cost;

import java.util.Collections;
import java.util.List;

@Service
public class CostService {

    @Value("${ACSP01_COST}")
    private String costAmount;
    @Value("${ACSP01_PAYMENT_METHODS}")
    private List<String> paymentMethods;
    private static final String COST_DESC = "Register ACSP fee";
    private static final String PAYMENT_ACCOUNT = "data-maintenance";

    public Cost getCosts() {
        var cost = new Cost();
        cost.setAmount(costAmount);
        cost.setAvailablePaymentMethods(paymentMethods);
        cost.setClassOfPayment(Collections.singletonList(PAYMENT_ACCOUNT));
        cost.setDescription(COST_DESC);
        cost.setDescriptionIdentifier("description-identifier");
        cost.setDescriptionValues(Collections.singletonMap("Key", "Value"));
        cost.setKind("payment-session#payment-session");
        cost.setResourceKind("register-acsp");
        cost.setProductType("register-acsp");

        return cost;
    }
}
