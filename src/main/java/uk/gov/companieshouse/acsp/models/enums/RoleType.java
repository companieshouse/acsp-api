package uk.gov.companieshouse.acsp.models.enums;

public enum RoleType {
    SOLE_TRADER("sole-trader"),
    MEMBER_OF_PARTNERSHIP("member-of-partnership"),
    MEMBER_OF_GOVERNING_BODY("member-of-the-governing-body"),
    EQUIVALENT_OF_DIRECTOR("equivalent-to-director"),
    MEMBER_OF_ENTITY("member-of-the-entity"),
    DIRECTOR("director"),
    MEMBER_OF_LLP("member-of-LLP"),
    GENERAL_PARTNER("general-partner"),
    SOMEONE_ELSE("someone-else");

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
