package uk.gov.companieshouse.acsp.configuration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import uk.gov.companieshouse.acsp.config.InterceptorConfig;
import uk.gov.companieshouse.acsp.interceptor.AuthenticationInterceptor;
import uk.gov.companieshouse.acsp.interceptor.LoggingInterceptor;
import uk.gov.companieshouse.acsp.interceptor.TransactionInterceptor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InterceptorConfigTest {

    @Mock
    private InterceptorRegistry interceptorRegistry;

    @Mock
    private InterceptorRegistration interceptorRegistration;

    @Mock
    private TransactionInterceptor transactionInterceptor;

    @Mock
    private LoggingInterceptor loggingInterceptor;

    @Mock
    private AuthenticationInterceptor authenticationInterceptor;

    @InjectMocks
    private InterceptorConfig interceptorConfig;

    @Test
    void addInterceptorsTest() {
        when(interceptorRegistry.addInterceptor(any())).thenReturn(interceptorRegistration);
        when(interceptorRegistration.addPathPatterns(any(String.class))).thenReturn(interceptorRegistration);
        interceptorConfig.addInterceptors(interceptorRegistry);

        InOrder inOrder = inOrder(interceptorRegistry, interceptorRegistration);

        // Transaction interceptor check
        inOrder.verify(interceptorRegistry).addInterceptor(loggingInterceptor);
        inOrder.verify(interceptorRegistry).addInterceptor(authenticationInterceptor);
        inOrder.verify(interceptorRegistry).addInterceptor(transactionInterceptor);
        inOrder.verify(interceptorRegistration).addPathPatterns(InterceptorConfig.TRANSACTIONS);
    }
}
