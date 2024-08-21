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

    public final String roleType;

    RoleType(String roleType) {
        this.roleType = roleType;
    }

    /**
     *   getValue() returns the selected value (for e.g. Role Type) from this enum list
     * @return selected value of roleType
     */
    public String getValue() {
        return this.roleType;
    }
}
