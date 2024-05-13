package uk.gov.companieshouse.acsp.models.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AddressDto {

    @JsonProperty("property_details")
    private String propertyDetails;

    @JsonProperty("line1")
    private String line1;

    @JsonProperty("line2")
    private String line2;

    @JsonProperty("town")
    private String town;

    @JsonProperty("county")
    private String county;

    @JsonProperty("country")
    private String country;

    @JsonProperty("postcode")
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
}
