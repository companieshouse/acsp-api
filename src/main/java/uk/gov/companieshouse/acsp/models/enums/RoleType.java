package uk.gov.companieshouse.acsp.models.enums;

public enum RoleType {
    SOLE_TRADER("Sole Trader"),
    MEMBER_OF_PARTNERSHIP("Member of partnership"),
    MEMBER_OF_GOVERNING_BODY("Member of the governing body"),
    EQUIVALENT_OF_DIRECTOR("Equivalent to director"),
    MEMBER_OF_ENTITY("Member of the entity"),
    DIRECTOR("Director"),
    MEMBER_OF_LLP("Member of LLP"),
    GENERAL_PARTNER("General partner"),
    SOMEONE_ELSE("Someone else");

    public final String label;
    private RoleType(String label) {
        this.label = label;
    }

    //text
    public String getLabel() {
        return label;
    }

    //keys
    public static RoleType findByLabel(String label){
        for(RoleType v : values()){
            if( v.label.equals(label)){
                return v;
            }
        }
        return null;
    }
}
