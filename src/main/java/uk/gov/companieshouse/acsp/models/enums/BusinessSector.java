package uk.gov.companieshouse.acsp.models.enums;

public enum BusinessSector {
    AIA("auditors, insolvency-practitioners, external-accountants-and-tax-advisers"),
    ILP("independent-legal-professionals"),
    TCSP("trust-or-company-service-providers"),
    CI("credit-institutions"),
    FI("financial-institutions"),
    EA("estate-agents"),
    HVD("high-value-dealers"),
    CASINOS("casinos");

    public final String businessSector;

    BusinessSector(String businessSector) {
        this.businessSector = businessSector;
    }

    /**
     *   getValue() returns the selected value (for e.g. Business Sector) from this enum list
     * @return selected value of business sector
     */
    public String getValue() {
        return this.businessSector;
    }

    //text
    public String getLabel() {
        return label;
    }

    //keys
    public static BusinessSector findByLabel(String label){
        for(BusinessSector v : values()){
            if( v.label.equals(label)){
                return v;
            }
        }
        return null;
    }
}



