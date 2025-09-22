package uk.gov.companieshouse.acsp.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import uk.gov.companieshouse.acsp.interceptor.AuthenticationInterceptor;
import uk.gov.companieshouse.acsp.interceptor.LoggingInterceptor;
import uk.gov.companieshouse.acsp.interceptor.TransactionInterceptor;

@Configuration
@ComponentScan("uk.gov.companieshouse.acsp.interceptor")
public class InterceptorConfig implements WebMvcConfigurer {

    public static final String TRANSACTIONS = "/transactions/**";
    public static final String PRIVATE_TRANSACTIONS = "/private/transactions/**";
    public static final String HEALTH_CHECK = "/*/healthcheck";

    private final TransactionInterceptor transactionInterceptor;
    private final LoggingInterceptor loggingInterceptor;
    private final AuthenticationInterceptor authenticationInterceptor;

    public InterceptorConfig(TransactionInterceptor transactionInterceptor,
                           LoggingInterceptor loggingInterceptor,
                           AuthenticationInterceptor authenticationInterceptor) {
        this.transactionInterceptor = transactionInterceptor;
        this.loggingInterceptor = loggingInterceptor;
        this.authenticationInterceptor = authenticationInterceptor;
    }

    /**
     * Set up the interceptors to run against endpoints when the endpoints are called
     * Interceptors are executed in the order they are added to the registry
     * @param registry The spring interceptor registry
     */
    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        addLoggingInterceptor(registry);
        addAuthenticationInterceptor(registry);
        addTransactionInterceptor(registry);
    }

    private void addLoggingInterceptor(InterceptorRegistry registry) {
        registry.addInterceptor(loggingInterceptor);
    }
    /**
     * Interceptor to authenticate requests to all endpoints except the health check endpoint
     * @param registry The spring interceptor registry
     */
    private void addAuthenticationInterceptor(InterceptorRegistry registry) {
        registry.addInterceptor(authenticationInterceptor)
                .excludePathPatterns(HEALTH_CHECK);
    }
    /**
     * Interceptor to get transaction and put in request for endpoints that require a transaction
     * @param registry The spring interceptor registry
     */
    private void addTransactionInterceptor(InterceptorRegistry registry) {
        registry.addInterceptor(transactionInterceptor)
                .addPathPatterns(TRANSACTIONS).addPathPatterns(PRIVATE_TRANSACTIONS);
    }
}
