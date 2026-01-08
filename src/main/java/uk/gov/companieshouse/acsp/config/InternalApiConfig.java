package uk.gov.companieshouse.acsp.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.companieshouse.api.InternalApiClient;
import uk.gov.companieshouse.api.http.ApiKeyHttpClient;

import java.util.function.Supplier;

@Configuration
public class InternalApiConfig {

    @Bean
    public Supplier<InternalApiClient> internalApiClientSupplier(@Value("${chs.internal.api.key}") String apiKey, @Value("${chs.kafka.api.url}") String apiUrl) {
        return () -> {
            var internalApiClient = new InternalApiClient(new ApiKeyHttpClient(apiKey));
            internalApiClient.setBasePath(apiUrl);
            return internalApiClient;
        };
    }

}