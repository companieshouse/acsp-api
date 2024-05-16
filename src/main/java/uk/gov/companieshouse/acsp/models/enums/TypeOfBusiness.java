package uk.gov.companieshouse.acsp.models.enums;

public enum TypeOfBusiness {
    LIMITED_COMPANY("Limited company"),
    LIMITED_PARTNERSHIP("Limited partnership"),
    LIMITED_LIABILITY_PARTNERSHIP("Limited Liability Partnership"),
    PARTNERSHIP("Non registered partnership"),
    SOLE_TRADER("Sole trader"),
    UNINCORPORATED_ENTITY("Unincorporated entity"),
    CORPORATE_BODY("Corporate body");

    public final String label;
    private TypeOfBusiness(String label) {
        this.label = label;
    }
}
