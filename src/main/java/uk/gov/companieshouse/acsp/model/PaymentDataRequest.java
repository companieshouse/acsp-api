package uk.gov.companieshouse.acsp.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PaymentDataRequest {

    @JsonProperty("redirect_uri")
    private String redirectUri;

    private String reference;

    private String resource;

    private String state;

    public String getRedirectUri() {
        return redirectUri;
    }

    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
