package uk.gov.companieshouse.acsp.models.enums;

public enum BusinessSector {
    aia("Auditors, insolvency practitioners, external accountants and tax advisers"),
    ilp("Independent legal professionals"),
    tcsp("Trust or company service providers"),
    ci("Credit institutions"),
    fi("Financial institutions"),
    ea("Estate agents"),
    hvd("High value dealers"),
    casinos("Casinos");

    public final String label;
    private BusinessSector(String label) {
        this.label = label;
    }
}



