package uk.gov.companieshouse.acsp.models.dto;

import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Objects;

public class AddressDto {

    @Field("premises")
    private String premises;

    @Field("addressLine1")
    private String addressLine1;

    @Field("addressLine2")
    private String addressLine2;

    @Field("locality")
    private String locality;

    @Field("region")
    private String region;

    @Field("country")
    private String country;

    @Field("postalCode")
    private String postalCode;

    public String getPremises() {
        return premises;
    }

    public void setPremises(String premises) {
        this.premises = premises;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AddressDto address)) return false;
        return Objects.equals(premises, address.premises)
                && Objects.equals(addressLine1, address.addressLine1)
                && Objects.equals(addressLine2, address.addressLine2)
                && Objects.equals(locality, address.locality)
                && Objects.equals(region, address.region)
                && Objects.equals(country, address.country)
                && Objects.equals(postalCode, address.postalCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(premises, addressLine1, addressLine2, locality, region, country, postalCode);
    }
}
