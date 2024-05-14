package uk.gov.companieshouse.acsp.models.enums;

public enum AMLSupervisoryBodies {
    ACCA("Association of Chartered Certified Accountants (ACCA)"),
    AAT("Association of Accounting Technicians (AAT)"),
    AIA("Association of International Accountants (AIA)"),
    ATT("Association of Taxation Technicians (ATT)"),
    CILEX("Chartered Institute of Legal Executives (CILEx)"),
    CIMA("Chartered Institute of Management Accountants (CIMA)"),
    CIOT("Chartered Institute of Taxation (CIOT)"),
    CLC("Council for Licensed Council for Licensed (CLC)"),
    DENI("Department for the Economy Northern Ireland"),
    FA("Faculty of Advocates"),
    FOAC("Faculty Office of the Archbishop of Canterbury"),
    FCA("Financial Conduct Authority "),
    GC("Gambling Commission"),
    GCB("General Council of the Bar"),
    GCBNI("General Council of the Bar of Northern Ireland"),
    IAB("Institute of Accountants and Bookkeepers (IAB)"),
    IPA("Insolvency Practitioners Association (IPA)"),
    ICB("Institute of Certified Bookkeepers"),
    ICAEW("Institute of Chartered Accountants in England and Wales (ICAEW)"),
    ICAI("Institute of Chartered Accountants in Ireland (ICAI)"),
    ICAS("Institute of Chartered Accountants of Scotland (ICAS)"),
    IFA("Institute of Financial Accountants (IFA)"),
    LCNI("Law Society of Northern Ireland"),
    LSS("Law Society of Scotland"),
    SRASEW("Solicitors Regulation Authority (SRA) Solicitors in England and Wales"),
    HMRC("Her Majesty's Revenue and Customs");

    public final String label;
    private AMLSupervisoryBodies(String label) {
        this.label = label;
    }
}
