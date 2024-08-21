package uk.gov.companieshouse.acsp.models.enums;

public enum TypeOfBusiness {
    LC("Limited company"),
    LP("Limited partnership"),
    LLP("Limited Liability Partnership"),
    PARTNERSHIP("Non registered partnership"),
    SOLE_TRADER("Sole trader"),
    UNINCORPORATED("Unincorporated entity"),
    CORPORATE_BODY("Corporate body");

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
