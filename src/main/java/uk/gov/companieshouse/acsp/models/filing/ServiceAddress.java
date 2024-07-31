package uk.gov.companieshouse.acsp.models.filing;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Class to hold the data for the address objects
 */
public final class ServiceAddress {

    @JsonProperty("isServiceAddressROA")
    private boolean isServiceAddressROA;
    @JsonProperty("address")
    private Address correspondenceAddress;

    public boolean isServiceAddressROA() {
        return isServiceAddressROA;
    }

    public void setServiceAddressROA(boolean serviceAddressROA) {
        isServiceAddressROA = serviceAddressROA;
    }

    public Address getCorrespondenceAddress() {
        return correspondenceAddress;
    }

    public void setCorrespondenceAddress(Address correspondenceAddress) {
        this.correspondenceAddress = correspondenceAddress;
    }
}
