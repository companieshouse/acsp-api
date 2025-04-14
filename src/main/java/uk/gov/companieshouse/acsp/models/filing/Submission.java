package uk.gov.companieshouse.acsp.models.filing;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public class Submission {

    @JsonProperty("received_at")
    private LocalDateTime receivedAt;

    @JsonProperty("transaction_id")
    private String transactionId;

    @JsonProperty("company_number")
    private String companyNumber;

    @JsonProperty("company_name")
    private String companyName;

    public LocalDateTime getReceivedAt() {
        return receivedAt;
    }

    public void setReceivedAt(LocalDateTime receivedAt) {
        this.receivedAt = receivedAt;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getCompanyNumber() { return companyNumber; }

    public void setCompanyNumber(String companyNumber) {
        this.companyNumber = companyNumber;
    }

    public String getCompanyName() { return companyName; }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
}
