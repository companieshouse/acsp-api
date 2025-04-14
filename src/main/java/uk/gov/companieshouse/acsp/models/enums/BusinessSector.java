package uk.gov.companieshouse.acsp.models.enums;

public enum BusinessSector {
    AIP("auditors, insolvency-practitioners, external-accountants-and-tax-advisers"),
    ILP("independent-legal-professionals"),
    TCSP("trust-or-company-service-providers"),
    CI("credit-institutions"),
    FI("financial-institutions"),
    EA("estate-agents"),
    HVD("high-value-dealers"),
    CASINOS("casinos"),
    PNTS("prefer-not-to-say");

    public final String label;

    BusinessSector(String businessSector) {
        this.label = businessSector;
    }

    /**
     *   getValue() returns the selected value (for e.g. Business Sector) from this enum list
     * @return selected value of business sector
     */

    public String getValue() {
        return this.label;
    }

    //text
    public String getLabel() {
        return label;
    }

    //keys
    public static BusinessSector findByLabel(String label) {
        if (label == null) {
            return null;
        }
        if (label.trim().isEmpty()) {
            throw new IllegalArgumentException("Label cannot be empty");
        }
        for (BusinessSector v : values()) {
            if (v.label.equals(label)) {
                return v;
            }
        }
        return null;
    }
}



