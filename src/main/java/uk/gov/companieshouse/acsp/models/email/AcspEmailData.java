package uk.gov.companieshouse.acsp.models.email;

import uk.gov.companieshouse.email_producer.model.EmailData;

public class AcspEmailData extends EmailData {

    private String companyName;
    private String companyNumber;
    private String applicationReference;

    public AcspEmailData() {
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyNumber() {
        return companyNumber;
    }

    public void setCompanyNumber(String companyNumber) {
        this.companyNumber = companyNumber;
    }

    public String getApplicationReference() {
        return applicationReference;
    }

    public void setApplicationReference(String applicationReference) {
        this.applicationReference = applicationReference;
    }
}
