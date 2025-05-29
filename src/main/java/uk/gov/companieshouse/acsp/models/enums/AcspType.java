package uk.gov.companieshouse.acsp.models.enums;

public enum AcspType {
    REGISTER_ACSP("register-acsp"),
    UPDATE_ACSP("update-acsp"),
    CLOSE_ACSP("close-acsp");

    public final String label;

    AcspType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static AcspType findByLabel(String label) {
        for (AcspType v : values()) {
            if (v.label.equals(label)) {
                return v;
            }
        }
        return null;
    }
}
