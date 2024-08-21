package uk.gov.companieshouse.acsp.models.enums;

public enum TypeOfBusiness {
    LC("Limited company"),
    LP("Limited partnership"),
    LLP("Limited Liability Partnership"),
    PARTNERSHIP("Non registered partnership"),
    SOLE_TRADER("Sole trader"),
    UNINCORPORATED("Unincorporated entity"),
    CORPORATE_BODY("Corporate body");

    public final String typeOfBusiness;

    TypeOfBusiness(String typeOfBusiness) {
        this.typeOfBusiness = typeOfBusiness;
    }

    /**
     *   getValue() returns the selected value (for e.g. Type of Business) from this enum list
     * @return selected value of typeOfBusiness
     */
    public String getValue() {
        return this.typeOfBusiness;
    }
}
