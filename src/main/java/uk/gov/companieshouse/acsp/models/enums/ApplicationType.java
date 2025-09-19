package uk.gov.companieshouse.acsp.models.enums;

public enum ApplicationType {
    VERIFICATION("verification"),
    REVERIFICATION("reverification");

    public final String label;

    ApplicationType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static ApplicationType findByLabel(String label) {
        for (ApplicationType v : values()) {
            if (v.label.equals(label)) {
                return v;
            }
        }
        return VERIFICATION;
    }
}
