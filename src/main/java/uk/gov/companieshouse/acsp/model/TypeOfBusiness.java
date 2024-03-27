package uk.gov.companieshouse.acsp.model;

public enum TypeOfBusiness {
    LIMITED_COMPANY("Limited company"),
    LIMITED_PARTNERSHIP("Limited partnership"),
    LIMITED_LIABILITY_PARTNERSHIP("Limited Liability Partnership"),
    PARTNERSHIP("Non registered partnership"),
    SOLE_TRADER("Sole trader");

    public final String label;
    private TypeOfBusiness(String label) {
        this.label = label;
    }
}
