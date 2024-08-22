package uk.gov.companieshouse.acsp.models.enums;

public enum TypeOfBusiness {
    LC("limited-company"),
    LP("limited-partnership"),
    LLP("limited-liability-partnership"),
    PARTNERSHIP("on-registered-partnership"),
    SOLE_TRADER("sole-trader"),
    UNINCORPORATED("unincorporated-entity"),
    CORPORATE_BODY("corporate-body");

    public final String label;
    private TypeOfBusiness(String label) {
        this.label = label;
    }

    //text
    public String getLabel() {
        return label;
    }

    //keys
    public static TypeOfBusiness findByLabel(String label){
        for(TypeOfBusiness v : values()){
            if( v.label.equals(label)){
                return v;
            }
        }
        return null;
    }
}
