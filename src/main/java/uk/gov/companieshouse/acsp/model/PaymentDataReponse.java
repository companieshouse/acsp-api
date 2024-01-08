package uk.gov.companieshouse.acsp.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Value;

import java.sql.Timestamp;
import java.util.List;

public class PaymentDataReponse {
    private String amount;

    @JsonProperty("completed_at")
    private Timestamp completedAt;

    @JsonProperty("created_at")
    private Timestamp createdAt;

    @JsonProperty("created_by")
    private CreatedBy createdBy;

    private  String description;

    private PaymentsLinks links;

    private String reference;

    @JsonProperty("company_number")
    private String companyNumber;

    private String status;

    private List<PaymentsCosts> costs;

    private String etag;

    private String kind;


    @JsonProperty("available_payment_methods")
    private List<String> availablePaymentMethods;

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

    public Timestamp getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(Timestamp completedAt) {
        this.completedAt = completedAt;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public CreatedBy getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(CreatedBy createdBy) {
        this.createdBy = createdBy;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public PaymentsLinks getLinks() {
        return links;
    }

    public void setLinks(PaymentsLinks links) {
        this.links = links;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getCompanyNumber() {
        return companyNumber;
    }

    public void setCompanyNumber(String companyNumber) {
        this.companyNumber = companyNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<PaymentsCosts> getCosts() {
        return costs;
    }

    public void setCosts(List<PaymentsCosts> costs) {
        this.costs = costs;
    }

    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

}
