package uk.gov.companieshouse.acsp.models.email;

import uk.gov.companieshouse.email_producer.model.EmailData;

public class ClientVerificationEmailData extends EmailData {
    private String clientName;
    private String referenceNumber;
    private String clientEmailAddress;

    // Getters and Setters
    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public String getClientEmailAddress() {
        return clientEmailAddress;
    }

    public void setClientEmailAddress(String clientEmailAddress) {
        this.clientEmailAddress = clientEmailAddress;
    }
}
