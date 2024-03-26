package uk.gov.companieshouse.acsp.model;

public enum AMLSupervisoryBodies {
    SOLE_TRADER("Sole Trader"),
    DIRECTOR("Director"),
    GENERAL_PARTNER("General partner"),
    MEMBER("Member"),
    GOVERNING_BODY_MEMBER("Member of the governing body"),
    SOMEONE_ELSE("Someone else");

    public final String label;
    private AMLSupervisoryBodies(String label) {
        this.label = label;
    }
}
