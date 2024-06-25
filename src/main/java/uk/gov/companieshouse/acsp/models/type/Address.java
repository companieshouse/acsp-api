package uk.gov.companieshouse.acsp.models.type;

import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Objects;

public class Address {

    @Field("property_details")
    private String propertyDetails;

    @Field("line1")
    private String line1;

    @Field("line2")
    private String line2;

    @Field("town")
    private String town;

    @Field("county")
    private String county;

    @Field("country")
    private String country;

    @Field("postcode")
    private String postcode;

    public String getPropertyDetails() {
        return propertyDetails;
    }

    public void setPropertyDetails(String propertyDetails) {
        this.propertyDetails = propertyDetails;
    }

    public String getLine1() {
        return line1;
    }

    public void setLine1(String line1) {
        this.line1 = line1;
    }

    public String getLine2() {
        return line2;
    }

    public void setLine2(String line2) {
        this.line2 = line2;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Address address)) return false;
        return Objects.equals(propertyDetails, address.propertyDetails)
                && Objects.equals(line1, address.line1)
                && Objects.equals(line2, address.line2)
                && Objects.equals(town, address.town)
                && Objects.equals(county, address.county)
                && Objects.equals(country, address.country)
                && Objects.equals(postcode, address.postcode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(propertyDetails, line1, line2, town, county, country, postcode);
    }
}
