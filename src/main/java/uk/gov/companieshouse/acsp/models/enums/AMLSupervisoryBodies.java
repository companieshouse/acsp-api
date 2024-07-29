package uk.gov.companieshouse.acsp.models.enums;

public enum AMLSupervisoryBodies {
    acca("Association of Chartered Certified Accountants (ACCA)"),
    aat("Association of Accounting Technicians (AAT)"),
    aia("Association of International Accountants (AIA)"),
    att("Association of Taxation Technicians (ATT)"),
    cilex("Chartered Institute of Legal Executives (CILEx)"),
    cima("Chartered Institute of Management Accountants (CIMA)"),
    ciot("Chartered Institute of Taxation (CIOT)"),
    clc("Council for Licensed Council for Licensed (CLC)"),
    deni("Department for the Economy Northern Ireland"),
    fa("Faculty of Advocates"),
    foac("Faculty Office of the Archbishop of Canterbury"),
    fca("Financial Conduct Authority "),
    gc("Gambling Commission"),
    gcb("General Council of the Bar"),
    gcbni("General Council of the Bar of Northern Ireland"),
    iab("Institute of Accountants and Bookkeepers (IAB)"),
    ipa("Insolvency Practitioners Association (IPA)"),
    icb("Institute of Certified Bookkeepers"),
    icaew("Institute of Chartered Accountants in England and Wales (ICAEW)"),
    icai("Institute of Chartered Accountants in Ireland (ICAI)"),
    icas("Institute of Chartered Accountants of Scotland (ICAS)"),
    ifa("Institute of Financial Accountants (IFA)"),
    lcni("Law Society of Northern Ireland"),
    lss("Law Society of Scotland"),
    srasew("Solicitors Regulation Authority (SRA) Solicitors in England and Wales"),
    hmrc("Her Majesty's Revenue and Customs");

    public final String label;
    private AMLSupervisoryBodies(String label) {
        this.label = label;
    }
}
