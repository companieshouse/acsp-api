package uk.gov.companieshouse.acsp.models.enums;

public enum AMLSupervisoryBodies {
    ACCA("association-of-chartered-certified-accountants"),
    AAT("association-of-accounting-technicians"),
    AIA("association-of-international-accountants"),
    ATT("association-of-taxation-technicians"),
    CILEX("chartered-institute-of-legal-executives"),
    CIMA("chartered-institute-of-management-accountants"),
    CIOT("chartered-institute-of-taxation"),
    CLC("council-for-licensed-conveyancers"),
    DENI("department-for-the-economy-northern-ireland"),
    FA("faculty-of-advocates"),
    FO("faculty-office-of-the-archbishop-of-canterbury"),
    FCA("financial-conduct-authority"),
    GC("gambling-commission"),
    BSB("bar-standards-board"),
    BONI("general-council-of-the-bar-of-northern-ireland"),
    IAB("institute-of-accountants-and-bookkeepers"),
    IPA("insolvency-practitioners-association"),
    ICB("institute-of-certified-bookkeepers"),
    ICAEW("institute-of-chartered-accountants-in-england-and-wales"),
    ICAI("institute-of-chartered-accountants-in-ireland"),
    ICAS("institute-of-chartered-accountants-of-scotland"),
    IFA("institute-of-financial-accountants"),
    LSNI("law-society-of-northern-ireland"),
    LSS("law-society-of-scotland"),
    SRA("solicitors-regulation-authority-solicitors-in-england-and-wales"),
    HMRC("her-majesty's-revenue-and-customs");

    public final String label;

    AMLSupervisoryBodies(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static AMLSupervisoryBodies findByLabel(String label){
        if (label == null) {
            return null;
        }
        if (label.trim().isEmpty()) {
            throw new IllegalArgumentException("Label cannot be empty");
        }
        for (AMLSupervisoryBodies v : values()) {
            if (v.label.equals(label)) {
                return v;
            }
        }
        return null;
    }
}
