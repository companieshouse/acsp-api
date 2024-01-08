package uk.gov.companieshouse.acsp.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class PaymentsCosts {
    private String amount;

    @JsonProperty("available_payment_methods")
    private List<String> availablePaymentMethods;

    @JsonProperty("class_of_payment")
    private List<String> classOfPayment;

    private String description;

    @JsonProperty("description_identifier")
    private String descriptionIdentifier;

    @JsonProperty("product_type")
    private String productType;

    @JsonProperty("description_values")
    private String descriptionValues;

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public List<String> getAvailablePaymentMethods() {
        return availablePaymentMethods;
    }

    public void setAvailablePaymentMethods(List<String> availablePaymentMethods) {
        this.availablePaymentMethods = availablePaymentMethods;
    }

    public List<String> getClassOfPayment() {
        return classOfPayment;
    }

    public void setClassOfPayment(List<String> classOfPayment) {
        this.classOfPayment = classOfPayment;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescriptionIdentifier() {
        return descriptionIdentifier;
    }

    public void setDescriptionIdentifier(String descriptionIdentifier) {
        this.descriptionIdentifier = descriptionIdentifier;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getDescriptionValues() {
        return descriptionValues;
    }

    public void setDescriptionValues(String descriptionValues) {
        this.descriptionValues = descriptionValues;
    }
}
