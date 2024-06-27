package uk.gov.companieshouse.acsp.models.enums;

public enum BusinessSector {
    AIA("Auditors, insolvency practitioners, external accountants and tax advisers"),
    ILP("Independent legal professionals"),
    TCSP("Trust or company service providers"),
    CI("Credit institutions"),
    FI("Financial institutions"),
    EA("Estate agents"),
    HVD("High value dealers"),
    CASINOS("Casinos");

    public final String label;
    private BusinessSector(String label) {
        this.label = label;
    }
}



