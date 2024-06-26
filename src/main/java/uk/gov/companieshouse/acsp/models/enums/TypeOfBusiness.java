package uk.gov.companieshouse.acsp.models.enums;

public enum TypeOfBusiness {
    LC("Limited company"),
    LP("Limited partnership"),
    LIMITED_LIABILITY("Limited Liability Partnership"),
    PARTNERSHIP("Non registered partnership"),
    SOLE_TRADER("Sole trader"),
    UNINCORPORATED("Unincorporated entity"),
    CORPORATE_BODY("Corporate body");

    public final String label;
    private TypeOfBusiness(String label) {
        this.label = label;
    }
}
