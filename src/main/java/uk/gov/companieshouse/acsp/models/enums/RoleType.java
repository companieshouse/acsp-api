package uk.gov.companieshouse.acsp.models.enums;

public enum RoleType {
    sole_trader("Sole Trader"),
    member_of_partnership("Member of partnership"),
    member_of_governing_body("Member of the governing body"),
    equivalent_of_director("Equivalent to director"),
    member_of_entity("Member of the entity"),
    director("Director"),
    member_of_llp("Member of LLP"),
    general_partner("General partner"),
    someone_else("Someone else");

    public final String label;
    private RoleType(String label) {
        this.label = label;
    }
}
