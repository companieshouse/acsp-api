package uk.gov.companieshouse.acsp.config;

import org.springframework.beans.factory.annotation.Value;

public class ApiConfig {
    @Value("${api.key}")
    private String apiKey;

    @Value("${api.url}")
    private String apiUrl;

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }
}
