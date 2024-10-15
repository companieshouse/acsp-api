package uk.gov.companieshouse.acsp.models.enums;

public enum AMLSupervisoryBodies {
    ACCA("Association of Chartered Certified Accountants (ACCA)","association-of-chartered-certified-accountants"),
    AAT("Association of Accounting Technicians (AAT)","association-of-accounting-technicians"),
    AIA( "Association of International Accountants (AIA)","association-of-international-accountants"),
    ATT("Association of Taxation Technicians (ATT)","association-of-taxation-technicians"),
    CILEX("Chartered Institute of Legal Executives (CILEx)","chartered-institute-of-legal-executives"),
    CIMA("Chartered Institute of Management Accountants (CIMA)","chartered-institute-of-management-accountants"),
    CIOT("Chartered Institute of Taxation (CIOT)","chartered-institute-of-taxation"),
    CLC("Council for Licensed Conveyancers (CLC)","council-for-licensed-council-for-licensed"),
    DENI("Department for the Economy Northern Ireland","department-for-the-economy-northern-ireland"),
    FA("Faculty of Advocates","faculty-of-advocates"),
    FO("Faculty Office of the Archbishop of Canterbury","faculty-office-of-the-archbishop-of-canterbury"),
    FCA("Financial Conduct Authority (FCA)","financial-conduct-authority"),
    GC("Gambling Commission","gambling-commission"),
    GCB("General Council of the Bar","general-council-of-the-bar"),
    BONI("General Council of the Bar of Northern Ireland","general-council-of-the-bar-of-northern-ireland"),
    IAB("Institute of Accountants and Bookk   eepers (IAB)","institute-of-accountants-and-bookkeepers"),
    IPA("Insolvency Practitioners Association (IPA)","insolvency-practitioners-association"),
    ICB("Institute of Certified Bookkeepers","institute-of-certified-bookkeepers"),
    ICAEW("Institute of Chartered Accountants in England and Wales (ICAEW)","institute-of-chartered-accountants-in-england-and-wales"),
    ICAI("Institute of Chartered Accountants in Ireland (ICAI)","institute-of-chartered-accountants-in-ireland"),
    ICAS("Institute of Chartered Accountants of Scotland (ICAS)","institute-of-chartered-accountants-of-scotland"),
    IFA("Institute of Financial Accountants (IFA)","institute-of-financial-accountants"),
    LSNI("Law Society of Northern Ireland","law-society-of-northern-ireland"),
    LSS("Law Society of Scotland","law-society-of-scotland"),
    SRA("Solicitors Regulation Authority (SRA) Solicitors in England and Wales","solicitors-regulation-authority-solicitors-in-england-and-wales"),
    HMRC("HMRC","her-majesty's-revenue-and-customs");

    public final String label;
    public final String name;

    private AMLSupervisoryBodies(String name,String label) {
        this.name = name;
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
    public String getName() {
        return name;
    }

    public static AMLSupervisoryBodies findByLabel(String label) {
        for (AMLSupervisoryBodies v : values()) {
            if (v.label.equals(label)) {
                return v;
            }
        }
        return null;
    }

    public static AMLSupervisoryBodies findByName(String name) {
        for (AMLSupervisoryBodies v : values()) {
            if (v.name.equals(name)) {
                return v;
            }
        }
        return null;
    }
}
