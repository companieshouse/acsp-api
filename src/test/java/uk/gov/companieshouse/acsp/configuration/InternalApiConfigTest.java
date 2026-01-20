package uk.gov.companieshouse.acsp.configuration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import uk.gov.companieshouse.acsp.config.InternalApiConfig;
import uk.gov.companieshouse.api.InternalApiClient;

import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@TestPropertySource("classpath:application.properties")
class InternalApiConfigTest {

    @Value("${chs.internal.api.key}")
    private String apiKey;

    @Value("${chs.kafka.api.url}")
    private String apiUrl;

    @Test
    void testInternalApiClientSupplier() {
        // Arrange
        InternalApiConfig config = new InternalApiConfig();

        // Act
        Supplier<InternalApiClient> supplier = config.internalApiClientSupplier(apiKey, apiUrl);
        InternalApiClient client = supplier.get();

        // Assert
        assertEquals(apiUrl, client.getBasePath());
    }
}