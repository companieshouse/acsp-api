package uk.gov.companieshouse.acsp.models.enums;

public enum TypeOfBusiness {
    lc("Limited company"),
    lp("Limited partnership"),
    llp("Limited Liability Partnership"),
    partnership("Non registered partnership"),
    sole_trader("Sole trader"),
    unincorporated("Unincorporated entity"),
    corporate_body("Corporate body");

    public final String label;
    private TypeOfBusiness(String label) {
        this.label = label;
    }
}
