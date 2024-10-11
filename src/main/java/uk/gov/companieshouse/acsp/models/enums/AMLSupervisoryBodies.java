package uk.gov.companieshouse.acsp.models.enums;

public enum AMLSupervisoryBodies {
    ACCA("association-of-chartered-certified-accountants (ACCA)"),
    AAT("association-of-accounting-technicians (AAT)"),
    AIA("association-of-international-accountants (AIA)"),
    ATT("association-of-taxation-technicians (ATT)"),
    CILEX("chartered-institute-of-legal-executives (CILEx)"),
    CIMA("chartered-institute-of-management-accountants (CIMA)"),
    CIOT("chartered-institute-of-taxation (CIOT)"),
    CLC("council-for-licensed-council-for-licensed (CLC)"),
    DENI("department-for-the-economy-northern-ireland"),
    FA("faculty-of-advocates"),
    FOAC("faculty-office-of-the-archbishop-of-canterbury"),
    FCA("financial-conduct-authority"),
    GC("gambling-commission"),
    GCB("general-council-of-the-bar"),
    GCBNI("general-council-of-the-bar-of-northern-ireland"),
    IAB("institute-of-accountants-and-bookkeepers (IAB)"),
    IPA("insolvency-practitioners-association (IPA)"),
    ICB("institute-of-certified-bookkeepers"),
    ICAEW("institute-of-chartered-accountants-in-england-and-wales (ICAEW)"),
    ICAI("institute-of-chartered-accountants-in-ireland (ICAI)"),
    ICAS("institute-of-chartered-accountants-of-scotland (ICAS)"),
    IFA("institute-of-financial-accountants (IFA)"),
    LCNI("law_society-of-northern-ireland"),
    LSS("law-society-of-scotland"),
    SRASEW("solicitors-regulation-authority (SRA) solicitors-in-england-and-wales"),
    HMRC("her-majesty's-revenue-and-customs");

    public final String label;

    private AMLSupervisoryBodies(String label) {
        this.label = label;
    }

    //value - association-of-chartered-certified-accountants
    public String getLabel() {
        return label;
    }




    //finds enum by value
    public static AMLSupervisoryBodies findByLabel(String label) {
        for (AMLSupervisoryBodies v : values()) {
            if (v.label.equals(label)) {
                return v;
            }
        }
        return null;
    }


}
